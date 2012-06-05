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

import dispatch._

trait ApiClient {

  private def localhost = host("localhost", 8080)

  def registerEvent(event: String) : String = {
    val req = (localhost / "event").POST.setBody(event)
    Http(req > As.string).apply()
  }

  def getReport(report : String = "userArrival") = {
    val req = (localhost / "reports" / report)
    Http(req > As.string).apply()
  }
}
