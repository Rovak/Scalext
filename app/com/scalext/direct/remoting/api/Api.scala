package com.scalext.direct.remoting.api

import play.api.libs.json.Json

class Api {

  var url = ""
  var widgetType = "remoting"
  var actions = List[Action]()

  def toJson = Json.obj(
    "url" -> url,
    "type" -> widgetType,
    "actions" -> Json.arr(actions.map(_.toApiJson)))
}