val pegasusVersion = settingKey[String]("Restli / Pegasus version number")

val root = (project in file("."))
  .enablePlugins(SbtPlugin, BuildInfoPlugin)
  .dependsOn(restliToolsScala)
  .settings(
    name := "sbt-restli",
    version := "0.3.0",
    organization := "com.linkedin.pegasus",
    crossSbtVersions := Seq("1.2.3", "0.13.17"),
    pegasusVersion := "24.0.2",
    buildInfoPackage := "sbtrestli",
    buildInfoKeys += pegasusVersion,
    libraryDependencies ++= Seq(
      "com.linkedin.pegasus" % "generator" % pegasusVersion.value,
    )
  )

lazy val restliToolsScala = (project in file("restli-tools-scala"))
  .settings(
    name := "restli-tools-scala",
    version := "24.0.2",
    organization := "com.linkedin.pegasus",
    crossScalaVersions := Seq("2.10.7", "2.12.6"),
    // Do not remove this line or tests break. Sbt mangles the java.class.path system property unless forking is enabled :(
    fork in Test := true,
    libraryDependencies ++= Seq(
      "org.specs2" %% "specs2-core" % "3.9.4" % Test,
      "org.specs2" %% "specs2-matcher-extra" % "3.9.4" % Test,
      "com.linkedin.pegasus" % "restli-int-test-api" % "24.0.2" % Test classifier "all",
      "com.linkedin.pegasus" % "restli-server" % "24.0.2",
      "org.scala-lang" % "scala-compiler" % scalaVersion.value,
    )
  )