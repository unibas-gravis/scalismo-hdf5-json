ThisBuild / version := "0.1.0-SNAPSHOT"

ThisBuild / scalaVersion := "3.2.2"

lazy val root = (project in file("."))
  .settings(
    name := "scalismo-hdf-json",
    organization := "ch.unibas.cs.gravis",
    libraryDependencies ++= Seq(
      "com.lihaoyi" %% "upickle" % "2.0.0",
      "org.scalameta" %% "munit" % "0.7.29" % Test
    )
  )
