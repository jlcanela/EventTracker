import sbt._
import Keys._
import PlayProject._

trait Resolvers {
  val typesafe = "typesafe.com" at "http://repo.typesafe.com/typesafe/releases/"
  val sonatype = "sonatype" at "http://oss.sonatype.org/content/repositories/releases"
}


trait Dependencies {

  object V {
    val akka = "2.0.2"
    val lift = "2.4-M5" 
  }

  val liftJson =  "net.liftweb" %% "lift-json"  % V.lift 
  val dispatch = "net.databinder.dispatch" %% "core" % "0.9.0-beta1"
  val jodaTime = "joda-time" % "joda-time" % "2.1"
  val jodaConvert = "org.joda" % "joda-convert" % "1.2"
  //val scalaz = "org.scalaz" %% "scalaz-core" % "7.0.0-M1" //TODO migrate to "7.0.0-M1" or "7.0.0" when available
  val scalaz = "org.scalaz" %% "scalaz-core" % "6.0.4"

  val sjsonapp = "net.debasishg" %% "sjsonapp" % "0.1"
  val rosettaJson = "com.reportgrid" %% "rosetta-json"  % "0.3.5"
  val casbah ="com.mongodb.casbah" %% "casbah" % "2.1.5-1"
  val bonecp = "com.jolbox" % "bonecp" % "0.7.1.RELEASE"
  val poi = "org.apache.poi" % "poi" % "3.7"
  val poiOoxml = "org.apache.poi" % "poi-ooxml" % "3.8"
  val jetty = "org.eclipse.jetty" % "jetty-webapp" % "7.5.4.v20111024" % "container,test->default"
  //  val specs = "org.scala-tools.testing" % "specs_2.9.0" % "1.6.8" % "test"
  val junit = "junit" % "junit" % "4.8" % "test->default" // For JUnit 4 testing
  val specs2 = "org.specs2" %% "specs2" % "1.11" % "test"
  val scalatest = "org.scalatest" %% "scalatest" % "1.7.2" % "test"
  val servletApi ="javax.servlet" % "servlet-api" % "2.5" % "provided->default"
  val h2 = "com.h2database" % "h2" % "1.2.138"
  val logback = "ch.qos.logback" % "logback-classic" % "1.0.0" % "compile->default"
  val slf4j = "org.slf4j" % "jcl-over-slf4j" % "1.6.4" 

}


object ApplicationBuild extends Build with Dependencies with Resolvers {

  val appName         = "eventtracker"
  val appVersion      = "1.0"

  val appDependencies = Seq(
  )

  val buildSettings = List(
    Seq(
      libraryDependencies in Test ++= Seq(junit, specs2, scalatest),
      libraryDependencies ++= Seq(jetty, servletApi),
      resolvers := Seq(typesafe, sonatype),
      shellPrompt := {
        (state: State) ⇒ "%s> ".format(Project.extract(state).currentProject.id)
      },
      scalacOptions := Seq("-deprecation", "-unchecked"),
      testOptions in Test += Tests.Argument("junitxml", "console")
      )
  ).foldLeft(Defaults.defaultSettings)(_ ++ _)


  val main = PlayProject(appName, appVersion, appDependencies, mainLang = SCALA).settings(
    ivyConfigurations += config("container"),    
    libraryDependencies ++= Seq(liftJson, dispatch, jodaTime, jodaConvert, scalaz, sjsonapp, rosettaJson, casbah,
      bonecp, poi, poiOoxml, jetty, servletApi, h2, logback, slf4j, scalaz)
    
  )


}
