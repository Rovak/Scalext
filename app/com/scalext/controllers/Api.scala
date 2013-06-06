package com.scalext.controllers

import com.google.gson.FieldNamingPolicy
import com.google.gson.GsonBuilder
import com.scalext.direct.dispatcher.{ParallelDispatcher, StandardDispatcher}
import com.scalext.direct.remoting.FormResult
import com.scalext.direct.remoting.api.ApiFactory
import com.scalext.direct.remoting.api.Rpc
import com.scalext.direct.remoting.api.RpcResult

import play.api.Play.current
import play.api.libs.json.JsArray
import play.api.libs.json.JsNull
import play.api.libs.json.JsObject
import play.api.libs.json.JsValue
import play.api.libs.json.Json
import play.api.libs.json.Json.toJsFieldJsValueWrapper
import play.api.mvc.Action
import play.api.mvc.Controller

object Api extends Controller {

  def isDebugMode = (play.api.Play.mode == play.api.Mode.Dev)
  val dispatcher = new ParallelDispatcher(ApiFactory.classes)
  val gson = new GsonBuilder()
    .setFieldNamingPolicy(FieldNamingPolicy.LOWER_CASE_WITH_UNDERSCORES)
    .create()

  /** Convert any result to a valid Direct result
    */
  def resultToJson(result: Any): JsValue = {
    result match {
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
      case list: Seq[Any] =>
        Json.parse(gson.toJson(list.toArray[Any]))
      case map: Map[_, _] =>
        Json.toJson(map.asInstanceOf[Map[String, String]])
      case null =>
        JsNull
      case _ =>
        Json.parse(gson.toJson(result))
    }
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
    val postData = post.map(row => (row._1 -> row._2.mkString))

    Rpc(
      id = postData("extTID").toInt,
      action = postData("extAction"),
      method = postData("extMethod"),
      data = Json.arr(Json.toJson(filterExtKeys(postData))))
  }

  var extKeys = Array("extType", "extUpload", "extMethod", "extTID", "extAction")

  /** Filter post keys which are given by the standard Ext JS Remoting Provider so this data is not being
    * passed to the direct method which is being called
    */
  def filterExtKeys(data: Map[String, String]): Map[String, String] = {
    data.filterNot(key => extKeys.contains(key._1))
  }

  /** Execute a JSON request
    */
  def executeApi = Action { request =>
    try {
      val rpcResults: Seq[Rpc] = request.contentType.get match {
        // Default JSON
        case "application/json" =>
          request.body.asJson.get match {
            case JsArray(elements) =>
              elements.map(buildRpc(_))
            case obj: JsObject =>
              List(buildRpc(obj))
          }
        // Form Submit
        case "application/x-www-form-urlencoded" =>
          List(buildRpc(request.body.asFormUrlEncoded.get))
        // Form Upload
        case "multipart/form-data" =>
          val postBody = request.body.asMultipartFormData.get
          val post = postBody.asFormUrlEncoded
          val rpc = buildRpc(post)
          rpc.data = List[Any](
            filterExtKeys(post.map(row => (row._1 -> row._2.mkString))),
            postBody.files.map(_.ref))
          List(rpc)
        case _ =>
          throw new Exception("Invalid Request")
      }

      val results = dispatcher.dispatch(rpcResults)

      if (results.size > 1)
        Ok(Json.toJson(results.map(resultToJson(_))))
      else
        Ok(resultToJson(results.head))

    } catch {
      // Debug Mode
      case e: Exception if isDebugMode =>
        Ok(Json.obj(
          "type" -> "exception",
          "message" -> e.getMessage,
          "where" -> e.getStackTraceString))
      // Production Mode
      case e: Exception =>
        Ok(Json.obj(
          "type" -> "exception",
          "message" -> "An unhandled exception occured",
          "where" -> ""))
    }
  }

  def buildFormResponse(rpc: String) = {
    val response = rpc.replace("\"", "\\\"")
    s"<html><body><textarea>$response</textarea></body></html>"
  }

}