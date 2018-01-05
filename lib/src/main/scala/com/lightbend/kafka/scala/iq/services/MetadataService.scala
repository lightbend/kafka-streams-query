/**
 * Copyright (C) 2018 Lightbend Inc. <https://www.lightbend.com>
 */

/*
 * Copyright Confluent Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.lightbend.kafka.scala.iq
package services

import org.apache.kafka.common.serialization.Serializer
import org.apache.kafka.streams.KafkaStreams
import org.apache.kafka.streams.state.StreamsMetadata

import scala.collection.JavaConverters._
import scala.util.{Failure, Success, Try}
import com.typesafe.scalalogging.LazyLogging

case class HostStoreInfo(host: String, port: Int, storeNames: Set[String])

/**
 * Looks up StreamsMetadata from KafkaStreams 
 * Adapted from https://github.com/confluentinc/kafka-streams-examples/blob/4.0.0-post/src/main/java/io/confluent/examples/streams/interactivequeries/MetadataService.java
 */
class MetadataService(val streams: KafkaStreams) extends LazyLogging {

  /**
   * Get the metadata for all of the instances of this Kafka Streams application
   * @return List of {@link HostStoreInfo}
   */
  def streamsMetadata(): List[HostStoreInfo] = {
    // Get metadata for all of the instances of this Kafka Streams application
    streams.allMetadata().asScala.toList.map(streamsMetadataToHostStoreInfo)
  }

  /**
   * Get the metadata for all instances of this Kafka Streams application that currently
   * has the provided store.
   * @param store   The store to locate
   * @return  List of {@link HostStoreInfo}
   */
  def streamsMetadataForStore(store: String): List[HostStoreInfo] = {
    // Get metadata for all of the instances of this Kafka Streams application hosting the store
    streams.allMetadataForStore(store).asScala.toList.map(streamsMetadataToHostStoreInfo)
  }

  /**
   * Find the metadata for the instance of this Kafka Streams Application that has the given
   * store and would have the given key if it exists.
   * @param store   Store to find
   * @param key     The key to find
   * @return {@link HostStoreInfo}
   */
  def streamsMetadataForStoreAndKey[K](store: String, key: K, serializer: Serializer[K]): Try[HostStoreInfo] = {
    // Get metadata for the instances of this Kafka Streams application hosting the store and
    // potentially the value for key
    logger.info(s"Finding streams metadata for $store, $key, $serializer")
    streams.metadataForKey(store, key, serializer) match {
      case null => Failure(new IllegalArgumentException(s"Metadata for key $key not found in $store"))
      case metadata => Success(new HostStoreInfo(metadata.host, metadata.port, metadata.stateStoreNames.asScala.toSet))
    }
  }

  private[services] val streamsMetadataToHostStoreInfo: StreamsMetadata => HostStoreInfo = metadata => {
    HostStoreInfo(metadata.host(), metadata.port(), metadata.stateStoreNames().asScala.toSet)
  }
}
