package com.scalext.controllers

import play.api.mvc._
import com.scalext.direct.remoting.api.ApiFactory
import play.api.libs.json._
import com.scalext.direct.remoting.api.Rpc

object Api extends Controller {

  val apiClasses = ApiFactory.getClasses()

  val classInstances = apiClasses.map {
    case (name, cls) =>
      (name -> cls.newInstance())
  }

  def directApi = Action {
    Ok(ApiFactory.config)
  }

  /**
   * Build and execute an Rpc request with the given JSON object
   *
   * @param rpc a single RPC
   */
  def buildRpc(rpc: JsValue): Rpc = {

    val action = (rpc \ "action").as[String]
    val method = (rpc \ "method").as[String]

    val cls = apiClasses(action)

    var methodParams = List[Class[_]]()
    var methodArgs = Seq[Object]()

    (rpc \ "data") match {
      case JsArray(elements) => elements.foreach { element =>
        methodParams ::= classOf[String]
        methodArgs = methodArgs ++ Seq(element)
      }
      case _ => // TODO add objects
    }

    val methodInstance = cls.getDeclaredMethod(method, methodParams: _*)
    val methodResult = methodInstance.invoke(
      classInstances(action),
      methodArgs.asInstanceOf[Seq[Object]]: _*)

    val result = methodResult match {
      case value: String => value
      case list: List[String] => Json.toJson(list)
    }

    Rpc(
      id = (rpc \ "tid").as[Int],
      action = action,
      method = method,
      result = result)
  }

  /**
   * Execute a JSON request
   */
  def executeApi = Action(parse.json) { request =>
    var rpcJson = request.body match {
      case JsArray(elements) => elements.foldLeft(Json.arr()) {
        case (list, current) => list :+ buildRpc(current).toJson
      }
      case obj: JsObject => buildRpc(obj).toJson
    }

    Ok(rpcJson)
  }

  def formResponse = Action {
    var response = "test"
    Ok(s"<html><body><textarea>$response</textarea></body></html>")
  }

}