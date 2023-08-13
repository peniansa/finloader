import sbt._

import Defaults._

resolvers += "Sonatype snapshots" at "http://oss.sonatype.org/content/repositories/snapshots/"

//addSbtPlugin("com.github.mpeltonen" % "sbt-idea" % "1.5.0-SNAPSHOT")

addSbtPlugin("com.eed3si9n" % "sbt-assembly" % "0.10.0")


addSbtPlugin("net.virtual-void" % "sbt-dependency-graph" % "0.7.4")