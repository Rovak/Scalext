package com.scalext.direct.dispatcher

trait ScalextClassBuilder {

  def buildClass(cls: Class[_]): Any

}
