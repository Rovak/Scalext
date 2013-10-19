package com.scalext.direct.remoting.api

import play.api.Play.current
import play.api.Play
import com.scalext.annotations._
import scala.reflect.runtime._

/**
 * Builds Direct API configuration
 *
 * Scans classes where are configured in application.conf and builds
 * configuration based on the given classes and containing annotations
 */
object ApiFactory {

  val runtimeMirror = universe.runtimeMirror(Play.classloader)

  def loadClass(className: String) = {
    Play.classloader.loadClass(className)
  }

  def buildClasses(classList: String): Map[String, Class[_]] = {
    if (!classList.isEmpty) classList.split(",").foldLeft(Map[String, Class[_]]()) {
      case (map, className) =>
        val cls = loadClass(className)
        var clsName = cls.getSimpleName
        annotation(runtimeMirror.classSymbol(cls).toType, universe.typeOf[Remotable]).map {
          annotation =>
            clsName = universe.show(annotation.scalaArgs.head).stripPrefix("\"").stripSuffix("\"")
        }
        map + (clsName -> cls)
    } else Map()
  }

  def annotations[T: universe.TypeTag](cls: universe.Type) = cls.typeSymbol.annotations

  def annotation[Z](cls: universe.Type, annotationType: universe.Type) = annotations(cls).find(_.tpe == annotationType)

  def methods(cls: universe.Type) = cls.declarations

  def method(cls: universe.Type, name: String) = cls.declarations.find(_.fullName == name)

  /**
   * Returns classes which are configured in the application.conf
   */
  def classes: Map[String, Class[_]] = {
    buildClasses(Play.configuration.getString("scalext.direct.classes").getOrElse(""))
  }

  /**
   * Build configuration from the given classes
   */
  def buildConfigFromClasses(classes: Map[String, Class[_]]) = {
    classes.map {
      case (className, cls) =>
        Action(className, methods(runtimeMirror.classSymbol(cls).toType).foldLeft(List[Method]()) {
          case (list, methodRef) if methodRef.annotations.exists(x => (x.tpe == universe.typeOf[Remotable]) || (x.tpe == universe.typeOf[FormHandler])) =>
            val methodRef2 = methodRef.asMethod
            val methodName = methodRef2.annotations.find(_.tpe == universe.typeOf[Remotable]).map(x => universe.show(x.scalaArgs.head).stripPrefix("\"").stripSuffix("\"")).getOrElse(methodRef.name.decoded)
            list :+ Method(methodName, methodRef2.paramss.length, methodRef2.annotations.exists(_.tpe == classOf[FormHandler]))
          case (list, _) => list
        })
    }
  }

  /**
   * Translates a map from the classes method to a direct API object
   */
  def config = Api(buildConfigFromClasses(classes).toList)
}