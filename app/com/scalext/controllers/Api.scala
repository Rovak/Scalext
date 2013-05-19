package com.scalext.controllers

import play.api.mvc._
import com.scalext.direct.remoting.api.ApiFactory
import play.api.libs.json._
import com.scalext.direct.remoting.api.Rpc
import com.google.gson.GsonBuilder
import com.google.gson.FieldNamingPolicy
import com.scalext.direct.remoting.api.RpcResult
import com.scalext.direct.remoting.api.FormResult
import views.html.defaultpages.badRequest

object Api extends Controller {

  val apiClasses = ApiFactory.getClasses()

  val classInstances = apiClasses.map {
    case (name, cls) => (name -> cls.newInstance())
  }

  val gson = new GsonBuilder()
    .setFieldNamingPolicy(FieldNamingPolicy.LOWER_CASE_WITH_UNDERSCORES)
    .create();

  /**
   * Dispatches the given RPC by executing the action and method
   */
  def dispatchRpc(rpc: Rpc): RpcResult = {

    val cls = apiClasses(rpc.action)
    val methodInstance = cls.getDeclaredMethods().find(method => method.getName() == rpc.method).get

    println("found method! " + methodInstance)

    var methodParams = methodInstance.getParameterTypes()
    var methodArgs = List[Any]()

    methodArgs = rpc.data match {
      case JsArray(elements) =>
        elements.zipWithIndex.foldLeft(List[Any]()) {
          case (current, (value, index)) =>
            current :+ gson.fromJson(Json.stringify(value), methodParams(index))
        }
    }

    if (!apiClasses.contains(rpc.action)) {
      throw new Exception(s"Action ${rpc.action} not found")
    }

    println("invoking " + methodArgs)

    val methodResult = methodInstance.invoke(
      classInstances(rpc.action),
      methodArgs.asInstanceOf[Seq[Object]]: _*)

    println("method result " + methodResult)

    val result: JsValue = methodResult match {
      case FormResult(formResult, success, errors) =>
        var resul = if (formResult == null) JsNull else Json.parse(gson.toJson(formResult))
        var jsonResult = Json.obj(
          "success" -> success,
          "data" -> resul)
        println("json result " + jsonResult)
        if (!errors.isEmpty) jsonResult += "errors" -> errors.foldLeft(Json.obj()) {
          case (current, (key, value)) =>
            current ++ Json.obj(key -> value)
        }
        jsonResult
      case list: List[Any] =>
        Json.parse(gson.toJson(list.toArray[Any]))
      case _ =>
        Json.parse(gson.toJson(methodResult))
    }

    println("Result! " + result)

    RpcResult(rpc, result)
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

  // Some(Map(extType -> List(rpc), extUpload -> List(false), extAction -> List(Scalext.example.Profile), extMethod -> List(updateBasicInfo), extTID -> List(3)))

  var extKeys = Array("extType", "extUpload", "extMethod", "extTID", "extAction")

  /**
   * Build and execute an RPC request from the given FORM Request
   *
   * @param rpc a single RPC
   */
  def buildRpc(post: Map[String, Seq[String]]): Rpc = {
    Rpc(
      id = post("extTID").mkString.toInt,
      action = post("extAction").mkString,
      method = post("extMethod").mkString,
      data = Json.arr(Json.toJson(post.filterNot(key => extKeys.contains(key._1)).map(row => (row._1 -> row._2.mkString)))))
  }

  /**
   * Execute a JSON request
   */
  def executeApi = Action { request =>

    request.contentType.get match {
      case "application/x-www-form-urlencoded" =>
        var post = request.body.asFormUrlEncoded.get
        var rpcJson = buildRpc(post)
        Ok(dispatchRpc(rpcJson).toJson)
      case "application/json" =>
        var rpcJson = request.body.asJson.get match {
          case JsArray(elements) => elements.foldLeft(Json.arr()) {
            case (list, current) =>
              list :+ dispatchRpc(buildRpc(current)).toJson
          }
          case obj: JsObject => dispatchRpc(buildRpc(obj)).toJson
          case value: JsValue => value
        }

        Ok(rpcJson)
      case _ =>
        Ok("Invalid Request!")
    }

  }

  def formResponse = Action {
    var response = "test"
    Ok(s"<html><body><textarea>$response</textarea></body></html>")
  }

}