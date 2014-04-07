package com.scalext.controllers

import com.scalext.direct.dispatcher.ParallelDispatcher
import com.scalext.direct.remoting.api._
import play.api.Play.current
import play.api.libs.json._
import play.api.mvc.{Request, AnyContent, Controller, Action}
import com.scalext.direct.RPCFactory._
import play.api.libs.json.JsArray
import play.api.libs.json.JsObject
import com.scalext.direct.remoting.Rpc


/**
 * Api Controller
 */
object Api extends Controller {

  import com.scalext.direct.RPCFactory._

  /**
   * Debug Mode
   */
  def isDebugMode = play.api.Play.mode == play.api.Mode.Dev

  /**
   * Dispatcher used to process API requests
   */
  val dispatcher = new ParallelDispatcher(ApiFactory.classes)

  /**
   * Generates a javascript file which calls the `Ext.direct.Manager.addProvider`
   * method to setup the Direct API configuration
   */
  def buildApi = Action {
    val result = s"Ext.direct.Manager.addProvider(${Json.stringify(ApiFactory.config.toJson)});"
    Ok(result).as("text/javascript")
  }


  /**
   * Read the request and process the RPC data depending on the contentType
   *
   * @param request Any request
   * @return Rpc results
   */
  def processRpcRequest(request: Request[AnyContent]): Option[List[Rpc]] = {
    request.contentType.flatMap {
      // Default JSON
      case "application/json" =>
        request.body.asJson.map {
          case JsArray(elements) =>
            elements.map(buildRpc).toList
          case obj: JsObject =>
            List(buildRpc(obj))
          case _ =>
            List()
        }
      // Form Submit
      case "application/x-www-form-urlencoded" =>
        request.body.asFormUrlEncoded.map { encoded =>
          List(buildRpc(encoded))
        }
      // Form Upload
      case "multipart/form-data" =>
        request.body.asMultipartFormData.map { postBody =>
          val post = postBody.asFormUrlEncoded
          val rpc = buildRpc(post)
          rpc.data = List[Any](
            filterExtKeys(post.map(row => row._1 -> row._2.mkString)),
            postBody.files.map(_.ref))
          List(rpc)
        }

    }
  }

  /**
   * Processes Direct API method calls
   */
  def executeApi = Action { request =>
    try {
      processRpcRequest(request).map { rpcResults =>
        val results = dispatcher.dispatch(rpcResults)
        if (results.size > 1) Ok(Json.toJson(results.map(resultToJson(_))))
        else Ok(resultToJson(results.head))
      }.getOrElse(BadRequest("Could not process request"))
    } catch {

      // Debug Mode, detailed exception information can be returned
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