// Comment to get more information during initialization
logLevel := Level.Warn

// The Typesafe repository 
resolvers += "Typesafe repository" at "http://repo.typesafe.com/typesafe/releases/"

addSbtPlugin("com.github.scct" % "sbt-scct" % "0.2")

addSbtPlugin("com.github.theon" %% "xsbt-coveralls-plugin" % "0.0.2")

// Use the Play sbt plugin for Play projects
addSbtPlugin("play" % "sbt-plugin" % "2.1.2")
