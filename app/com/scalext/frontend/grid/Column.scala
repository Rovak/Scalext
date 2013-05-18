package com.scalext.frontend.grid

import com.scalext.frontend.Base
import play.api.libs.json.JsValue

case class Column(var dataIndex: String, var text: String = "") extends Base {

  if (text.isEmpty)
    text = dataIndex

  override def toJson: JsValue = {
    config += "text" -> text
    config += "dataIndex" -> dataIndex
    super.toJson
  }

}