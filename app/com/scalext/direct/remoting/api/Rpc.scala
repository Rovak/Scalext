package com.scalext.direct.remoting.api

import play.api.libs.json.Json
import play.api.libs.json.Json.toJsFieldJsValueWrapper

/** Holds information given by a Direct API request
  *
  * A request can hold multiple RPC requests
  */
case class Rpc(id: Int, action: String, method: String, var data: Any) {
  def toJson = Json.obj(
    "type" -> "rpc",
    "tid" -> id,
    "action" -> action,
    "method" -> method)
}

/** Contains a RPC request and its result
  */
case class RpcResult(rpc: Rpc, result: Any) {
  def toJson = rpc.toJson
}