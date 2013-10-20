package com.scalext.util

import scala.reflect.runtime._

object Reflection {

  def annotations[T: universe.TypeTag](cls: universe.Type) = cls.typeSymbol.annotations

  def annotation[Z](cls: universe.Type, annotationType: universe.Type) = annotations(cls).find(_.tpe == annotationType)

  def methods(cls: universe.Type) = cls.declarations

  def method(cls: universe.Type, name: String) = cls.declarations.find(_.fullName == name)

  def show(value: Any) = universe.show(value).stripPrefix("\"").stripSuffix("\"")

}
