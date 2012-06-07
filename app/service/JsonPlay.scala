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

import rosetta.json._

import play.api.libs.json._

import rosetta.json._

object JsonPlay extends JsonImplementation[JsValue] {
  val JsonToString: rosetta.io.Serializer[JsValue, String] = new rosetta.io.Serializer[JsValue, String] {
    def serialize(v: JsValue): String = Json.stringify(v)

    def deserialize(v: String): JsValue = if (v.trim.length == 0) JsNull else Json.parse(v)
  }

  implicit val BooleanToJson: JsonSerializer[Boolean] = new JsonSerializer[Boolean] {
    def serialize(v: Boolean): JsValue = JsBoolean(v)

    def deserialize(v: JsValue): Boolean = v match {
      case JsBoolean(v) => v

      case _ => sys.error("Expected Boolean but found: " + v)
    }
  }

  implicit val StringToJson: JsonSerializer[String] = new JsonSerializer[String] {
    def serialize(v: String): JsValue = JsString(v)

    def deserialize(v: JsValue): String = v match {
      case JsString(v) => v

      case _ => sys.error("Expected String but found: " + v)
    }
  }

  implicit val LongToJson: JsonSerializer[Long] = new JsonSerializer[Long] {
    def serialize(v: Long): JsValue = JsNumber(v)

    def deserialize(v: JsValue): Long = v match {
      case JsNumber(v) => v.toLong

      case _ => sys.error("Expected Long but found: " + v)
    }
  }

  implicit val DoubleToJson: JsonSerializer[Double] = new JsonSerializer[Double] {
    def serialize(v: Double): JsValue = JsNumber(v)

    def deserialize(v: JsValue): Double = v match {
      case JsNumber(v) => v.toDouble

      case _ => sys.error("Expected Double but found: " + v)
    }
  }

  implicit def ObjectToJson[A](implicit serializer: JsonSerializer[A]): JsonSerializer[Iterable[(String, A)]] = new JsonSerializer[Iterable[(String, A)]] {
    def serialize(v: Iterable[(String, A)]): JsValue = JsObject(v.toList.map { field =>
      (field._1, serializer.serialize(field._2))
    })

    def deserialize(v: JsValue): Iterable[(String, A)] = v match {
      case JsObject(fields) => fields.map { field =>
        (field._1, serializer.deserialize(field._2))
      }

      case _ => sys.error("Expected Object but found: " + v)
    }
  }

  implicit def ArrayToJson[A](implicit serializer: JsonSerializer[A]): JsonSerializer[Iterable[A]] = new JsonSerializer[Iterable[A]] {
    def serialize(v: Iterable[A]): JsValue = JsArray(v.toList.map(serializer.serialize _))

    def deserialize(v: JsValue): Iterable[A] = v match {
      case JsArray(elements) => elements.map(serializer.deserialize _)

      case _ => sys.error("Expected Array but found: " + v)
    }
  }

  implicit def OptionToJson[A](implicit serializer: JsonSerializer[A]): JsonSerializer[Option[A]] = new JsonSerializer[Option[A]] {
    def serialize(v: Option[A]): JsValue = v match {
      case None => JsNull

      case Some(v) => serializer.serialize(v)
    }

    def deserialize(v: JsValue): Option[A] = v match {
      case JsNull => None

      case v => Some(serializer.deserialize(v))
    }
  }

  def foldJson[Z](json: JsValue, matcher: JsonMatcher[Z]): Z = json match {
    case JsNull => matcher._1()
    case JsUndefined(_) => matcher._1()

    case JsBoolean(v)   => matcher._2(v)
    //case JsNumber(v)    => matcher._3(v.toLong)
    case JsNumber(v) => matcher._4(v.toDouble)
    case JsString(v) => matcher._5(v)
    case JsArray(v)  => matcher._6(v)
    case JsObject(v) => matcher._7(v.map(field => (field._1, field._2)))

    case _ : JsValue => sys.error("Cannot fold over JsValue")
  }
}
