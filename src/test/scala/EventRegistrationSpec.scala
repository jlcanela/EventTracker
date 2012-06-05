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

import com.aclys.eventtracker.service.CamelRunner
import org.specs2._
import specification._

class EventRegistrationSpec extends Specification with CamelRunner with ApiClient {

  override def map(fs: =>Fragments) = Step(start) ^ super.map(fs) ^
    Step(stop)

  def is =

  "This is a specification for the event registration feature" ^
    p ^
    "The event registration feature enables" ^
    "using client API to register a valid event" ! registerValidEvent ^
    "using client API to generate error when event is not a valid json structure" ! registerNonJsonEvent ^
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

  def registerValidEvent = {
    val r = registerEvent(aValidEvent)
    r must startWith("""{"creationDate":"201""")
  }

  def registerNonJsonEvent = {
    val r = registerEvent("Non JSon Event")
    r must startWith("""{"status":"fail","data":"unknown token N""")
  }

  def registerWithMissingData = {
    val r = registerEvent(anInvalidEvent)
    r must be equalTo("""{"status":"fail","data":["eventType is mandatory","userId is mandatory"]}""")
  }

}