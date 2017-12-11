import sbt._
import Keys._
import Versions._

object Dependencies {

  object Common {
  
    val ks              = "com.lightbend"                %% "kafka-streams-scala"      % ksVersion exclude("org.slf4j", "slf4j-log4j12")
    val kq              = "com.lightbend"                %% "kafka-streams-query"      % kqVersion exclude("org.slf4j", "slf4j-log4j12")
    val alpakka         = "com.lightbend.akka"           %% "akka-stream-alpakka-file" % alpakkaFileVersion
    val reactiveKafka   = "com.typesafe.akka"            %% "akka-stream-kafka"        % reactiveKafkaVersion
    val akkaSlf4j       = "com.typesafe.akka"            %% "akka-slf4j"               % akkaVersion
    val akkaStreams     = "com.typesafe.akka"            %% "akka-stream"              % akkaVersion
    val akkaHttp        = "com.typesafe.akka"            %% "akka-http"                % akkaHttpVersion
    val akkaHttpCirce   = "de.heikoseeberger"            %% "akka-http-circe"          % akkaHttpCirceVersion
    val circeCore       = "io.circe"                     %% "circe-core"               % circeVersion
    val circeGeneric    = "io.circe"                     %% "circe-generic"            % circeVersion
    val circeParser     = "io.circe"                     %% "circe-parser"             % circeVersion
    val logback         = "ch.qos.logback"                % "logback-classic"          % logbackVersion
    val scalaLogging    = "com.typesafe.scala-logging"   %% "scala-logging"            % scalaLoggingVersion
  }

  object Dsl {
  
    val bijection       = "com.twitter"                  %% "bijection-avro"           % bijectionVersion
    val confluentAvro   = "io.confluent"                  % "kafka-avro-serializer"    % confluentPlatformVersion exclude("org.slf4j", "slf4j-log4j12")
    val kafka           = "org.apache.kafka"             %% "kafka"                    % kafkaVersion excludeAll(ExclusionRule("org.slf4j", "slf4j-log4j12"), ExclusionRule("org.apache.zookeeper", "zookeeper")) 
  }

  object Proc {
    val algebird        = "com.twitter"                  %% "algebird-core"            % algebirdVersion 
    val chill           = "com.twitter"                  %% "chill"                    % chillVersion 
  }

  object Server {
    val scalaLogging    = "com.typesafe.scala-logging"   %% "scala-logging"            % scalaLoggingVersion
    val curator         = "org.apache.curator"            % "curator-test"             % curatorVersion
    val kafkaStreams    = "org.apache.kafka"              % "kafka-streams"            % kafkaVersion
    val kafka           = "org.apache.kafka"             %% "kafka"                    % kafkaVersion excludeAll(ExclusionRule("org.slf4j", "slf4j-log4j12"), ExclusionRule("org.apache.zookeeper", "zookeeper")) 
  }

  val commonDependencies: Seq[ModuleID] = Seq(Common.ks,
    Common.kq,
    Common.alpakka,
    Common.reactiveKafka,
    Common.akkaSlf4j,
    Common.akkaStreams,
    Common.akkaHttp,
    Common.akkaHttpCirce,
    Common.circeCore,
    Common.circeGeneric,
    Common.circeParser,
    Common.logback,
    Common.scalaLogging
  )

  val dslDependencies: Seq[ModuleID] = commonDependencies ++ Seq(Dsl.bijection,
    Dsl.confluentAvro,
    Dsl.kafka
  )

  val procDependencies: Seq[ModuleID] = commonDependencies ++ Seq(Proc.algebird, Proc.chill)
  val serverDependencies: Seq[ModuleID] = Seq(Server.scalaLogging, Server.curator, Server.kafkaStreams, Server.kafka) 
}
