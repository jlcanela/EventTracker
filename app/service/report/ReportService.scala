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

package service.report

import service.JSonService
import service.ValidationException
import service.event.EventProtocol
import net.debasishg.sjson.json._
import org.joda.time.DateTime
import rosetta.json.JsonImplementation
import scalaz._
import Scalaz._
//import net.liftweb._
//import json.JsonAST.{ JValue, JString }

import com.mongodb.casbah.Imports._
	      
trait ReportService[Json] extends JSonService[Json] with EventProtocol[Json] with JsonSerialization[Json] {

  val jsonImplementation: JsonImplementation[Json]
  import jsonImplementation._
  import EventProtocol._
  
  val eventColl : MongoCollection

  def userArrivalReport = {

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

    // ISODate does not manage timezone and is not available for map reduce queries. So I implemented function convertIsoDate()
    val mapJS = """
      function m() {
           function toDate(dt) {
	           function twoDigit(num) {
	              if (num < 10) {
	      			return "0" + num;
	              } else {
	                return "" + num;
	              }               
	           }
              return dt.getFullYear() + "-" + twoDigit(dt.getMonth() + 1) + "-" + twoDigit(dt.getDate());
           }
           function convertIsoDate( dt ) { // for example dt = "2012-05-31T17:09:53.874+02:00"
              var daterg=/([0-9]+)-([0-9]+)-([0-9]+)T([0-9]+):([0-9]+):([0-9]+).([0-9]+)([+-])([0-9]+):([0-9]+)/g;
              daterg.lastIndex = 0; // daterg is cached by js engine, need to clean lastIndex 
              var match = daterg.exec(dt);
              var d;
          
              if (match != null) {
                  var d = new Date(match[1], match[2] - 1, match[3], match[4], match[5], match[6]);  
         		  var addminutes = - d.getTimezoneOffset();
                  
                  var sign = match[8];  
                  var zh = parseInt(match[9]);
                  var zm = parseInt(match[10]);
      
				  if (sign == "+") {
				    addminutes = addminutes - zh * 60;
				    addminutes = addminutes - zm ;
				  } else if (sign == "-") {
				    addminutes = addminutes + zh * 60;
				    addminutes = addminutes + zm;
				  }
				  d = new Date(d.getTime() + addminutes * 60 * 1000);
                  return d;          
				} else {
                  return;
                }
          }
          var key = toDate(convertIsoDate(this.creationDate));
          var singleEvent = {};
          singleEvent[this.userId] = [ this.eventType ];
          emit( key, { events : [singleEvent] } )
      }
    """
      
    val reduceJS = """
      function r( dt, values ) {
            
	      var eventsByUser = {};

          function addEvents(userId, events) {
            if (!eventsByUser[userId]) {
                eventsByUser[userId] = [];
            } 
            eventsByUser[userId] = eventsByUser[userId].concat(events);
          }

          values.forEach( function(ev) {
            for(idx in ev.events) {
               var eventCol = ev.events[idx];
               for(userId in eventCol) { 
                 addEvents(userId, eventCol[userId]);
               }
            }
          })
      
          return { "events" : [eventsByUser] };
      
    }
    """
      
    val finalizeJS = """
      function f( date, value ){
          var count = 0;
      
          if (value.events[0]) {
             for(userId in value.events[0]) {
                 count++;
             }
          }
          return count;
      }
    """
      
     // TODO : mapReduce temp collection are cleaned on connection close
     // need to investigate connection pooling + cleaning no longer used resources such as temp collections 
     def report : Seq[DBObject]= eventColl.mapReduce(
        mapJS,
        reduceJS,
        MapReduceInlineOutput,
       finalizeFunction = Some(finalizeJS),
        verbose = true
      ).cursor.toSeq
    
      // TODO : use rosetta-json to convert from DBObject to lift-json in a better type-safe way
      def toJson(dbo: DBObject) = {
        JsonObject(
	      "date" -> JsonString(dbo.get("_id").toString), 
	      "value" -> JsonLong(java.lang.Double.parseDouble(dbo.get("value").toString).toLong)
      )
    }

    JsonObject(
        "date-start" -> JsonString("2000-01-01"), 
        "date-end" -> JsonString("2012-12-31"),
        "data" -> JsonArray( report.map(toJson _) )            
    ).success
  }
  

}
