package com.scalext.controllers

import play.api.mvc._
import play.api.libs.json._
import com.google.gson._
import scala.collection.parallel._
import com.scalext.direct.remoting.api.Rpc
import com.scalext.direct.remoting.api.RpcResult
import com.scalext.direct.remoting.api.FormResult
import com.scalext.direct.remoting.api.ApiFactory
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

    var methodParams = methodInstance.getParameterTypes()
    var methodArgs = List[Any]()

    println(s"found method: $methodInstance => ${rpc.data}")

    methodArgs = rpc.data match {
      case JsArray(elements) => {
        elements.zipWithIndex.foldLeft(List[Any]()) {
          case (current, (value, index)) => current :+ valueToParam(value, methodParams(index))
        }
      }
      case seq: Seq[_] =>
        seq.zipWithIndex.foldLeft(List[Any]()) {
          case (current, (value, index)) => current :+ valueToParam(value, methodParams(index))
        }
    }

    if (!apiClasses.contains(rpc.action)) {
      throw new Exception(s"Action ${rpc.action} not found")
    }

    println(s"invoking $methodInstance, with $methodArgs")

    val methodResult = methodInstance.invoke(
      classInstances(rpc.action),
      methodArgs.asInstanceOf[Seq[Object]]: _*)

    val result = resultToJson(methodResult)

    RpcResult(rpc, result match {

      case value: JsValue =>
        Json.obj("result" -> value)
      case obj: JsObject =>
        obj

    })
  }

  /**
   * Value to param
   */
  def valueToParam(param: Any, paramType: Class[_]): Any = {
    param match {
      case map: Map[_, _] => map
      case jsval: JsValue => gson.fromJson(Json.stringify(jsval), paramType)
      case _ => param
    }
  }

  def resultToJson(result: Any): JsValue = {
    result match {
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
      case list: List[Any] =>
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
    Rpc(
      id = post("extTID").mkString.toInt,
      action = post("extAction").mkString,
      method = post("extMethod").mkString,
      data = Json.arr(Json.toJson(filterExtKeys(post.map(row => (row._1 -> row._2.mkString))))))
  }

  var extKeys = Array("extType", "extUpload", "extMethod", "extTID", "extAction")

  def filterExtKeys(data: Map[String, String]): Map[String, String] = {
    data.filterNot(key => extKeys.contains(key._1))
  }

  /**
   * Execute a JSON request
   */
  def executeApi = Action { request =>
    request.contentType.get match {
      // Default JSON
      case "application/json" =>
        var rpcJson = request.body.asJson.get match {
          case JsArray(elements) =>
            var rpcs = elements.map(buildRpc(_)).toList.toParArray
            rpcs.tasksupport = new ThreadPoolTaskSupport()
            rpcs.map(dispatchRpc(_)).foldLeft(Json.arr()) {
              case (list, current) => list :+ current.toJson
            }
          case obj: JsObject =>
            dispatchRpc(buildRpc(obj)).toJson
          case value: JsValue =>
            value
        }
        Ok(rpcJson)
      // Form Submit
      case "application/x-www-form-urlencoded" =>
        var post = request.body.asFormUrlEncoded.get
        var rpcJson = buildRpc(post)
        Ok(dispatchRpc(rpcJson).toJson)
      // Form Upload
      case "multipart/form-data" =>
        var postBody = request.body.asMultipartFormData.get
        var post = postBody.asFormUrlEncoded
        var rpc = buildRpc(post)
        var params = List[Any](
          filterExtKeys(post.map(row => (row._1 -> row._2.mkString))),
          postBody.files.map(_.ref))
        rpc.data = params
        var result = dispatchRpc(rpc)
        Ok(result.toJson)
      case _ =>
        Ok("Invalid Request!")
    }

  }

  def buildFormResponse(rpc: String) = {
    var response = rpc.replace("\"", "\\\"")
    s"<html><body><textarea>$response</textarea></body></html>"
  }

}