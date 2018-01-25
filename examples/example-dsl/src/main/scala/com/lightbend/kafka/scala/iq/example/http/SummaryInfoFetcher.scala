/**
 * Copyright (C) 2018 Lightbend Inc. <https://www.lightbend.com>
 */

package com.lightbend.kafka.scala.iq.example
package http

import com.lightbend.kafka.scala.iq.http.KeyValueFetcher
import scala.concurrent.Future

class SummaryInfoFetcher(kvf: KeyValueFetcher[String, Long]) {
  def fetchAccessCountSummary(hostKey: String): Future[Long] =
    kvf.fetch(hostKey, WeblogProcessing.ACCESS_COUNT_PER_HOST_STORE, "/weblog/access/" + hostKey)

  def fetchPayloadSizeSummary(hostKey: String): Future[Long] =
    kvf.fetch(hostKey, WeblogProcessing.PAYLOAD_SIZE_PER_HOST_STORE, "/weblog/bytes/" + hostKey)

  def fetchWindowedAccessCountSummary(hostKey: String, fromTime: Long, toTime: Long): Future[List[(Long, Long)]] = 
    kvf.fetchWindowed(hostKey, WeblogProcessing.WINDOWED_ACCESS_COUNT_PER_HOST_STORE, "/weblog/access/win/", fromTime, toTime) 

  def fetchWindowedPayloadSizeSummary(hostKey: String, fromTime: Long, toTime: Long): Future[List[(Long, Long)]] = 
    kvf.fetchWindowed(hostKey, WeblogProcessing.WINDOWED_PAYLOAD_SIZE_PER_HOST_STORE, "/weblog/bytes/win/", fromTime, toTime) 
  
}
