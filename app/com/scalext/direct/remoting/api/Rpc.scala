package com.scalext.direct.remoting.api

import play.api.libs.json.Json
import play.api.libs.json.JsValue

case class Rpc(id: Int, action: String, method: String, result: Any) {
  def toJson: JsValue = Json.obj(
    "type" -> "rpc",
    "tid" -> id,
    "action" -> action,
    "method" -> method,
    "result" -> result.toString)
}