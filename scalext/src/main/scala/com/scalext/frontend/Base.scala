package com.scalext.frontend

import play.api.libs.json._
import play.api.libs.json.Json.toJsFieldJsValueWrapper
import com.scalext.Serialization


trait Base {
  var config = Map[String, Any]()

  def toJson: JsValue = config.foldLeft(Json.obj()) {
    // Inherited by Base conversions
    case (current: JsObject, (key: String, value: Base)) =>
      current ++ Json.obj(key -> value.toJson)
    // Strings
    case (current: JsObject, (key: String, value: String)) =>
      current ++ Json.obj(key -> value)
    // Lists
    case (current: JsObject, (key: String, value: List[_])) =>
      current ++ Json.obj(key -> value.foldLeft(Json.arr()) {
        case (arr, item: Base) => arr :+ item.toJson
      })
    case (current: JsObject, (key: String, value)) =>
      current ++ Json.obj(key -> Serialization.toJson(value))

  }

}