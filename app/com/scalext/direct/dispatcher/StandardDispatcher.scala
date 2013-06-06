package com.scalext.direct.dispatcher

import scala.collection.parallel.ThreadPoolTaskSupport
import scala.collection.parallel.traversable2ops

import com.google.gson.FieldNamingPolicy
import com.google.gson.GsonBuilder
import com.scalext.direct.remoting.api.Rpc
import com.scalext.direct.remoting.api.RpcResult

import play.api.libs.json.JsArray
import play.api.libs.json.JsValue
import play.api.libs.json.Json

/**
 * Default Dispatcher
 */
class StandardDispatcher(directClasses: Map[String, Class[_]]) extends Dispatcher {

  val classInstances = directClasses.map {
    case (name, cls) => (name -> cls.newInstance())
  }

  val gson = new GsonBuilder()
    .setFieldNamingPolicy(FieldNamingPolicy.LOWER_CASE_WITH_UNDERSCORES)
    .create()

  /**
   * Dispatch a multiple requests
   */
  override def dispatch(rpcs: Seq[Rpc]): Seq[RpcResult] = {
    rpcs.map(dispatch(_)).toList
  }

  /**
   * Dispatch a single RPC
   */
  override def dispatch(rpc: Rpc): RpcResult = {

    val cls = directClasses(rpc.action)
    val methodInstance = cls.getDeclaredMethods.find(method => method.getName == rpc.method).get

    val methodParams = methodInstance.getParameterTypes
    var methodArgs = List[Any]()

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

    if (!directClasses.contains(rpc.action)) {
      throw new Exception(s"Action ${rpc.action} not found")
    }

    val methodResult = methodInstance.invoke(
      classInstances(rpc.action),
      methodArgs.asInstanceOf[Seq[Object]]: _*)

    RpcResult(rpc, methodResult)
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