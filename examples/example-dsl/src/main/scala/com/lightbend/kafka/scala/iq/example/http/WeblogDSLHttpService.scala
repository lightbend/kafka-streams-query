/**
 * Copyright (C) 2018 Lightbend Inc. <https://www.lightbend.com>
 */

package com.lightbend.kafka.scala.iq.example
package http

import akka.actor.ActorSystem

import akka.stream.ActorMaterializer

import io.circe.generic.auto._
import io.circe.syntax._

import org.apache.kafka.streams.state.HostInfo

import scala.concurrent.ExecutionContext
import com.lightbend.kafka.scala.iq.http.InteractiveQueryHttpService


class WeblogDSLHttpService(
  hostInfo: HostInfo, 
  summaryInfoFetcher: SummaryInfoFetcher,
  actorSystem: ActorSystem,
  actorMaterializer: ActorMaterializer,
  ec: ExecutionContext
) extends InteractiveQueryHttpService(hostInfo, actorSystem, actorMaterializer, ec) { 


  // define the routes
  val routes = handleExceptions(myExceptionHandler) {
    pathPrefix("weblog") {
      (get & pathPrefix("access" / "win") & path(Segment)) { hostKey =>
        complete {
          summaryInfoFetcher.fetchWindowedAccessCountSummary(hostKey, 0, System.currentTimeMillis).map(_.asJson)
        }
      } ~
      (get & pathPrefix("bytes" / "win") & path(Segment)) { hostKey =>
        complete {
          summaryInfoFetcher.fetchWindowedPayloadSizeSummary(hostKey, 0, System.currentTimeMillis).map(_.asJson)
        }
      } ~
      (get & pathPrefix("access" / "win" / Segment / LongNumber / LongNumber)) { (hostKey, fromTime, toTime) =>
        complete {
          summaryInfoFetcher.fetchWindowedAccessCountSummary(hostKey, fromTime, toTime).map(_.asJson)
        }
      } ~
      (get & pathPrefix("bytes" / "win" / Segment / LongNumber / LongNumber)) { (hostKey, fromTime, toTime) =>
        complete {
          summaryInfoFetcher.fetchWindowedPayloadSizeSummary(hostKey, fromTime, toTime).map(_.asJson)
        }
      } ~
      (get & pathPrefix("access" / "range" / Segment / Segment)) { (fromKey, toKey) =>
        complete {
          summaryInfoFetcher.fetchRangeAccessCountSummary(fromKey, toKey).map(_.asJson)
        }
      } ~
      (get & pathPrefix("bytes" / "range" / Segment / Segment)) { (fromKey, toKey) =>
        complete {
          summaryInfoFetcher.fetchRangePayloadSizeSummary(fromKey, toKey).map(_.asJson)
        }
      } ~
      (get & pathPrefix("access") & path(Segment)) { hostKey =>
        complete {
          if (hostKey == "ALL") summaryInfoFetcher.fetchAllAccessCountSummary.map(_.asJson)
          else if (hostKey == "COUNT") summaryInfoFetcher.fetchApproxAccessCountNumEntries.map(_.asJson)
          else summaryInfoFetcher.fetchAccessCountSummary(hostKey).map(_.asJson)
        }
      } ~
      (get & pathPrefix("bytes") & path(Segment)) { hostKey =>
        complete {
          if (hostKey == "ALL") summaryInfoFetcher.fetchAllPayloadSizeSummary.map(_.asJson)
          else if (hostKey == "COUNT") summaryInfoFetcher.fetchApproxPayloadNumEntries.map(_.asJson)
          else summaryInfoFetcher.fetchPayloadSizeSummary(hostKey).map(_.asJson)
        }
      }
    }
  }
}
