package com.scalext.direct.remoting.api

import play.api.libs.json.Json
import play.api.libs.json.JsBoolean

case class Method(var name: String, var numberOfParameters: Int = 0, var formHandler: Boolean = false) extends ApiObject {
  def toDirectApi = {
    var result = Json.obj(
      "name" -> name,
      "len" -> numberOfParameters)
    if (formHandler) result += "formHandler" -> JsBoolean(true)
    result
  }
}