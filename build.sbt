name := "paperBoy"

version := "1.0"

scalaVersion := "2.11.7"

libraryDependencies ++= Seq(
  "com.ning" % "async-http-client" % "1.7.1",
  "com.typesafe.akka" %% "akka-actor" % "2.3.14",
  "com.typesafe.slick" % "slick_2.11" % "3.1.1",
  "com.typesafe.slick" %% "slick-hikaricp" % "3.1.1"
    exclude("com.zaxxer", "HikariCP-java6"),
  "com.zaxxer" % "HikariCP" % "2.4.1",
  "org.slf4j" % "slf4j-nop" % "1.6.4",
  "org.mariadb.jdbc" % "mariadb-java-client" % "1.4.5",
  "org.jsoup" % "jsoup" % "1.8.3"
)
resourceDirectory in Compile := baseDirectory.value / "src" / "main" / "resources"