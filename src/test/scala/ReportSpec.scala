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

class ReportSpec extends Specification with CamelRunner with ApiClient {

  override def map(fs: =>Fragments) = Step(start) ^ super.map(fs) ^
    Step(stop)

  def is =

  "This is a specification for the userArrival Event report"                                      ^
    p^
    "The userArrival Event report enables"                                                      ^
    "using client API to get a report"                                            ! reportIsAccessible^
    end

  val reportTemplate =
    """
      |{
      |    "date-start": "2012-05-31",
      |    "date-end": "2012-06-01",
      |    "data": [{
      |        "date": "2012-05-31",
      |        "value": 1
      |    },
      |    {
      |        "date": "2012-06-01",
      |        "value": 4
      |    }]
      |}
    """.stripMargin

  val serviceUri = "reports/userArrival"

  def reportIsAccessible = {
    val r = getReport()
    r must startWith("""{"date-start":"2000-01-01","date-end":"2012-12-31","data":[{"date":""")
  }

}