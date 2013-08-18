package com.scalext.direct.controller

import com.scalext.direct.dispatcher.ScalextClassBuilder

class DefaultControllerFactory extends ScalextClassBuilder {
  def buildClass(cls: Class[_]): Any = cls.newInstance()
}
