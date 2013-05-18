package com.scalext.frontend

import play.api.libs.json._

trait Base {
  var config = Map[String, Any]()
  var items = List[Base]()
  def toJson: JsValue
}

case class Panel() extends Base {

  var title = ""

  def toJson = {

    var itemConfig = Json.obj(
      "title" -> title)

    if (!items.isEmpty) config += ("items" -> items)

    config.foldLeft(itemConfig) {
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
    }
  }

  def +=(child: Base): Panel = {
    items ::= child
    this
  }
}

