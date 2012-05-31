name := "EventTracker"
 
scalaVersion := "2.9.1"
 
seq(webSettings :_*)

resolvers += "Java.net Maven2 Repository" at "http://download.java.net/maven/2/"

resolvers += "releases" at "http://oss.sonatype.org/content/repositories/releases"  

resolvers += "Typesafe Repository" at "http://repo.typesafe.com/typesafe/releases/"

scalacOptions ++= Seq("-encoding", "UTF-8", "-deprecation", "-unchecked")


libraryDependencies ++= {
  val liftVersion = "2.4-M5" 
 Seq(
    "net.liftweb" %% "lift-json"  % liftVersion % "compile->default"
//    "net.liftweb" %% "lift-webkit"  % liftVersion % "compile->default",
//    "net.liftweb" %% "lift-util"  % liftVersion % "compile->default" withSources(),    
//    "net.liftweb" %% "lift-squeryl-record"  % liftVersion % "compile->default" withSources(),    
//    "net.liftweb" %% "lift-proto"  % liftVersion % "compile->default" withSources()    
  )    
}

// Customize any further dependencies as desired
libraryDependencies ++= Seq(
  "joda-time" % "joda-time" % "2.1",  
  "org.joda" % "joda-convert" % "1.2",
  "org.scalaz" %% "scalaz-core" % "6.0.4",
  "net.debasishg" %% "sjsonapp" % "0.1" withSources(),
  "com.reportgrid" %% "rosetta-json"  % "0.3.5" % "compile",  
//  "se.scalablesolutions.akka" % "akka-actor" % "1.3.1",
//  "se.scalablesolutions.akka" % "akka-remote" % "1.3.1",
  "com.mongodb.casbah" %% "casbah" % "2.1.5-1",
  "scalaz.camel" %% "scalaz-camel-akka" % "0.4-SNAPSHOT" withSources(),
  "org.apache.camel" % "camel-spring" % "2.9.1",
  "org.apache.camel" % "camel-jms" % "2.9.1",
  "org.apache.camel" % "camel-http" % "2.9.1",
  "org.apache.camel" % "camel-jetty" % "2.9.1",
  "org.apache.camel" % "camel-netty" % "2.9.1",
  "org.apache.activemq" % "activemq-core" % "5.5.0",
  "com.jolbox" % "bonecp" % "0.7.1.RELEASE" % "compile->default",  
  "org.apache.poi" % "poi" % "3.7" withSources(),
  "org.apache.poi" % "poi-ooxml" % "3.8" withSources(),
  "org.eclipse.jetty" % "jetty-webapp" % "7.5.4.v20111024" % "container,test->default",
  "org.scala-tools.testing" % "specs_2.9.0" % "1.6.8" % "test", // For specs.org tests
  "junit" % "junit" % "4.8" % "test->default", // For JUnit 4 testing
  "org.scalatest" %% "scalatest" % "1.7.2" % "test",
  "javax.servlet" % "servlet-api" % "2.5" % "provided->default",
  "com.h2database" % "h2" % "1.2.138", // In-process database, useful for development systems
  "ch.qos.logback" % "logback-classic" % "1.0.0" % "compile->default",
  "org.slf4j" % "jcl-over-slf4j" % "1.6.4" // only used for debugging.
)

