/**
 * Copyright (C) 2018 Lightbend Inc. <https://www.lightbend.com>
 */

package com.lightbend.kafka.scala.iq.example
package serializers

import models.LogRecord
import org.apache.kafka.common.serialization.{ Serdes, Serializer, Deserializer, Serde }
import io.confluent.kafka.serializers.AbstractKafkaAvroSerDeConfig
import com.lightbend.kafka.scala.iq.serializers._

trait AppSerializers extends Serializers {
  final val ts = new Tuple2Serializer[String, String]()
  final val ms = new ModelSerializer[LogRecord]()
  final val logRecordSerde = Serdes.serdeFrom(ms, ms)

  implicit val baSerde = byteArraySerde
  implicit val tuple2StringSerde = Serdes.serdeFrom(ts, ts)
  implicit val wss = windowedStringSerde
  implicit val ss = stringSerde
  implicit val ls = longSerde

  /**
   * The Serde instance varies depending on whether we are using Schema Registry. If we are using
   * schema registry, we use the serde provided by Confluent, else we use Avro serialization backed by
   * Twitter's bijection library
   */ 
  def logRecordAvroSerde(maybeSchemaRegistryUrl: Option[String]): Serde[LogRecordAvro] = maybeSchemaRegistryUrl.map { url =>
    val serde = new SpecificAvroSerdeWithSchemaRegistry[LogRecordAvro]()
    serde.configure(
        java.util.Collections.singletonMap(AbstractKafkaAvroSerDeConfig.SCHEMA_REGISTRY_URL_CONFIG, url),
        false)
    serde
  }.getOrElse {
    new SpecificAvroSerde[LogRecordAvro](LogRecordAvro.SCHEMA$)
  }
}

class LogRecordSerde extends Serde[LogRecord] {
  private val lrSer = new ModelSerializer[LogRecord]

  override def serializer(): Serializer[LogRecord] = lrSer
  override def deserializer(): Deserializer[LogRecord] = lrSer
  override def configure(configs: java.util.Map[String, _], isKey: Boolean): Unit = lrSer.configure(configs, isKey)
  override def close(): Unit = lrSer.close
}
