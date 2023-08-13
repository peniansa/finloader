import sbtassembly.Plugin._
import AssemblyKeys._

name := "finloader"

version := "1.0"

//scalaVersion := "2.10.3"
scalaVersion := "2.11.4"

resolvers += "Sonatype snapshots" at "http://oss.sonatype.org/content/repositories/snapshots/"


libraryDependencies ++= List(
  "com.typesafe.slick" %% "slick" % "2.1.0",
  "com.typesafe" % "config" % "1.0.2",
  "com.github.tototoshi" %% "scala-csv" % "1.1.2",
  "joda-time" % "joda-time" % "2.3",
  "org.joda" % "joda-convert" % "1.2",
  "com.github.tototoshi" %% "slick-joda-mapper" % "1.2.0",
  "postgresql" % "postgresql" % "9.1-901.jdbc4",
  "org.rogach" %% "scallop" % "0.9.5",
  "org.slf4j" % "slf4j-log4j12" % "1.6.4",
  "org.specs2" %% "specs2-core" % "2.4.15" % "test,it",
  "junit" % "junit" % "4.8.1" % "test,it"
)

assemblySettings

//mergeStrategy in assembly <<= (mergeStrategy in assembly) { (old) =>
//  {
//    case PathList("scala", xs @ _*) => MergeStrategy.first
//    case x => old(x)
////    case _ => MergeStrategy.first
//  }
//}

//excludedJars in assembly <<= (fullClasspath in assembly) map { cp =>
//  cp filter {_.data.getName == "scala-library-2.10.1.jar"}
//}

//assemblyOption in assembly ~= { _.copy(includeScala = false) }


//com.github.retronym.SbtOneJar.oneJarSettings

//net.virtualvoid.sbt.graph.Plugin.graphSettings

//EclipseKeys.configurations := Set(Compile, Test, IntegrationTest)

//hack for sbteclipse
//unmanagedSourceDirectories in Compile <++= baseDirectory { base =>
//  Seq(
//    base / "src/it/scala"
//  )
//}
