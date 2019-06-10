
name := "radCicsApi"

version := "1.0"

scalaVersion := "2.12.2"

resolvers += "scalaz-bintray" at "https://dl.bintray.com/scalaz/releases"

resolvers += "Akka Snapshot Repository" at "http://repo.akka.io/snapshots/"

lazy val commonSettings = Seq(
  scalaVersion:="2.12.2",
  organization:="com.redbee"
)

scalaSource in IntegrationTest := baseDirectory.value / "it"
scalaSource in Test := baseDirectory.value / "test"
parallelExecution in Test := false
parallelExecution in IntegrationTest := false
lazy val root = (project in file("."))
  .enablePlugins(PlayScala)
  .enablePlugins(JavaAppPackaging)
  .enablePlugins(DockerPlugin)
  .configs(IntegrationTest)
  .settings(
    commonSettings,
    Defaults.itSettings,
    libraryDependencies ++= Seq(
      jdbc , ehcache , ws , specs2 % Test , guice,
      "log4j" % "apache-log4j-extras" % "1.2.17",
      "org.scalikejdbc" %% "scalikejdbc"                  % "3.3.0",
      "org.scalikejdbc" %% "scalikejdbc-test"   % "3.3.0"   % "it",
      "org.scalikejdbc" %% "scalikejdbc-config"           % "3.3.0",
      "org.scalikejdbc" %% "scalikejdbc-play-initializer" % "2.6.0-scalikejdbc-3.3",
      "org.scalatestplus.play" %% "scalatestplus-play" % "3.1.2" % "test",
      "io.reactivex" %% "rxscala" % "0.26.5",
      "org.scalatest" %% "scalatest" % "3.0.5" % "it,test")
  )

javaOptions in Test += s"-Dconfig.file=${baseDirectory.value}/conf/application.conf"
javaOptions in IntegrationTest += s"-Dconfig.file=${baseDirectory.value}/conf/application.conf"
fork in Test := true
fork in IntegrationTest:= true

libraryDependencies += "com.sandinh" %% "scala-soap" % "1.8.0"