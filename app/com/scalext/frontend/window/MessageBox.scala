package com.scalext.frontend.window

import com.scalext.frontend.Base
import play.api.libs.json.Json

object MessageBox {
  def confirm(message: String, title: String = "Confirm"): MessageBox = {
    new MessageBox {
      title = title
      message = message
    }
  }
}

case class MessageBox() extends Base {

  var title = ""
  var message = ""

  override def toJson = Json.obj(
    "title" -> title,
    "message" -> message)

  def show = s"Ext.MessageBox.show(${toJson});"
}