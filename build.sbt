val pegasusVersion = "24.0.2"
val specs2Version = "3.9.4"
val log4jVersion = "2.8.1"

// Starting with sbt 1.0, log4j is included with sbt
def log4jDependencies(sbtVersion: String): Seq[ModuleID] = {
  if (sbtVersion == "0.13") Seq(
    "org.apache.logging.log4j" % "log4j-api" % log4jVersion,
    "org.apache.logging.log4j" % "log4j-core" % log4jVersion,
    "org.apache.logging.log4j" % "log4j-slf4j-impl" % log4jVersion
  ) else Nil
}

val repoUrl = url("https://github.com/linkedin/sbt-restli")

inThisBuild(Seq(
  licenses := Seq(("Apache-2.0", url("https://github.com/linkedin/sbt-restli/blob/master/LICENSE.TXT"))),
  homepage := Some(repoUrl),
  scmInfo := Some(ScmInfo(repoUrl, "scm:git:git@github.com:linkedin/sbt-restli.git")),
  developers := List(Developer("TylerHorth", "Tyler Horth", "tylerhorth@outlook.com", url("https://github.com/TylerHorth"))),
  organization := "com.linkedin.sbt-restli",
  pgpPublicRing := file("./travis/local.pubring.asc"),
  pgpSecretRing := file("./travis/local.secring.asc")
))

lazy val sbtRestli = (project in file("sbt-restli"))
  .enablePlugins(SbtPlugin)
  .dependsOn(restliToolsScala)
  .settings(
    name := "sbt-restli",
    crossSbtVersions := Seq("0.13.17", "1.2.6"),
    crossScalaVersions := Seq("2.10.7", "2.12.7"),
    scriptedLaunchOpts ++= Seq("-Xmx1024M", "-Dplugin.version=" + version.value),
    scripted := scripted.dependsOn(publishLocal in restliToolsScala).evaluated,
    releaseEarlyEnableSyncToMaven := false,
    bintrayOrganization := Some("sbt-restli"),
    releaseEarlyWith := BintrayPublisher,
    resolvers += Resolver.sonatypeRepo("releases"),
    libraryDependencies ++= Seq(
      "com.linkedin.pegasus" % "generator" % pegasusVersion,
      "com.linkedin.pegasus" % "restli-tools" % pegasusVersion,
      "com.linkedin.pegasus" % "data-avro-generator" % pegasusVersion
    ),
    libraryDependencies ++= log4jDependencies((sbtBinaryVersion in pluginCrossBuild).value)
  )

lazy val restliToolsScala = (project in file("restli-tools-scala"))
  .settings(
    name := "restli-tools-scala",
    crossScalaVersions := Seq("2.10.7", "2.11.12", "2.12.7"),
    releaseEarlyWith := SonatypePublisher,
    // Do not remove this line or tests break. Sbt mangles the java.class.path system property unless forking is enabled :(
    fork in Test := true,
    libraryDependencies ++= Seq(
      "org.specs2" %% "specs2-core" % specs2Version % Test,
      "org.specs2" %% "specs2-matcher-extra" % specs2Version % Test,
      "com.linkedin.pegasus" % "restli-int-test-api" % pegasusVersion % Test classifier "all",
      "com.linkedin.pegasus" % "restli-server" % pegasusVersion,
      "org.scala-lang" % "scala-compiler" % scalaVersion.value,
    )
  )
