package com.scalext.direct.remoting.api

import play.api.Play.current
import play.api.Play
import com.scalext.annotations._
import scala.reflect.runtime._
import com.scalext.util.Reflection

/**
 * Builds Direct API configuration
 *
 * Scans classes where are configured in application.conf and builds
 * configuration based on the given classes and containing annotations
 */
object ApiFactory {

  import com.scalext.util.Reflection._

  val classLoader = Play.classloader
  val runtimeMirror = universe.runtimeMirror(classLoader)

  def loadClass(className: String) = classLoader.loadClass(className)

  def buildClasses(classList: String): Map[String, Class[_]] = {
    if (!classList.isEmpty) classList.split(",").foldLeft(Map[String, Class[_]]()) {
      case (map, className) =>
        val cls = loadClass(className)
        val clsName = Reflection.annotation(runtimeMirror.classSymbol(cls).toType, universe.typeOf[Remotable]).map(x => Reflection.show(x.scalaArgs.head)).getOrElse(cls.getSimpleName)
        map + (clsName -> cls)
    } else Map()
  }


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
            val methodName = methodRef2.annotations
              .find(_.tpe == universe.typeOf[Remotable]).map(x => universe.show(x.scalaArgs.head).stripPrefix("\"").stripSuffix("\""))
              .getOrElse(methodRef.name.toString)
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