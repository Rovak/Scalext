package com.scalext.controllers

import play.api.mvc._
import com.scalext.direct.remoting.api.ApiFactory
import play.api.libs.json._
import com.scalext.direct.remoting.api.Rpc
import com.google.gson.GsonBuilder
import com.google.gson.FieldNamingPolicy
import com.scalext.direct.remoting.api.RpcResult

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

    var methodParams = List[Class[_]]()
    var methodArgs = Seq[Object]()

    rpc.data match {
      case JsArray(elements) => elements.foreach { element =>
        methodParams ::= classOf[String]
        methodArgs = methodArgs ++ Seq(element match {
          case JsString(value) => value
        })
      }
      case _ => // TODO add objects
    }

    if (!apiClasses.contains(rpc.action)) {
      throw new Exception(s"Action ${rpc.action} not found")
    }

    val cls = apiClasses(rpc.action)

    val methodInstance = cls.getDeclaredMethod(rpc.method, methodParams: _*)
    val methodResult = methodInstance.invoke(
      classInstances(rpc.action),
      methodArgs.asInstanceOf[Seq[Object]]: _*)

    val result: JsValue = methodResult match {
      case list: List[Any] => Json.parse(gson.toJson(list.toArray[Any]))
      case _ => Json.parse(gson.toJson(methodResult))
    }

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
      data = (rpc \ "data"))
  }

  /**
   * Execute a JSON request
   */
  def executeApi = Action(parse.json) { request =>
    var rpcJson = request.body match {
      case JsArray(elements) => elements.foldLeft(Json.arr()) {
        case (list, current) =>
          list :+ dispatchRpc(buildRpc(current)).toJson
      }
      case obj: JsObject => dispatchRpc(buildRpc(obj)).toJson
      case value: JsValue => value
    }

    Ok(rpcJson)
  }

  def formResponse = Action {
    var response = "test"
    Ok(s"<html><body><textarea>$response</textarea></body></html>")
  }

}