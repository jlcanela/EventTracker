import sbt._
import Keys._
import PlayProject._

object ApplicationBuild extends Build {

  val appName         = "eventtracker"
  val appVersion      = "1.0"

  val appDependencies = Seq(
  )

  val main = PlayProject(appName, appVersion, appDependencies, mainLang = SCALA).settings(
    // You can add additionnal project settings here
  )


}
