name := "paperBoy"

version := "1.0"

scalaVersion := "2.11.7"

libraryDependencies ++= Seq(
  "com.ning" % "async-http-client" % "1.7.1",
  "com.typesafe.akka" %% "akka-actor" % "2.3.14",
  "com.typesafe.slick" % "slick_2.11" % "3.1.1",
  "org.slf4j" % "slf4j-nop" % "1.6.4"


)