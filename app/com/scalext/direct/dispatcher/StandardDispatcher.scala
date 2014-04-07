package com.scalext.direct.dispatcher

import com.google.gson.FieldNamingPolicy
import com.google.gson.GsonBuilder

import play.api.libs.json.JsArray
import play.api.libs.json.JsValue
import play.api.libs.json.Json
import com.scalext.direct.remoting.{RpcError, RpcResult, Rpc}

/**
 * Default Dispatcher
 */
class StandardDispatcher(directClasses: Map[String, Class[_]]) extends Dispatcher {

  val gson = new GsonBuilder()
    .setFieldNamingPolicy(FieldNamingPolicy.LOWER_CASE_WITH_UNDERSCORES)
    .create()

  /**
   * Retrieve a new class for the given action
   *
   * @param actionName action
   * @return class instance for the given action
   */
  def getClassInstanceForAction(actionName: String) = {
    directClasses.get(actionName).map(cls => cls.newInstance())
  }


  /**
   * Dispatch a single RPC
   */
  override def dispatch(rpc: Rpc): RpcResult = {
    getClassInstanceForAction(rpc.action).map { classInstance =>
      classInstance.getClass.getDeclaredMethods.find(_.getName == rpc.method).map { methodInstance =>
        val methodParams = methodInstance.getParameterTypes

        val methodArgs = rpc.data match {
          case JsArray(elements) =>
            elements.zipWithIndex.foldLeft(List[Any]()) {
              case (current, (value, index)) => current :+ valueToParam(value, methodParams(index))
            }
          case seq: Seq[_] =>
            seq.zipWithIndex.foldLeft(List[Any]()) {
              case (current, (value, index)) => current :+ valueToParam(value, methodParams(index))
            }
        }

        val methodResult = methodInstance.invoke(
          classInstance,
          methodArgs.asInstanceOf[Seq[Object]]: _*)

        RpcResult(rpc, methodResult)
      }.getOrElse(RpcResult(rpc, RpcError(s"Method: ${rpc.method} not found on class ${classInstance.getClass.getName}")))
    }.getOrElse(RpcResult(rpc, RpcError(s"Class not found for action ${rpc.action}")))
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
}