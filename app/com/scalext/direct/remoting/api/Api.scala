package com.scalext.direct.remoting.api

import play.api.libs.json.Json
import com.scalext.controllers.routes

case class Api(var actions: List[Action] = List()) {

  var url = routes.Api.executeApi().url
  var widgetType = "remoting"

  def toJson = Json.obj(
    "url" -> url,
    "type" -> widgetType,
    "actions" -> actions.foldLeft(Json.obj()) {
      case (list, action) => list ++ action.toDirectApi
    })
}