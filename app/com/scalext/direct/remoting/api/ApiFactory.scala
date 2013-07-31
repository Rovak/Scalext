package com.scalext.direct.remoting.api

import play.api.Play.current
import play.api.Play
import com.scalext.annotations.Remotable
import com.scalext.annotations.FormHandler

/** Builds Direct API configuration
  *
  * Scans classes where are configured in application.conf and builds
  * configuration based on the given classes and containing annotations
  */
object ApiFactory {

  def buildClasses(classList: String): Map[String, Class[_]] = {
    if (!classList.isEmpty) classList.split(",").foldLeft(Map[String, Class[_]]()) {
      case (map, className) =>
        val cls = Class.forName(className)
        var clsName = cls.getSimpleName
        val remotable = cls.getAnnotation(classOf[Remotable])
        if (remotable != null && !remotable.name().isEmpty) {
          clsName = remotable.name()
        }
        map + (clsName -> cls)
    } else Map()
  }

  /** Returns classes which are configured in the application.conf
    */
  def classes: Map[String, Class[_]] = {
    buildClasses(Play.configuration.getString("scalext.direct.classes").getOrElse(""))
  }

  /**
   * Build configuration from the given classes
   */
  def buildConfigFromClasses(classes: Map[String, Class[_]]): List[Action] = {
    var actions = List[Action]()
    classes.foreach {
      case (className, cls) =>
        actions ::= Action(
          className,
          cls.getDeclaredMethods.foldLeft(List[Method]()) {
            // Only @Remotable methods
            case (list, method: java.lang.reflect.Method) if (method.getAnnotation(classOf[Remotable]) != null || method.getAnnotation(classOf[FormHandler]) != null) =>
              val remotable = method.getAnnotation(classOf[Remotable])
              val methodName = if (remotable != null && !remotable.name().isEmpty) remotable.name() else method.getName
              list :+ Method(
                methodName,
                method.getParameterTypes.length,
                method.getAnnotation(classOf[FormHandler]) != null)
            case (list, _) => list
          })
    }
    actions
  }

  /** Translates a map from the classes method to a direct API object
    */
  def config: Api = {
    Api(buildConfigFromClasses(classes))
  }

}