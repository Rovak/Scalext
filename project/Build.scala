import sbt._
import Keys._
import play.Project._

object ApplicationBuild extends Build {

  val appName         = "scalaext"
  val appVersion      = "0.1.1"

  val appDependencies = Seq(
    jdbc,
    anorm,
    "commons-io" % "commons-io" % "2.1",
    "org.apache.commons" % "commons-lang3" % "3.1",
    "com.google.code.gson" % "gson" % "2.2.3"
  )


  val main = play.Project(appName, appVersion, appDependencies).settings(

  )

}
