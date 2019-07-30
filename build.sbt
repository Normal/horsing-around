name := "horsing-around"

version := "0.1"

scalaVersion := "2.12.8"

libraryDependencies ++= Seq(
  // logging
  "ch.qos.logback" % "logback-classic" % "1.2.3",
  "com.typesafe.scala-logging" %% "scala-logging" % "3.9.2",

  // tests
  "org.scalactic" %% "scalactic" % "3.0.8",
  "org.scalatest" %% "scalatest" % "3.0.8" % "test",
  
  // json
  "org.json4s" %% "json4s-native" % "3.6.7",
  "org.json4s" %% "json4s-jackson" % "3.6.7"
)

test in assembly := {}
assemblyJarName in assembly := s"${name.value}.jar"
