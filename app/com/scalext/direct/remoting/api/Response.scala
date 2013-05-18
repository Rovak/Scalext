package com.scalext.direct.remoting.api

import play.api.libs.json.JsValue
import play.api.libs.json.Json
import play.api.libs.json.JsObject

trait Object {
  def toDirectApi: JsValue
}

case class Method(var name: String, var numberOfParameters: Int = 0) extends Object {
  def toDirectApi = Json.obj(
    "name" -> name,
    "len" -> numberOfParameters)
}
case class Action(name: String, var methods: List[Method] = List()) extends Object {

  def toDirectApi = Json.obj(
    name -> methods.foldLeft(Json.arr()) {
      case (current, method) => current :+ method.toDirectApi

    })
}