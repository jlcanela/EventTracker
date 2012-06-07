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

package service.event

import service.JSonService
import service.ValidationException
import net.debasishg.sjson.json._
import org.joda.time.DateTime
import rosetta.json.JsonImplementation
import scalaz.camel.core._
import scalaz._
import Scalaz._
import net.liftweb._
import json.JsonAST.{ JValue, JString }
	      
import com.mongodb.casbah.Imports._
import java.io.ObjectInputStream

trait EventService[Json] extends JSonService[Json] with EventProtocol[Json] with JsonSerialization[Json] {


  val jsonImplementation: JsonImplementation[Json]
  import jsonImplementation._
  import EventProtocol._
  
  val eventColl : MongoCollection
  
  private def convert2Mongo(j : Json) : Validation[String,MongoDBObject] = try {
    // will be more typesafe to use a MongoJson implementation for rosetta, let's do that later
  	com.mongodb.util.JSON.parse(j2s(j)) match {
		    case obj : BasicDBObject => Success(obj) //.success.liftFailNel
		    case x => "Failed convert2Mongo (classname=%s)".format(x.getClass.getName).fail
	} 

  } catch {
    case ex => ex.getMessage.fail
  }
  
  private def persistEvent(e: Event) : ValidationNEL[String, (Boolean, Json)] = for {
	      json <- tojson(e)
	      res <- persistEvent(json)
   } yield res 
  
  private def persistEvent(json: Json) : ValidationNEL[String, (Boolean, Json)] = try {
	  for {
	      mongoObject <- convert2Mongo(json).liftFailNel
	      wr = eventColl.save(mongoObject)	      
	  } yield (wr.getLastError.ok, json)
  } catch {
    case ex => ex.printStackTrace; "FailedPersistEvent : %s".format(ex.getMessage).fail.liftFailNel
  }

  def creationDate(json : Json) : ValidationNEL[String, DateTime] = (DateTimeFormat.reads(json)) match {
    case Success(date) => Success(date)
    case Failure(e) => Success(new DateTime) // using server receive date if date not present in json stream, this rule will be updated for custom event 
  } 
  
  def eventType(json: Json) = json match {
    case JsonString(eventType) => Success(eventType)
    case _ => "eventType is mandatory".fail.liftFailNel
  }
  
  def userId(json: Json) : ValidationNEL[String, String] = StringFormat.reads(json) match {
    case Success(uid) => Success(uid)
    case Failure(e) => "userId is mandatory".fail.liftFailNel
  }

  def convertEvent(j: Json) = for {
    e <- (creationDate(j.get("creationDate")) |@| eventType(j.get("eventType")) |@| userId(j.get("userId"))) {
      (creationDate, eventType, uid) => Event(creationDate, eventType, uid, j) // TODO: CAUTION, using localdate, check what date is used by the client API
    }} yield e

   def registerEvent(j: Json): ValidationNEL[String, Json] = for {
     e <- convertEvent(j)
     r <- persistEvent(e)
   } yield j


/*  private def validateMax(i : Int) = if (i < 10) i.success else "must be lower than 10".fail
  private def validateEven(i : Int) = if (i % 2 == 0) i.success else "must be even".fail
  
  private def validateAll(i : Int) = (validateMax(i) |@| validateEven(i)) {
    (maxOk, minOk) => i
  }  */
  
}
