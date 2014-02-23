import sbt._
import Keys._

object ApplicationBuild extends Build {

  val appName         = "scalext"
  val projectVersion = "0.1-SNAPSHOT"

  val defaultSettings = Project.defaultSettings ++ Seq(
    resolvers += "Typesafe Repository" at "http://repo.typesafe.com/typesafe/releases/",
    version :=  "0.1-SNAPSHOT",
    scalaVersion := "2.10.2")

  val main = Project(
    id = appName,
    base = file("scalext"),
    settings = defaultSettings ++ Seq(
      libraryDependencies ++= Seq(
        "commons-io" % "commons-io" % "2.1",
        "org.apache.commons" % "commons-lang3" % "3.1",
        "com.google.code.gson" % "gson" % "2.2.3",
        "com.typesafe.play" %% "play-json" % "2.2.1",
        "com.typesafe" %% "config" % "1.2.0"
      )
    ))

}
