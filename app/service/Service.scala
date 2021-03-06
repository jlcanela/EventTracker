/*
 * Copyright 2012 Aclys
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

package service

import scalaz._
import Scalaz._
import _root_.service.event.EventService
import _root_.service.report.ReportService
import play.api.libs.json._

import com.mongodb.casbah.Imports._

case class ValidationException[T](v : ValidationNEL[String, T]) extends Exception(v.toString)


class Service extends EventService[JsValue] with ReportService[JsValue]{

  

  val jsonImplementation = JsonPlay // selecting lift-json implementation for rosetta stone
  val mf : Manifest[JsValue] = Manifest.classType(classOf[JsValue]) // keep a concrete reference to JValue manifest for JSonService

  val mongoConnection = MongoConnection()
  val mongoDbName = "eventtracker"
  val mongoEventCollName = "event"

  val eventColl = mongoConnection(mongoDbName)(mongoEventCollName)


  import com.mongodb.casbah.commons.conversions.scala._
  RegisterConversionHelpers()


  def start = {
    //router.start
  }

  def shutdown = {
    //router.stop
  }


}

