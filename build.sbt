ThisBuild / version := "0.1.0-SNAPSHOT"

ThisBuild / scalaVersion := "3.2.2"

lazy val root = (project in file("."))
  .settings(
    name := "scalismo-hdf5-json",
    organization := "ch.unibas.cs.gravis",
    homepage := Some(url("https://github.com/unibas-gravis/scalismo-hdf5-json")),
    licenses += ("Apache-2.0", url("http://www.apache.org/licenses/LICENSE-2.0")),
    scmInfo := Some(
      ScmInfo(url("https://github.com/unibas-gravis/scalismo-hdf5-json"), "git@github.com:unibas-gravis/scalismo-hdf5-json.git")
    ),
    developers := List(
      Developer("marcelluethi", "marcelluethi", "marcel.luethi@unibas.ch", url("https://github.com/marcelluethi"))
    ),
    publishMavenStyle := true,
    publishTo := Some(
      if (isSnapshot.value)
        Opts.resolver.sonatypeOssSnapshots.head
      else
        Opts.resolver.sonatypeStaging),
    libraryDependencies ++= Seq(
      "com.lihaoyi" %% "upickle" % "2.0.0",
      "org.scalameta" %% "munit" % "0.7.29" % Test
    )
  )
