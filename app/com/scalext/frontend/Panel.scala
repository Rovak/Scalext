package com.scalext.frontend


case class Panel() extends Container {

  var title = ""

  override def toJson = {
    config += "title" -> title
    if (!items.isEmpty)
      config += ("items" -> items)
    super.toJson
  }

  def +=(child: Base): Panel = {
    items ::= child
    this
  }
}

