package com.scalext.frontend

object Helpers {
  def define(obj: Component) = s"Ext.define('${obj.className}', ${obj.toJson});"
}
