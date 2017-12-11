package com.lightbend.kafka.scala.iq
package serializers

import org.apache.kafka.common.serialization.{ Deserializer, Serde, Serializer }

import org.apache.avro.Schema

import com.twitter.bijection.Injection
import com.twitter.bijection.avro.SpecificAvroCodecs

import java.util.Map

class SpecificAvroSerde[T <: org.apache.avro.specific.SpecificRecordBase](schema: Schema) extends Serde[T] {

  val recordInjection: Injection[T, Array[Byte]] = SpecificAvroCodecs.toBinary(schema)
  val avroSerde = new SpecificAvroSerDeserializer(recordInjection)

  override def serializer(): Serializer[T] = avroSerde

  override def deserializer(): Deserializer[T] = avroSerde

  override def configure(configs: Map[String, _], isKey: Boolean): Unit = ()

  override def close(): Unit = ()
}
