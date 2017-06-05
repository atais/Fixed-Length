name := "fixed-length"

version := "1.0"

scalaVersion := "2.10.6"

libraryDependencies += "com.chuusai" %% "shapeless" % "2.3.2"
libraryDependencies += "org.scalatest" %% "scalatest" % "3.0.1" % "test"
libraryDependencies += "org.scalacheck" %% "scalacheck" % "1.13.4" % "test"

libraryDependencies += compilerPlugin("org.scalamacros" % "paradise" % "2.1.0" cross CrossVersion.full)
