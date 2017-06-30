name := "fixed-length"

crossScalaVersions := Seq("2.10.6", "2.11.8", "2.12.2")

scalaVersion := "2.10.6"

// dependencies
lazy val shapeless = "com.chuusai" %% "shapeless" % "2.3.2"
lazy val shapelessMacros = compilerPlugin("org.scalamacros" % "paradise" % "2.1.0" cross CrossVersion.full)
lazy val cats = "org.typelevel" %% "cats" % "0.9.0"

// test dependencies
lazy val scalatest = "org.scalatest" %% "scalatest" % "3.0.1"
lazy val scalacheck = "org.scalacheck" %% "scalacheck" % "1.13.4"


libraryDependencies ++= Seq(
  shapeless,
  cats,
  scalatest % Test,
  scalacheck % Test
)

libraryDependencies ++= (if (scalaBinaryVersion.value startsWith "2.10") Seq(shapelessMacros) else Nil)

// release settings
releaseCrossBuild := true
publishTo := Some(
  if (isSnapshot.value)
    Opts.resolver.sonatypeSnapshots
  else
    Opts.resolver.sonatypeStaging
)
