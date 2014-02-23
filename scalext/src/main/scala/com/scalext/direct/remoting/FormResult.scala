package com.scalext.direct.remoting

import play.api.libs.json.Json
import play.api.libs.json.Json.toJsFieldJsValueWrapper

case class FormResult(result: Any, success: Boolean = true, errors: Map[String, String] = Map()) {
  def toJson = {
    var result = Json.obj(
      "success" -> (errors.isEmpty || success))
    if (!errors.isEmpty) result += "errors" -> errors.foldLeft(Json.obj()) {
      case (current, (key, value)) =>
        current ++ Json.obj(key -> value)
    }
    result
  }

}