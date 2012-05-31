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

import net.debasishg.sjson.json._
import org.joda.time.{DateTime, LocalDate}
import rosetta.json.JsonImplementation
import scalaz.camel.core._

import scalaz._
import Scalaz._

trait SerializationHelper[Json] { self: DefaultProtocol[Json] with JsonSerialization[Json] =>

  val jsonImplementation: JsonImplementation[Json]
  import jsonImplementation._

  implicit object DateTimeFormat extends Format[DateTime, Json] {
    def writes(o: DateTime) = JsonString(o.toString).success
    def reads(json: Json) : ValidationNEL[String, DateTime]= json match {
      case JsonString(s) => try { 
        new DateTime(s).success
        } catch {
          case ex => "invalid date time format (%s) : %s".format(s, ex.getMessage).fail.liftFailNel
        }
      case JsonNull => "missing expected date time".fail.liftFailNel
      case x => "invalid date time format(%s)".format(x).fail.liftFailNel
    }
  }

  implicit object LocalDateFormat extends Format[LocalDate, Json] {
    def writes(o: LocalDate) = JsonString(o.toString).success
    def reads(json: Json) : ValidationNEL[String, LocalDate]= json match {
      case JsonString(s) => try { 
        new LocalDate(s).success
        } catch {
          case ex => "invalid local date format (%s) : %s".format(s, ex.getMessage).fail.liftFailNel
        }
      case JsonNull => "missing expected local date ".fail.liftFailNel
      case x => "invalid local date format(%s)".format(x).fail.liftFailNel
    }
  }

  def asProduct9[S, T1, T2, T3, T4, T5, T6, T7, T8, T9](f1: String, f2: String, f3: String, f4: String, f5: String, f6: String, f7: String, f8: String, f9: String)(apply: (T1, T2, T3, T4, T5, T6, T7, T8, T9) => S)(unapply: S => Product9[T1, T2, T3, T4, T5, T6, T7, T8, T9])(implicit bin1: Format[T1, Json], bin2: Format[T2, Json], bin3: Format[T3, Json], bin4: Format[T4, Json], bin5: Format[T5, Json], bin6: Format[T6, Json], bin7: Format[T7, Json], bin8: Format[T8, Json], bin9: Format[T9, Json]) = new Format[S, Json] {

    def writes(s: S) = {
      val product = unapply(s)
      List(
        f1.success <|*|> tojson(product._1),
        f2.success <|*|> tojson(product._2),
        f3.success <|*|> tojson(product._3),
        f4.success <|*|> tojson(product._4),
        f5.success <|*|> tojson(product._5),
        f6.success <|*|> tojson(product._6),
        f7.success <|*|> tojson(product._7),
        f8.success <|*|> tojson(product._8),
        f9.success <|*|> tojson(product._9)).sequence[({ type λ[α] = ValidationNEL[String, α] })#λ, (String, Json)] match {
          case Success(kvs) =>
            JsonObject(Map.empty[String, Json] ++ kvs.map { case (k, v) => (k.asInstanceOf[String], v) }).success
          case Failure(errs) => errs.fail
        }
    }
    def reads(js: Json) = js match {
      case m @ JsonObject(_) => // m is the Map
        (
          field[T1](f1, m) |@|
          field[T2](f2, m) |@|
          field[T3](f3, m) |@|
          field[T4](f4, m) |@|
          field[T5](f5, m) |@|
          field[T6](f6, m) |@|
          field[T7](f7, m) |@|
          field[T8](f8, m) |@|
          field[T9](f9, m)) { apply }
      case _ => "object expected".fail.liftFailNel
    }
  }
  def asProduct10[S, T1, T2, T3, T4, T5, T6, T7, T8, T9, T10](f1: String, f2: String, f3: String, f4: String, f5: String, f6: String, f7: String, f8: String, f9: String, f10: String)(apply: (T1, T2, T3, T4, T5, T6, T7, T8, T9, T10) => S)(unapply: S => Product10[T1, T2, T3, T4, T5, T6, T7, T8, T9, T10])(implicit bin1: Format[T1, Json], bin2: Format[T2, Json], bin3: Format[T3, Json], bin4: Format[T4, Json], bin5: Format[T5, Json], bin6: Format[T6, Json], bin7: Format[T7, Json], bin8: Format[T8, Json], bin9: Format[T9, Json], bin10: Format[T10, Json]) = new Format[S, Json] {

    def writes(s: S) = {
      val product = unapply(s)
      List(
        f1.success <|*|> tojson(product._1),
        f2.success <|*|> tojson(product._2),
        f3.success <|*|> tojson(product._3),
        f4.success <|*|> tojson(product._4),
        f5.success <|*|> tojson(product._5),
        f6.success <|*|> tojson(product._6),
        f7.success <|*|> tojson(product._7),
        f8.success <|*|> tojson(product._8),
        f9.success <|*|> tojson(product._9),
        f10.success <|*|> tojson(product._10)).sequence[({ type λ[α] = ValidationNEL[String, α] })#λ, (String, Json)] match {
          case Success(kvs) =>
            JsonObject(Map.empty[String, Json] ++ kvs.map { case (k, v) => (k.asInstanceOf[String], v) }).success
          case Failure(errs) => errs.fail
        }
    }
    def reads(js: Json) = js match {
      case m @ JsonObject(_) => // m is the Map
        (
          field[T1](f1, m) |@|
          field[T2](f2, m) |@|
          field[T3](f3, m) |@|
          field[T4](f4, m) |@|
          field[T5](f5, m) |@|
          field[T6](f6, m) |@|
          field[T7](f7, m) |@|
          field[T8](f8, m) |@|
          field[T9](f9, m) |@|
          field[T10](f10, m)) { apply }
      case _ => "object expected".fail.liftFailNel
    }
  }

  def asProduct11[S, T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11](f1: String, f2: String, f3: String, f4: String, f5: String, f6: String, f7: String, f8: String, f9: String, f10: String, f11: String)(apply: (T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11) => S)(unapply: S => Product11[T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11])(implicit bin1: Format[T1, Json], bin2: Format[T2, Json], bin3: Format[T3, Json], bin4: Format[T4, Json], bin5: Format[T5, Json], bin6: Format[T6, Json], bin7: Format[T7, Json], bin8: Format[T8, Json], bin9: Format[T9, Json], bin10: Format[T10, Json], bin11: Format[T11, Json]) = new Format[S, Json] {

    def writes(s: S) = {
      val product = unapply(s)
      List(
        f1.success <|*|> tojson(product._1),
        f2.success <|*|> tojson(product._2),
        f3.success <|*|> tojson(product._3),
        f4.success <|*|> tojson(product._4),
        f5.success <|*|> tojson(product._5),
        f6.success <|*|> tojson(product._6),
        f7.success <|*|> tojson(product._7),
        f8.success <|*|> tojson(product._8),
        f9.success <|*|> tojson(product._9),
        f10.success <|*|> tojson(product._10),
        f11.success <|*|> tojson(product._11)).sequence[({ type λ[α] = ValidationNEL[String, α] })#λ, (String, Json)] match {
          case Success(kvs) =>
            JsonObject(Map.empty[String, Json] ++ kvs.map { case (k, v) => (k.asInstanceOf[String], v) }).success
          case Failure(errs) => errs.fail
        }
    }
    def reads(js: Json) = js match {
      case m @ JsonObject(_) => // m is the Map
        (
          field[T1](f1, m) |@|
          field[T2](f2, m) |@|
          field[T3](f3, m) |@|
          field[T4](f4, m) |@|
          field[T5](f5, m) |@|
          field[T6](f6, m) |@|
          field[T7](f7, m) |@|
          field[T8](f8, m) |@|
          field[T9](f9, m) |@|
          field[T10](f10, m) |@|
          field[T11](f11, m)) { apply }
      case _ => "object expected".fail.liftFailNel
    }
  }

  def asProduct12[S, T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12](f1: String, f2: String, f3: String, f4: String, f5: String, f6: String, f7: String, f8: String, f9: String, f10: String, f11: String, f12: String)(apply: (T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12) => S)(unapply: S => Product12[T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12])(implicit bin1: Format[T1, Json], bin2: Format[T2, Json], bin3: Format[T3, Json], bin4: Format[T4, Json], bin5: Format[T5, Json], bin6: Format[T6, Json], bin7: Format[T7, Json], bin8: Format[T8, Json], bin9: Format[T9, Json], bin10: Format[T10, Json], bin11: Format[T11, Json], bin12: Format[T12, Json]) = new Format[S, Json] {

    def writes(s: S) = {
      val product = unapply(s)
      List(
        f1.success <|*|> tojson(product._1),
        f2.success <|*|> tojson(product._2),
        f3.success <|*|> tojson(product._3),
        f4.success <|*|> tojson(product._4),
        f5.success <|*|> tojson(product._5),
        f6.success <|*|> tojson(product._6),
        f7.success <|*|> tojson(product._7),
        f8.success <|*|> tojson(product._8),
        f9.success <|*|> tojson(product._9),
        f10.success <|*|> tojson(product._10),
        f11.success <|*|> tojson(product._11),
        f12.success <|*|> tojson(product._12)).sequence[({ type λ[α] = ValidationNEL[String, α] })#λ, (String, Json)] match {
          case Success(kvs) =>
            JsonObject(Map.empty[String, Json] ++ kvs.map { case (k, v) => (k.asInstanceOf[String], v) }).success
          case Failure(errs) => errs.fail
        }
    }
    def reads(js: Json) = js match {
      case m @ JsonObject(_) => // m is the Map
        (
          field[T1](f1, m) |@|
          field[T2](f2, m) |@|
          field[T3](f3, m) |@|
          field[T4](f4, m) |@|
          field[T5](f5, m) |@|
          field[T6](f6, m) |@|
          field[T7](f7, m) |@|
          field[T8](f8, m) |@|
          field[T9](f9, m) |@|
          field[T10](f10, m) |@|
          field[T11](f11, m) |@|
          field[T12](f12, m)) { apply }
      case _ => "object expected".fail.liftFailNel
    }
  }

}