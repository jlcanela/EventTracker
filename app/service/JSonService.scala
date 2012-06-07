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

import net.debasishg.sjson.json.JsonSerialization
import scalaz._
import Scalaz._

trait JSonService[Json] extends JsonSerialization[Json] {

  import jsonImplementation._

  val mf: Manifest[Json]

  def stringToJson(s: String): Json = {
    s.deserialize[Json]
  }

  def j2s(j: Json): String = j.serialize[String]

  // error messages
  def jSendFail(data: Json) : Json = JsonObject("status" -> JsonString("fail"), "data" -> data)

  def jSendFail(errors: scalaz.NonEmptyList[String]): Json = errors.list match {
    case Seq(s) => jSendFail(JsonString(s))
    case l: Seq[_] => jSendFail(JsonArray(l.map(e => JsonString(e.toString))))
  }
  def jSendFail(reason: Map[String, String]): Json = jSendFail(JsonObject(reason.map({ case (key, value) => (key, JsonString(value)) })))

  // success
  def jSendSuccess(data: Json): Json = JsonObject("status" -> JsonString("ok"), "data" -> data)

  def displayResult(r: ValidationNEL[String, Json]) : Json = r match {
    case Success(data) => jSendSuccess(data)
    case Failure(f) => jSendFail(f)
  }

}

