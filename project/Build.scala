import sbt._
import Keys._
import play.Project._

object ApplicationBuild extends Build {

  val appName         = "scalaextjs"
  val appVersion      = "1.0-SNAPSHOT"

  val appDependencies = Seq(
    jdbc,
    anorm,
    "commons-io" % "commons-io" % "2.1",
    "org.apache.commons" % "commons-lang3" % "3.1",
    "com.google.code.gson" % "gson" % "2.2.3"
  )


  val main = play.Project(appName, appVersion, appDependencies).settings(      
    scalaVersion := "2.10.1"
  )

}
