package com.scalext.direct

import play.api.libs.json.{JsArray, Json, JsValue}
import com.scalext.direct.remoting.{RpcError, Rpc, FormResult, RpcResult}
import com.scalext.Serialization

object RPCFactory {

  val extKeys = Array("extType", "extUpload", "extMethod", "extTID", "extAction")

  /** Convert any result to a valid Direct result
    */
  def resultToJson(result: Any): JsValue = result match {
    case RpcResult(rpc, RpcError(error)) =>
      rpc.toJson ++ Json.obj("error" -> error)
    case RpcResult(rpc, data) =>
      rpc.toJson ++ Json.obj("result" -> resultToJson(data))
    case FormResult(formResult, success, errors) =>
      var jsonResult = Json.obj(
        "success" -> (errors.isEmpty && success),
        "data" -> resultToJson(formResult))
      if (!errors.isEmpty)
        jsonResult += "errors" -> errors.foldLeft(Json.obj()) {
          case (current, (key, value)) =>
            current ++ Json.obj(key -> value)
        }
      jsonResult
    case _ =>
      Serialization.toJson(result)
  }

  /** Build and execute an RPC request from the given JSON object
    *
    * @param rpc a single RPC
    */
  def buildRpc(rpc: JsValue): Rpc = {
    Rpc(
      id = (rpc \ "tid").as[Int],
      action = (rpc \ "action").as[String],
      method = (rpc \ "method").as[String],
      data = (rpc \ "data") match {
        case arr: JsArray => arr
        case _ => Json.arr()
      })
  }

  /** Build and execute a RPC request from the given Ext Direct Form Request
    *
    * @param post Map with Key => Value
    */
  def buildRpc(post: Map[String, Seq[String]]) = {

    // Convert seq to string
    val postData = post.map(row => row._1 -> row._2.mkString)

    Rpc(
      id = postData("extTID").toInt,
      action = postData("extAction"),
      method = postData("extMethod"),
      data = Json.arr(Json.toJson(filterExtKeys(postData))))
  }

  /** Filter post keys which are given by the standard Ext JS Remoting Provider so this data is not being
    * passed to the direct method which is being called
    */
  def filterExtKeys(data: Map[String, String]): Map[String, String] = {
    data.filterNot(key => extKeys.contains(key._1))
  }
}
