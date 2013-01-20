addSbtPlugin("com.github.gseitz" % "sbt-release" % "0.6")

resolvers += Resolver.url("Wordnik Ivy", url("https://ci.aws.wordnik.com/artifactory/ivy"))(Resolver.ivyStylePatterns)

addSbtPlugin("com.wordnik.sbt" % "wordnik-sbt-common" % "latest.integration")