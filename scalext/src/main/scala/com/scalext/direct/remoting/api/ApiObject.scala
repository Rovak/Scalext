package com.scalext.direct.remoting.api

import play.api.libs.json.JsValue

/** Object which can be translated to a Direct API
  */
trait ApiObject {
  def toDirectApi: JsValue
}

