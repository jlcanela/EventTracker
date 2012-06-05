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

  def start = {
    router.start
  }

  def shutdown = {
    router.stop
  }


}

