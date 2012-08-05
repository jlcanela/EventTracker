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

import org.specs2._
import play.api.test.{FakeRequest, FakeApplication}
import specification._

import controllers.routes
//import models.{AppDB, Bar}

//import org.scalatest.FlatSpec
//import org.scalatest.matchers.ShouldMatchers

//import org.squeryl.PrimitiveTypeMode.inTransaction

import play.api.http.ContentTypes.JSON
import play.api.test._
import play.api.test.Helpers._


class EventRegistrationSpec extends Specification with ApiClient {

  def is =

  "This is a specification for the event registration feature" ^
    p ^
    "The event registration feature enables" ^
    "using client API to register a valid event" ! registerValidEvent ^
      // Unable to test registerNonJsonEvent calling directly the play Action
      // "using client API to generate error when event is not a valid json structure" ! registerNonJsonEvent ^
    "using client API to generate error when event is missing either event type or userId" ! registerWithMissingData ^
    end

  val aValidEvent =
    """
      |{
      |    "eventType": "userArrival",
      |    "userId": "user:7",
      |    "searchKeyword": "event",
      |    "referringWebsite": "http://www.google.com",
      |    "productId": "product:1",
      |    "category": "Laptop"
      |}
    """.stripMargin

  val anInvalidEvent =
    """
      |{
      |    "searchKeyword": "event",
      |    "referringWebsite": "http://www.google.com",
      |    "productId": "product:1",
      |    "category": "Laptop"
      |}
    """.stripMargin

  val serviceUri = "/event"

  def registerValidEvent = running(FakeApplication()) {
    val r = registerEvent(aValidEvent)
    contentAsString(r) must contain(""""status":"ok"""")
  }

  /*def registerNonJsonEvent = running(FakeApplication()) {
    val r = registerEvent("Non JSon Event")
    //status(r) must be equalTo OK
    r must startWith("""{"status":"fail","data":"unknown token N""")
  }*/

  def registerWithMissingData = running(FakeApplication()) {
    val r = registerEvent(anInvalidEvent)
    contentAsString(r) must be equalTo("""{"status":"fail","data":["eventType is mandatory","userId is mandatory"]}""")
  }


}