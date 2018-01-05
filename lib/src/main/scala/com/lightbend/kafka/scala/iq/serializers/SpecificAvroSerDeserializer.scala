/**
 * Copyright (C) 2018 Lightbend Inc. <https://www.lightbend.com>
 */

package com.lightbend.kafka.scala.iq
package serializers

import com.twitter.bijection.Injection
import org.apache.avro.specific.SpecificRecordBase
import java.util.{Map => JMap}

import scala.util.Try

class SpecificAvroSerDeserializer[T <: SpecificRecordBase](injection: Injection[T, Array[Byte]]) extends SerDeserializer[T] {
  val inverted: Array[Byte] => Try[T] = injection.invert _

  override def configure(configs: JMap[String, _], isKey: Boolean): Unit = ()

  override def serialize(topic: String, record: T): Array[Byte] =  injection(record)

  override def deserialize(s: String, bytes: Array[Byte]): T =  inverted(bytes).get

  override def close(): Unit = ()
}
