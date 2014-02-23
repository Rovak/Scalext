package com.scalext.frontend


case class Panel() extends Container {
  var title = ""
  val className = "Ext.Panel"

  var width = 0
  var height = 0

  override def toJson = {
    config += "title" -> title
    if (width > 0) config += "width" -> width
    if (height > 0) config += "height" -> height
    if (!items.isEmpty) config += ("items" -> items)
    super.toJson
  }

  def +=(child: Base): Panel = {
    items ::= child
    this
  }
}

