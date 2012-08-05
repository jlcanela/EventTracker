// Comment to get more information during initialization
logLevel := Level.Warn

// The Typesafe repository 
resolvers += Resolver.url("Typesafe Ivy Releases Repository", url("http://repo.typesafe.com/typesafe/ivy-releases/"))(Resolver.ivyStylePatterns)

// Use the Play sbt plugin for Play projects
addSbtPlugin("play" % "sbt-plugin" % "2.0.3")

addSbtPlugin("com.github.mpeltonen" % "sbt-idea" % "1.1.0-M2-TYPESAFE") 


