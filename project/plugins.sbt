
resolvers += "Web plugin repo" at "http://siasia.github.com/maven2"

libraryDependencies <+= sbtVersion(v => "com.github.siasia" %% "xsbt-web-plugin" % (v+"-0.2.11"))

addSbtPlugin("com.typesafe.sbteclipse" % "sbteclipse-plugin" % "2.0.0")

resolvers ++= Seq(
  "sonatype-public" at "https://oss.sonatype.org/content/groups/public",
  "repo.codahale.com" at "http://repo.codahale.com")
