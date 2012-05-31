/*
 * Copyright 2007-2011 WorldWide Conferencing, LLC
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.aclys.eventtracker.service

import org.apache.camel.impl.DefaultCamelContext
import org.apache.camel.CamelContext
import scalaz.camel.core.Camel
import scalaz.camel.core.Message
import scalaz.camel.core.Router
import scalaz._
import Scalaz._
import _root_.com.aclys.eventtracker.service.event.EventService
import _root_.com.aclys.eventtracker.service.report.ReportService

case class ValidationException[T](v : ValidationNEL[String, T]) extends Exception(v.toString)

import net.liftweb.json.JsonAST._
import rosetta.json.lift._
import com.mongodb.casbah.Imports._

class CamelService extends Camel with EventService[JValue] with ReportService[JValue]{

  
  import org.apache.camel.CamelContext
  import org.apache.camel.impl.DefaultCamelContext

  import dispatch.json.JsValue
 
  val jsonImplementation = JsonLift // selecting lift-json implementation for rosetta stone
  val mf : Manifest[JValue] = Manifest.classType(classOf[JValue]) // keep a concrete reference to JValue manifest for JSonService 

  val mongoConnection = MongoConnection()
  val mongoDbName = "eventtracker"
  val mongoEventCollName = "event"

  val eventColl = mongoConnection(mongoDbName)(mongoEventCollName)

  val camelContext: CamelContext = new DefaultCamelContext
  implicit val router = new Router(camelContext)

  import com.mongodb.casbah.commons.conversions.scala._
  RegisterConversionHelpers()

  // capture the events
  from("jetty:http://localhost:8080/event") { attempt { registerEvent } fallback displayError }

  from("jetty:http://localhost:8080/reports/userArrival") { attempt { userArrivalReport } fallback displayError }

  router.start

  def shutdown = {
    router.stop
  }


}

