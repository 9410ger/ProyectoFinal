
name := "Final"

version := "0.1"

scalaVersion := "2.13.10"

libraryDependencies += "ch.qos.logback" % "logback-classic" % "1.2.3"
libraryDependencies += "org.postgresql" % "postgresql" % "42.2.23"
libraryDependencies += "org.apache.kafka" % "kafka-clients" % "3.0.0"
libraryDependencies += "org.apache.kafka" %% "kafka-streams-scala" % "3.0.0"
libraryDependencies += "com.typesafe.scala-logging" %% "scala-logging" % "3.9.4"
libraryDependencies += "com.fasterxml.jackson.core" % "jackson-databind" % "2.14.0"
libraryDependencies += "com.fasterxml.jackson.module" %% "jackson-module-scala" % "2.14.1"
libraryDependencies += "org.apache.commons" % "commons-dbcp2" % "2.9.0"