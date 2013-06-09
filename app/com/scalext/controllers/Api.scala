package com.scalext.controllers

import com.scalext.direct.dispatcher.ParallelDispatcher
import com.scalext.direct.remoting.api._
import play.api.Play.current
import play.api.libs.json._
import play.api.mvc.{Controller, Action}


/** Api Controller
  */
object Api extends Controller {

  import com.scalext.direct.RPCFactory._

  def isDebugMode = play.api.Play.mode == play.api.Mode.Dev
  val dispatcher = new ParallelDispatcher(ApiFactory.classes)

  /** Build the javascript API
    */
  def buildApi = Action {
    val result = s"Ext.direct.Manager.addProvider(${Json.stringify(ApiFactory.config.toJson)});";
    Ok(result).as("text/javascript")
  }

  /** Execute a JSON request
    */
  def executeApi = Action { request =>
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
}