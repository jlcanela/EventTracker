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

package controllers

import play.api._
import play.api.mvc._

import play.api.data._
import play.api.data.Forms._
import play.api.libs.json._

import scalaz._
import Scalaz._

import _root_.service.Service

object Application extends Controller {

  val service = new Service

  def index = Action {
    Ok("Hello World") //views.html.index("Your new application is ready."))
  }

  def event = Action(parse.json) {  implicit request =>

    Ok(service.displayResult(service.registerEvent(request.body)))
  }

  def userArrival = Action {
    val report : JsValue = service.displayResult(service.userArrivalReport)
    Ok(report)
  }

}