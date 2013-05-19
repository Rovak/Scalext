package com.scalext.direct.remoting.api

import play.api.libs.json.JsValue
import play.api.libs.json.Json
import play.api.libs.json.JsObject
import com.google.gson.annotations.SerializedName

trait ApiObject {
  def toDirectApi: JsValue
}

