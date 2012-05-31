package com.aclys.eventtracker.service.event

import com.aclys.eventtracker.service.SerializationHelper
import net.debasishg.sjson.json._
import org.joda.time.{DateTime, LocalDate}
import rosetta.json.JsonImplementation
import scalaz.Scalaz.mkIdentity

trait EventProtocol[Json] extends DefaultProtocol[Json] with SerializationHelper[Json] { self: JsonSerialization[Json] =>
  val jsonImplementation: JsonImplementation[Json]
  import jsonImplementation._


  object EventProtocol {

    case class Event(creationDate: DateTime, eventType: String, userId: String, data: Json)    
    implicit val EventFormat: Format[Event, Json] =
      asProduct4("creationDate", "eventType", "userId", "data")(Event)(Event.unapply(_).get)

  }
}

