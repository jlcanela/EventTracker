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

import net.debasishg.sjson.json.JsonSerialization
import scalaz.Scalaz._
import scalaz.camel.core.Camel
import scalaz.camel.core.Router
import scalaz.camel.core.Message
import scalaz._

import org.apache.camel.Exchange

trait JSonService[Json] extends JsonSerialization[Json] {
  this: Camel =>

  import jsonImplementation._

  implicit val router: Router
  val mf: Manifest[Json]

  def liftMessage[T](f: Message => ValidationNEL[String, T]): MessageProcessor = (m: Message, k: MessageValidation => Unit) =>
    (try {
      f(m)
    } catch {
      case ex =>
        ex.printStackTrace
        ex.getMessage.fail.liftFailNel
    }) match {
      case Success(ok) =>
        k(m.setBody(ok).success)
      case f @ Failure(_) =>
        k(m.setException(ValidationException(f)).fail)
    }

  def postJson(m: Message) = m.headers("CamelHttpMethod") match {
    case "POST" => stringToJson(m.bodyAs[String]).success
    case _ => "not a post method".fail.liftFailNel
  }

  def stringToJson(s: String): Json = {
    s.deserialize[Json]
  }

  def j2s(j: Json): String = j.serialize[String]
  val jsonToString = liftMessage((m: Message) => j2s(m.bodyAs[Json](mf, router)).success)
  
  // error messages
  def jSendFail(data: Json): String = JsonObject("status" -> JsonString("fail"), "data" -> data).serialize[String]
  def jSendFail(errors: scalaz.NonEmptyList[String]): String = errors.list match {
    case Seq(s) => jSendFail(JsonString(s))
    case l: Seq[_] => jSendFail(JsonArray(l.map(e => JsonString(e.toString))))
  }
  def jSendFail(reason: Map[String, String]): String = jSendFail(JsonObject(reason.map({ case (key, value) => (key, JsonString(value)) })))

  val displayError: AttemptHandler1 = {
    case ValidationException(Failure(e)) => { m: Message => m.setBody(jSendFail(e)) }
    case ValidationException(Success(e)) => { m: Message => m.setBody(jSendFail(JsonString("ERROR 500_X1"))) }
    case e: Exception => { m: Message => m.setBody(jSendFail(JsonString("EX (%s) :".format(e.getClass.getName, e.getMessage)))) } //>=> failWith(e)
  }

}

