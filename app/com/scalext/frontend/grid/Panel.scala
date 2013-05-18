package com.scalext.frontend.grid

import com.scalext.frontend.{ Base, Panel => BasePanel }

class Panel extends BasePanel {

  var columns = List[Column]()

  override def toJson = {
    config += "columns" -> columns
    super.toJson
  }

}