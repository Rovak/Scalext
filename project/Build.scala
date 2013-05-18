import sbt._
import Keys._
import play.Project._

object ApplicationBuild extends Build {

  val appName         = "scalaextjs"
  val appVersion      = "1.0-SNAPSHOT"

  val appDependencies = Seq(
    // Add your project dependencies here,
    jdbc,
    anorm
  )


  val main = play.Project(appName, appVersion, appDependencies).settings(
    // Add your own project settings here      
    scalaVersion := "2.10.1"
  )

}
