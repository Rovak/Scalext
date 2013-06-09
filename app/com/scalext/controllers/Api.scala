package com.scalext.controllers

import com.scalext.direct.dispatcher.ParallelDispatcher
import com.scalext.direct.remoting.api._
import play.api.Play.current
import play.api.libs.json._
import play.api.mvc.{Controller, Action}
import com.scalext.direct.remoting.{RpcResult, Rpc, FormResult}
import com.scalext.Serialization

/** Api Controller
  */
object Api extends Controller {

  def isDebugMode = play.api.Play.mode == play.api.Mode.Dev

  val dispatcher = new ParallelDispatcher(ApiFactory.classes)


  /** Build the javascript API
    */
  def buildApi = Action {
    val result = s"Ext.direct.Manager.addProvider(${Json.stringify(ApiFactory.config.toJson)});";
    Ok(result).as("text/javascript")
  }

  /** Convert any result to a valid Direct result
    */
  def resultToJson(result: Any): JsValue = result match {
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

  val extKeys = Array("extType", "extUpload", "extMethod", "extTID", "extAction")

  /** Filter post keys which are given by the standard Ext JS Remoting Provider so this data is not being
    * passed to the direct method which is being called
    */
  def filterExtKeys(data: Map[String, String]): Map[String, String] = {
    data.filterNot(key => extKeys.contains(key._1))
  }

  /** Execute a JSON request
    */
  def executeApi = Action {
    request =>
      try {
        val rpcResults = request.contentType.get match {
          // Default JSON
          case "application/json" =>
            request.body.asJson.get match {
              case JsArray(elements) =>
                elements.map(buildRpc(_))
              case obj: JsObject =>
                List(buildRpc(obj))
              case _ =>
                throw new Exception("Invalid Json Input")
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
              filterExtKeys(post.map(row => row._1 -> row._2.mkString)),
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
            "message" -> "An unhandled exception occurred",
            "where" -> ""))
      }
  }

  def buildFormResponse(rpc: String) = {
    val response = rpc.replace("\"", "\\\"")
    s"<html><body><textarea>$response</textarea></body></html>"
  }

}