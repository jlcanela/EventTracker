package com.aclys.eventtracker.service.event

import com.aclys.eventtracker.service.JSonService
import com.aclys.eventtracker.service.ValidationException
import net.debasishg.sjson.json._
import org.joda.time.DateTime
import rosetta.json.JsonImplementation
import scalaz.camel.core._
import scalaz._
import Scalaz._
import net.liftweb._
import json.JsonAST.{ JValue, JString }
	      
import com.mongodb.casbah.Imports._
	      
trait EventService[Json] extends JSonService[Json] with EventProtocol[Json] with JsonSerialization[Json] {
  this: Camel =>

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
  
  private def persistEvent(e: Event) : ValidationNEL[String, Boolean] = for {
	      json <- tojson(e)
	      res <- persistEvent(json)
   } yield res 
  
  private def persistEvent(json: Json) : ValidationNEL[String, Boolean] = try {
	  for {
	      mongoObject <- convert2Mongo(json).liftFailNel
	      wr = eventColl.save(mongoObject)	      
	  } yield wr.getLastError.ok
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
  
  
  private def registerEventImpl(m: Message): ValidationNEL[String, Json] = for {
    j <- postJson(m)
    e <- (creationDate(j.get("creationDate")) |@| eventType(j.get("eventType")) |@| userId(j.get("userId"))) {
      (creationDate, eventType, uid) => Event(creationDate, eventType, uid, j) // TODO: CAUTION, using localdate, check what date is used by the client API
    }
    persisted <- persistEvent(e) 
    json <- tojson(e)
  } yield json


  def registerEvent = liftMessage((m: Message) => registerEventImpl(m)) >=> jsonToString 

}