name := "EventTracker"
 
scalaVersion := "2.9.1"
 
//resolvers += "Java.net Maven2 Repository" at "http://download.java.net/maven/2/"
//resolvers += "releases" at "http://oss.sonatype.org/content/repositories/releases"  

resolvers += "Typesafe Repository" at "http://repo.typesafe.com/typesafe/releases/"

scalacOptions ++= Seq("-encoding", "UTF-8", "-deprecation", "-unchecked")

//seq(webSettings :_*)

parallelExecution in Test := false

// fullRunInputTask(InputKey[Unit]("myCustomTaskName"), Runtime, "package.MyClass")
