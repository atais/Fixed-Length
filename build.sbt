import sbtrelease.ReleasePlugin.autoImport.ReleaseTransformations._

// main dependencies
lazy val shapeless = "com.chuusai" %% "shapeless" % "2.3.7"
lazy val shapelessMacros = compilerPlugin("org.scalamacros" % "paradise" % "2.1.0" cross CrossVersion.full)
lazy val cats = "org.typelevel" %% "cats-core" % "2.6.1"

// test dependencies
lazy val scalatest = "org.scalatest" %% "scalatest" % "3.2.10" % Test
lazy val scalacheck = "org.scalacheck" %% "scalacheck" % "1.15.4" % Test
lazy val scalatestPlus = "org.scalatestplus" %% "scalacheck-1-15" % "3.2.10.0" % Test

lazy val main = (project in file("."))
  .settings(

    name := "fixed-length",
    organization := "com.github.atais",
    scalaVersion := "2.13.6",
    crossScalaVersions := Seq("2.12.15", "2.13.6"),

    // dependencies
    libraryDependencies ++= Seq(
      shapeless,
      cats,
      scalatest,
      scalacheck,
      scalatestPlus
    ),
    libraryDependencies ++= (if (scalaBinaryVersion.value startsWith "2.10") Seq(shapelessMacros) else Nil),

    // release settings
    releaseCrossBuild := true,
    publishTo := Some(
      if (isSnapshot.value)
        Opts.resolver.sonatypeSnapshots
      else
        Opts.resolver.sonatypeStaging
    ),

    releaseProcess := Seq[ReleaseStep](
      checkSnapshotDependencies,
      inquireVersions,
      runClean,
      runTest,
      setReleaseVersion,
      commitReleaseVersion,
      tagRelease,
      ReleaseStep(action = Command.process("publishSigned", _), enableCrossBuild = true),
//      setNextVersion,
//      commitNextVersion,
      ReleaseStep(action = Command.process("sonatypeReleaseAll", _), enableCrossBuild = true),
      pushChanges
    )
  )




