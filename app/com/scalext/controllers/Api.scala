package com.scalext.controllers

import com.google.gson.FieldNamingPolicy
import com.google.gson.GsonBuilder
import com.scalext.direct.dispatcher.StandardDispatcher
import com.scalext.direct.remoting.api.ApiFactory
import com.scalext.direct.remoting.api.FormResult
import com.scalext.direct.remoting.api.Rpc
import play.api.Play.current
import play.api.libs.json.JsArray
import play.api.libs.json.JsNull
import play.api.libs.json.JsObject
import play.api.libs.json.JsValue
import play.api.libs.json.Json
import play.api.libs.json.Json.toJsFieldJsValueWrapper
import play.api.mvc.Action
import play.api.mvc.Controller
import com.scalext.direct.remoting.api.RpcResult

object Api extends Controller {

  def isDebugMode = (play.api.Play.mode == play.api.Mode.Dev)

  val dispatcher = new StandardDispatcher(ApiFactory.getClasses())

  val gson = new GsonBuilder()
    .setFieldNamingPolicy(FieldNamingPolicy.LOWER_CASE_WITH_UNDERSCORES)
    .create()

  /**
   * Convert any result to a valid Direct result
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

  /**
   * Build and execute an RPC request from the given JSON object
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

  /**
   * Build and execute an RPC request from the given FORM Request
   *
   * @param rpc a single RPC
   */
  def buildRpc(post: Map[String, Seq[String]]): Rpc = {
    var postData = post.map(row => (row._1 -> row._2.mkString))

    Rpc(
      id = postData("extTID").toInt,
      action = postData("extAction"),
      method = postData("extMethod"),
      data = Json.arr(Json.toJson(filterExtKeys(postData))))
  }

  var extKeys = Array("extType", "extUpload", "extMethod", "extTID", "extAction")

  def filterExtKeys(data: Map[String, String]): Map[String, String] = {
    data.filterNot(key => extKeys.contains(key._1))
  }

  /**
   * Execute a JSON request
   */
  def executeApi = Action { request =>
    try {
      var rpcResults: Seq[Rpc] = request.contentType.get match {
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
          var postBody = request.body.asMultipartFormData.get
          var post = postBody.asFormUrlEncoded
          var rpc = buildRpc(post)
          rpc.data = List[Any](
            filterExtKeys(post.map(row => (row._1 -> row._2.mkString))),
            postBody.files.map(_.ref))
          List(rpc)
        case _ =>
          throw new Exception("Invalid Request")
      }

      var results = dispatcher.dispatch(rpcResults)

      if (results.size > 1)
        Ok(Json.toJson(results.map(resultToJson(_))))
      else
        Ok(resultToJson(results.head))

    } catch {
      // Debug Mode
      case e: Exception if isDebugMode =>
        Ok(Json.obj(
          "type" -> "exception",
          "mesage" -> e.getMessage(),
          "where" -> e.getStackTraceString))
      // Production Mode
      case e: Exception =>
        Ok(Json.obj(
          "type" -> "exception",
          "mesage" -> "An unhandled exception occured",
          "where" -> ""))
    }
  }

  def buildFormResponse(rpc: String) = {
    var response = rpc.replace("\"", "\\\"")
    s"<html><body><textarea>$response</textarea></body></html>"
  }

}