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

package com.aclys.eventtracker.service.report

import com.aclys.eventtracker.service.JSonService
import com.aclys.eventtracker.service.ValidationException
import com.aclys.eventtracker.service.event.EventProtocol
import net.debasishg.sjson.json._
import org.joda.time.DateTime
import rosetta.json.JsonImplementation
import scalaz.camel.core._
import scalaz._
import Scalaz._
import net.liftweb._
import json.JsonAST.{ JValue, JString }
	      
import com.mongodb.casbah.Imports._
	      
trait ReportService[Json] extends JSonService[Json] with EventProtocol[Json] with JsonSerialization[Json] {
  this: Camel =>

  val jsonImplementation: JsonImplementation[Json]
  import jsonImplementation._
  import EventProtocol._
  
  val eventColl : MongoCollection

  def userArrivalReportImpl = { 

    // NOTE : current implementation does not save to mongodb the date in the right format
    // so it's not yet possible to search by start-date / end-date
    // TODO : instead of doing the group by in memory, we should use a mongodb map / reduce
    def event = eventColl.find(
        MongoDBObject("eventType" -> "userArrival"), // select by event type                                                    
        MongoDBObject("creationDate" -> true)).      // and keep only creation date        
        toList.groupBy(_.get("creationDate") match {
          case dt : String => DateTime.parse(dt).toLocalDate.toString
          case x => println("###" + x.getClass.getName); x.toString
        }).toList.map({ case (date, values) => (date, values.size) })

    JsonObject(
        "date-start" -> JsonString("date-start"), 
        "date-end" -> JsonString("date-end"),
        "data" -> JsonArray(
            event.map(x => 
              JsonObject("date" -> JsonString(x._1), "value" -> JsonLong(x._2))))
    ).success    
  }
  
  def userArrivalReport = liftMessage((m: Message) => userArrivalReportImpl) >=> jsonToString 

}
