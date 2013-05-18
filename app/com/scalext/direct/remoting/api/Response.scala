package com.scalext.direct.remoting.api

import play.api.libs.json.JsValue
import play.api.libs.json.Json
import play.api.libs.json.JsObject

trait Object {
  def toApiJson: JsValue
}

case class Method(var name: String, var numberOfParameters: Int = 0) extends Object {
  def toApiJson = Json.obj(
    "name" -> name,
    "len" -> numberOfParameters)
}
case class Action(name: String) extends Object {
  def toApiJson = Json.obj(
    "name" -> name)
}