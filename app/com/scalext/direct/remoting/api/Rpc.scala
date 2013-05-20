package com.scalext.direct.remoting.api

import play.api.libs.json.Json
import play.api.libs.json.JsValue
import play.api.libs.json.JsObject
import play.api.libs.json.JsArray

case class Rpc(id: Int, action: String, method: String, var data: Any) {
  def toJson: JsObject = Json.obj(
    "type" -> "rpc",
    "tid" -> id,
    "action" -> action,
    "method" -> method)
}

case class RpcResult(rpc: Rpc, result: JsObject) {
  def toJson = rpc.toJson ++ result
}