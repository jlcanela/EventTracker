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

import com.mongodb._
import rosetta.json._

object MongoJson extends JsonImplementation[AnyRef] {
  
  val JsonToString: rosetta.io.Serializer[AnyRef, String] = new rosetta.io.Serializer[AnyRef, String] {
    def serialize(v: AnyRef): String = v.toString

    def deserialize(v: String): AnyRef = if (v.trim.length == 0) new BasicDBObject else com.mongodb.util.JSON.parse(v)
  }

  implicit val BooleanToJson: JsonSerializer[Boolean] = new JsonSerializer[Boolean] {
    def serialize(v: Boolean): AnyRef = new java.lang.Boolean(v)

    def deserialize(v: AnyRef): Boolean = v match {
      case b : java.lang.Boolean => b

      case _ => sys.error("Expected Boolean but found: " + v)
    }
  }

  implicit val StringToJson: JsonSerializer[String] = new JsonSerializer[String] {
    def serialize(v: String): AnyRef = v

    def deserialize(v: AnyRef): String = v match {
      case s : String => s

      case _ => sys.error("Expected String but found: " + v)
    }
  }

  implicit val LongToJson: JsonSerializer[Long] = new JsonSerializer[Long] {
    def serialize(v: Long): AnyRef = new java.lang.Long(v)

    def deserialize(v: AnyRef): Long = v match {
      case l : java.lang.Long => l
      case i : java.lang.Integer => i.toLong

      case _ => sys.error("Expected Long but found: " + v)
    }
  }

  implicit val DoubleToJson: JsonSerializer[Double] = new JsonSerializer[Double] {
    def serialize(v: Double): AnyRef = new java.lang.Double(v)

    def deserialize(v: AnyRef): Double = v match {
      case d : java.lang.Double => d
      case i : java.lang.Integer => i.toDouble

      case _ => sys.error("Expected Double but found: " + v)
    }
  }

  implicit def ObjectToJson[A](implicit serializer: JsonSerializer[A]): JsonSerializer[Iterable[(String, A)]] = new JsonSerializer[Iterable[(String, A)]] {
    import scala.collection.JavaConversions._
    
    def serialize(v: Iterable[(String, A)]): AnyRef = {
      val coll : Map[String, AnyRef] = Map(v.toList.map { case (key, value) =>
      	(key, serializer.serialize(value))
      }:_*)
      new BasicDBObject(mapAsJavaMap(coll))
    } 
    def deserialize(v: AnyRef): Iterable[(String, A)] = v match {
      case dbo : BasicDBObject => { 
        dbo.keySet.toSet.map( (key : String) =>  (key, serializer.deserialize(dbo.get(key))))
      }

      case _ => sys.error("Expected Object but found: " + v)
    }
  }

  implicit def ArrayToJson[A](implicit serializer: JsonSerializer[A]): JsonSerializer[Iterable[A]] = new JsonSerializer[Iterable[A]] {
    import scala.collection.JavaConversions._

    def serialize(v: Iterable[A]): AnyRef = {
      val list = new BasicDBList
      val coll : java.util.Collection[AnyRef] = v.toList.map(serializer.serialize _)
      (new BasicDBList).addAll(coll)
      list
    }

    def deserialize(v: AnyRef): Iterable[A] = v match {
      case dbo : BasicDBList => dbo.toArray.map(serializer.deserialize _)

      case _ => sys.error("Expected Array but found: " + v)
    }
  }

  implicit def OptionToJson[A](implicit serializer: JsonSerializer[A]): JsonSerializer[Option[A]] = new JsonSerializer[Option[A]] {
    def serialize(v: Option[A]): AnyRef = v match {
      case None => null

      case Some(v) => serializer.serialize(v)
    }

    def deserialize(v: AnyRef): Option[A] = v match {
      case null => None

      case v => Some(serializer.deserialize(v))
    }
  }

  def foldJson[Z](json: AnyRef, matcher: JsonMatcher[Z]): Z = json match {
    case null => matcher._1()

    case v: java.lang.Boolean  => matcher._2(v)
    case v: java.lang.Integer    => matcher._3(v.toLong)
    case v: java.lang.Double => matcher._4(v)
    case v: String => matcher._5(v)
    case v: BasicDBList  => matcher._6(v.toArray)
    case v: BasicDBObject => {
         import scala.collection.JavaConversions._
         val tuples: List[(String, AnyRef)] = v.toList
         matcher._7(tuples) 
    }

    case x => sys.error("Cannot fold over %s".format(x.getClass.getName))
  }
}
