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

import service.SerializationHelper
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

