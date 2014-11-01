import sbt._
import Keys._
import sbtrelease.ReleasePlugin._
import sbtbuildinfo.Plugin._
import scala.xml.Group

object build extends Build {

  val manifestSetting = packageOptions <+= (name, version, organization) map {
    (title, version, vendor) =>
      Package.ManifestAttributes(
        "Created-By" -> "Simple Build Tool",
        "Built-By" -> System.getProperty("user.name"),
        "Build-Jdk" -> System.getProperty("java.version"),
        "Specification-Title" -> title,
        "Specification-Version" -> version,
        "Specification-Vendor" -> vendor,
        "Implementation-Title" -> title,
        "Implementation-Version" -> version,
        "Implementation-Vendor-Id" -> vendor,
        "Implementation-Vendor" -> vendor)
  }

  val publishSettings: Seq[Setting[_]] = Seq(
    publishTo <<= (version) { version: String =>
      val res =
        if (version.trim.endsWith("SNAPSHOT"))
          Opts.resolver.sonatypeSnapshots
        else
          Opts.resolver.sonatypeStaging
      Some(res)
    },
    publishMavenStyle := true,
    publishArtifact in Test := false,
    pomIncludeRepository := { x => false }
  )

  val mavenCentralFrouFrou = Seq(
    homepage := Some(new URL("https://developers.helloreverb.com/swagger/")),
    startYear := Some(2009),
    licenses := Seq(("ASL", new URL("http://github.com/wordnik/swagger-async-httpclient/raw/HEAD/LICENSE"))),
    pomExtra <<= (pomExtra, name, description) {(pom, name, desc) => pom ++ Group(
      <scm>
        <url>http://github.com/wordnik/swagger-async-httpclient</url>
        <connection>scm:git:git://github.com/wordnik/swagger-async-httpclient.git</connection>
      </scm>
      <developers>
        <developer>
          <id>casualjim</id>
          <name>Ivan Porto Carrero</name>
          <url>http://flanders.co.nz/</url>
        </developer>
      </developers>
    )}
  )

  def versionSpecificSourcesIn(c: Configuration) =
    unmanagedSourceDirectories in c <+= (scalaVersion, sourceDirectory in c) {
      case (v, dir) if v startsWith "2.9" => dir / "scala_2.9"
      case (v, dir) if v startsWith "2.10" => dir / "scala_2.10"
      case (v, dir) if v startsWith "2.11" => dir / "scala_2.10"
    }

  val projectSettings = Seq(
    organization := "com.wordnik.swagger",
    name := "swagger-async-httpclient",
    scalaVersion := "2.10.4",
    crossScalaVersions := Seq("2.9.1", "2.9.1-1", "2.9.2", "2.9.3", "2.10.4", "2.11.2"),
    scalacOptions ++= Seq("-unchecked", "-deprecation", "-optimize", "-Xcheckinit", "-encoding", "utf8", "-P:continuations:enable"),
    scalacOptions in Compile <++= scalaVersion map ({
      case v if v startsWith "2.10" => Seq("-language:implicitConversions", "-language:reflectiveCalls")
      case _ => Seq.empty
    }),
    javacOptions in compile ++= Seq("-target", "1.6", "-source", "1.6", "-Xlint:deprecation"),
    manifestSetting,
    autoCompilerPlugins := true,
    libraryDependencies <+= scalaVersion(sv => compilerPlugin("org.scala-lang.plugins" % "continuations" % sv)),
    parallelExecution in Test := false,
    commands += Command.args("s", "<shell command>") { (state, args) =>
      args.mkString(" ") ! state.log
      state
    },
    TaskKey[Unit]("gc", "runs garbage collector") <<= streams map { s =>
      s.log.info("requesting garbage collection")
      System.gc()
    }
  )

  val buildInfoConfig: Seq[Setting[_]] = buildInfoSettings ++ Seq(
    sourceGenerators in Compile <+= buildInfo,
    buildInfoKeys := Seq[BuildInfoKey](name, version, scalaVersion, sbtVersion),
    buildInfoPackage <<= organization(_ + ".client.async")
  )

  val defaultSettings =
    Defaults.defaultSettings ++ releaseSettings ++ buildInfoConfig ++ projectSettings ++ publishSettings ++ mavenCentralFrouFrou


  lazy val root = Project(
    id = "swagger-async-httpclient",
    base = file("."),
    settings = defaultSettings ++ Seq(
      libraryDependencies ++= Seq(
        "org.scalatra.rl" %% "rl" % "0.4.10",
        "com.google.guava" % "guava" % "18.0",
        "org.slf4j" % "slf4j-api" % "1.7.7",
        "ch.qos.logback" % "logback-classic" % "1.1.2" % "provided",
        "org.json4s" %% "json4s-jackson" % "3.2.11",
        "com.googlecode.juniversalchardet" % "juniversalchardet" % "1.0.3",
        "eu.medsea.mimeutil" % "mime-util" % "2.1.3" exclude("org.slf4j", "slf4j-log4j12") exclude("log4j", "log4j"),
        "com.ning" % "async-http-client" % "1.8.14"
      ),
      libraryDependencies <+= scalaVersion {
         case "2.9.3" => "org.clapper" % "grizzled-slf4j_2.9.2" % "0.6.10" exclude("org.scala-lang", "scala-library")
         case v if v startsWith "2.9" => "org.clapper" %% "grizzled-slf4j" % "0.6.10"
         case v => "com.typesafe" %% "scalalogging-slf4j" % "1.1.0"
      },
      libraryDependencies <++= scalaVersion {
        case v if v startsWith "2.9" => Seq("com.typesafe.akka" % "akka-actor" % "2.0.5")
        case v => Seq.empty
      },
      resolvers <++= scalaVersion {
        case v if v startsWith "2.9" => Seq("Typesafe Repository" at "http://repo.typesafe.com/typesafe/releases/")
        case v => Seq.empty
      },
      versionSpecificSourcesIn(Compile)
    )
  )
}