/**
 * Copyright (C) 2018 Lightbend Inc. <https://www.lightbend.com>
 */

package com.lightbend.kafka.scala.iq
package serializers

import org.apache.kafka.common.serialization._
import org.apache.kafka.streams.kstream._

trait SerDeserializer[T] extends Serializer[T] with Deserializer[T]

trait Serializers {
  final val stringSerializer = new StringSerializer()
  final val stringDeserializer = new StringDeserializer()
  final val byteArraySerializer = new ByteArraySerializer()
  final val byteArrayDeserializer = new ByteArrayDeserializer()

  final val timeWindowedStringSerializer: TimeWindowedSerializer[String] = new TimeWindowedSerializer[String](stringSerializer)
  final val timeWindowedStringDeserializer: TimeWindowedDeserializer[String] = new TimeWindowedDeserializer[String](stringDeserializer)
  final val timeWindowedStringSerde: Serde[Windowed[String]] = Serdes.serdeFrom(timeWindowedStringSerializer, timeWindowedStringDeserializer)

  final val sessionWindowedStringSerializer: SessionWindowedSerializer[String] = new SessionWindowedSerializer[String](stringSerializer)
  final val sessionWindowedStringDeserializer: SessionWindowedDeserializer[String] = new SessionWindowedDeserializer[String](stringDeserializer)
  final val sessionWindowedStringSerde: Serde[Windowed[String]] = Serdes.serdeFrom(sessionWindowedStringSerializer, sessionWindowedStringDeserializer)

  final val timeWindowedByteArraySerializer: TimeWindowedSerializer[Array[Byte]] = new TimeWindowedSerializer[Array[Byte]](byteArraySerializer)
  final val timeWindowedByteArrayDeserializer: TimeWindowedDeserializer[Array[Byte]] = new TimeWindowedDeserializer[Array[Byte]](byteArrayDeserializer)
  final val timeWindowedByteArraySerde: Serde[Windowed[Array[Byte]]] = Serdes.serdeFrom(timeWindowedByteArraySerializer, timeWindowedByteArrayDeserializer)

  final val sessionWindowedByteArraySerializer: SessionWindowedSerializer[Array[Byte]] = new SessionWindowedSerializer[Array[Byte]](byteArraySerializer)
  final val sessionWindowedByteArrayDeserializer: SessionWindowedDeserializer[Array[Byte]] = new SessionWindowedDeserializer[Array[Byte]](byteArrayDeserializer)
  final val sessionWindowedByteArraySerde: Serde[Windowed[Array[Byte]]] = Serdes.serdeFrom(sessionWindowedByteArraySerializer, sessionWindowedByteArrayDeserializer)

  final val stringSerde = Serdes.String()
  final val longSerde: Serde[Long] = Serdes.Long().asInstanceOf[Serde[Long]]
  final val byteArraySerde = Serdes.ByteArray()
}
