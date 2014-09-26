name := """sicamp36beautiful"""

version := "1.0-SNAPSHOT"

lazy val root = (project in file(".")).enablePlugins(PlayScala)

scalaVersion := "2.10.4"

libraryDependencies ++= Seq(
  ws, // Play's web services module"org.scala-lang" %% "scala-pickling" % "0.8.0",
  "org.slf4j" % "slf4j-api" % "1.7.2",
  "ch.qos.logback" % "logback-classic" % "1.0.9",
  "com.oracle" % "ojdbc6" % "11.2.0",
  "com.typesafe.akka" %% "akka-actor" % "2.3.4",
  "com.typesafe.akka" %% "akka-slf4j" % "2.3.4",
  "com.google.code.findbugs" % "jsr305" % "2.0.3",
  "c3p0" % "c3p0" % "0.9.1.2",
  "redis.clients" % "jedis" % "2.2.1",
  "org.webjars" % "bootstrap" % "2.3.1",
  "org.webjars" % "flot" % "0.8.0",
  "com.google.guava" % "guava" % "18.0",
  "mysql" % "mysql-connector-java" % "5.1.32",
  "com.alibaba" % "fastjson" % "1.1.41",
  "org.json4s" %% "json4s-jackson" % "3.2.10",
  "org.json4s" %% "json4s-native" % "3.2.10",
  "org.json4s" %% "json4s-ext" % "3.2.10",
  "com.typesafe.slick" %% "slick" % "2.1.0",
  "com.github.nscala-time" %% "nscala-time" % "1.4.0",
  "org.joda" % "joda-convert" % "1.6",
  "com.github.tototoshi" %% "slick-joda-mapper" % "1.2.0",
  "org.scalatest" %% "scalatest" % "2.1.6" % "test",
  "com.typesafe.akka" %% "akka-testkit" % "2.3.4" % "test",
  "org.scalatestplus" % "play_2.10" % "1.0.0" % "test"
)
