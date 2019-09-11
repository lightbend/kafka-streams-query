import sbtassembly.MergeStrategy
import NativePackagerHelper._

name := "QueryExampleProject-root"

version in ThisBuild := "0.2.0"

scalaVersion := Versions.scalaVersion

def appProject(id: String)(base:String = id) = Project(id, base = file(base))
  .enablePlugins(JavaAppPackaging)

// standalone run of the dsl example application
lazy val dslRun = (project in file("./example-dsl"))
  .settings(Common.settings: _*)
  .settings(libraryDependencies ++= Dependencies.dslDependencies)
  .settings (
    fork in run := true,
    mainClass in Compile := Some("com.lightbend.kafka.scala.iq.example.WeblogProcessing"),
    scalacOptions := Seq("-Xexperimental", "-unchecked", "-deprecation", "-Ywarn-unused-import"),
    javaOptions in run ++= Seq(
      "-Dconfig.file=" + (resourceDirectory in Compile).value / "application-dsl.conf",
      "-Dlogback.configurationFile=" + (resourceDirectory in Compile).value / "logback-dsl.xml",
      "-Dlog4j.configurationFile=" + (resourceDirectory in Compile).value / "log4j.properties"),
    (sourceDirectory in AvroConfig) := baseDirectory.value / "src/main/resources/com/lightbend/kafka/scala/iq/example",
    (stringType in AvroConfig) := "String",
    addCommandAlias("dsl", "dslRun/run")
  )
  .dependsOn(server)

// packaged run of the dsl example application
lazy val dslPackage = appProject("dslPackage")("build/dsl")
  .settings(
    scalaVersion := Versions.scalaVersion,
    resourceDirectory in Compile := (resourceDirectory in (dslRun, Compile)).value,
    mappings in Universal ++= {
      Seq(((resourceDirectory in Compile).value / "application-dsl.conf") -> "conf/application.conf") ++
        Seq(((resourceDirectory in Compile).value / "logback-dsl.xml") -> "conf/logback.xml") ++
        Seq(((resourceDirectory in Compile).value / "log4j.properties") -> "conf/log4j.properties")
    },
    assemblyMergeStrategy in assembly := {
      case PathList("application-dsl.conf") => MergeStrategy.discard
      case PathList("logback-dsl.xml") => MergeStrategy.discard
      case PathList("META-INF", "MANIFEST.MF") => MergeStrategy.discard
      case PathList("META-INF", xs @ _*) => MergeStrategy.last
      case PathList("META-INF", "io.netty.versions.properties") => MergeStrategy.last
      case x =>
        val oldStrategy = (assemblyMergeStrategy in assembly).value
        oldStrategy(x)
    },
    scriptClasspath := Seq("../conf/") ++ scriptClasspath.value,
    mainClass in Compile := Some("com.lightbend.kafka.scala.iq.example.WeblogProcessing")
  )
  .dependsOn(server, dslRun)

// standalone run of the proc example application
lazy val procRun = (project in file("./example-proc"))
  .settings(Common.settings: _*)
  .settings(libraryDependencies ++= Dependencies.procDependencies)
  .settings (
    fork in run := true,
    mainClass in Compile := Some("com.lightbend.kafka.scala.iq.example.WeblogDriver"),
    scalacOptions := Seq("-Xexperimental", "-unchecked", "-deprecation", "-Ywarn-unused-import"),
    javaOptions in run ++= Seq(
      "-Dconfig.file=" + (resourceDirectory in Compile).value / "application-proc.conf",
      "-Dlogback.configurationFile=" + (resourceDirectory in Compile).value / "logback-proc.xml",
      "-Dlog4j.configurationFile=" + (resourceDirectory in Compile).value / "log4j.properties"),
    addCommandAlias("proc", "procRun/run")
  )
  .dependsOn(server)

// packaged run of the proc example application
lazy val procPackage = appProject("procPackage")("build/proc")
  .settings(
    scalaVersion := Versions.scalaVersion,
    resourceDirectory in Compile := (resourceDirectory in (procRun, Compile)).value,
    mappings in Universal ++= {
      Seq(((resourceDirectory in Compile).value / "application-proc.conf") -> "conf/application.conf") ++
        Seq(((resourceDirectory in Compile).value / "logback-proc.xml") -> "conf/logback.xml") ++
        Seq(((resourceDirectory in Compile).value / "log4j.properties") -> "conf/log4j.properties")
    },
    assemblyMergeStrategy in assembly := {
      case PathList("application-proc.conf") => MergeStrategy.discard
      case PathList("logback-proc.xml") => MergeStrategy.discard
      case PathList("META-INF", "MANIFEST.MF") => MergeStrategy.discard
      case PathList("META-INF", xs @ _*) => MergeStrategy.last
      case PathList("META-INF", "io.netty.versions.properties") => MergeStrategy.last
      case x =>
        val oldStrategy = (assemblyMergeStrategy in assembly).value
        oldStrategy(x)
    },
    scriptClasspath := Seq("../conf/") ++ scriptClasspath.value,
    mainClass in Compile := Some("com.lightbend.kafka.scala.iq.example.WeblogDriver")
  )
  .dependsOn(server, procRun)

lazy val server = (project in file("./kafka-local-server")).
    settings(Common.settings: _*).
    settings(libraryDependencies ++= Dependencies.serverDependencies)

lazy val root = (project in file(".")).
    aggregate(dslRun, dslPackage, procRun, procPackage, server)
