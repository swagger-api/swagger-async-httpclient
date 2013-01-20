import sbt._
import Keys._
import sbtrelease.ReleasePlugin._
import com.wordnik.sbt.common.Settings
import com.wordnik.sbt.common.Dependencies._
import sbtbuildinfo.Plugin._

object SwaggerAsyncBuild extends Build {
  val projectSettings = Seq(
    organization := "com.wordnik.swagger",
    name := "swagger-async-httpclient",
    scalaVersion := "2.10.0",
    crossScalaVersions := Seq("2.9.1", "2.9.1-1", "2.9.2", "2.10.0"),
    scalacOptions in Compile <++= scalaVersion map ({
      case v if v startsWith "2.10" => Seq("-language:implicitConversions", "-language:reflectiveCalls")
      case _ => Seq.empty
    }),
    buildInfoPackage := "com.wordnik.swagger.client.async"
  )

  val defaultSettings =
    Defaults.defaultSettings ++ 
    releaseSettings ++ 
    Settings.defaultSettings ++ 
    Settings.publishSettings ++ 
    Settings.defaultLibrarySettings ++
    Settings.buildInfoConfig ++ 
    projectSettings


  lazy val root = Project(
    id = "swagger-async-httpclient",
    base = file("."),
    settings = defaultSettings ++ Seq(
      libraryDependencies ++= Seq(
        utils("uri"),
        "org.json4s" %% "json4s-jackson" % "3.1.0",
        jUniversalChardet,
        "eu.medsea.mimeutil" % "mime-util" % "2.1.3" exclude("org.slf4j", "slf4j-log4j12") exclude("log4j", "log4j"),
        "com.ning" % "async-http-client" % "1.7.9"
      ),
      Settings.versionSpecificSourcesIn(Compile)
    )
  )
}