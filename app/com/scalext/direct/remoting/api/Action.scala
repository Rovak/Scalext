package com.scalext.direct.remoting.api

import play.api.libs.json.Json
import play.api.libs.json.Json.toJsFieldJsValueWrapper

case class Action(name: String, var methods: List[Method] = List()) extends ApiObject {

  def toDirectApi = Json.obj(
    name -> methods.foldLeft(Json.arr()) {
      case (current, method) => current :+ method.toDirectApi
    })
}