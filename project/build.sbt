name := "finloader_build"

version := "1.0"

scalaVersion := "2.10.3"

libraryDependencies ++= List(
  "com.typesafe.slick" %% "slick" % "2.0.0",
  "com.typesafe" % "config" % "1.0.2",
  "org.slf4j" % "slf4j-nop" % "1.6.4",
//  "com.h2database" % "h2" % "1.3.166",
  "postgresql" % "postgresql" % "9.1-901.jdbc4"
)

