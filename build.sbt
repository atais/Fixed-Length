name := "fixed-length"

version := "1.0"

scalaVersion := "2.10.6"

libraryDependencies += "com.chuusai" %% "shapeless" % "2.3.2"
libraryDependencies += "org.scalatest" %% "scalatest" % "3.0.1" % "test"
libraryDependencies += "org.scalacheck" %% "scalacheck" % "1.13.4" % "test"

libraryDependencies += compilerPlugin("org.scalamacros" % "paradise" % "2.1.0" cross CrossVersion.full)

libraryDependencies += "org.scodec" %% "scodec-bits" % "1.1.4"
libraryDependencies += "org.scodec" %% "scodec-core" % "1.10.3"
libraryDependencies += "org.typelevel" %% "cats" % "0.9.0"
libraryDependencies += "org.scalactic" %% "scalactic" % "3.0.1"
libraryDependencies += "org.scalatest" %% "scalatest" % "3.0.1" % "test"
libraryDependencies += "org.scalacheck" %% "scalacheck" % "1.13.4" % "test"