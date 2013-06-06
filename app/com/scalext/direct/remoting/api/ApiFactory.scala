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

  /** Returns classes which are configured in the application.conf
    */
  def classes: Map[String, Class[_]] = {
    val directClasses = Play.configuration.getString("scalext.direct.classes").getOrElse("")

    directClasses.split(",").foldLeft(Map[String, Class[_]]()) {
      case (map, className) =>
        val cls = Class.forName(className)
        var clsName = cls.getSimpleName()
        var remotable = cls.getAnnotation(classOf[Remotable])
        if (remotable != null && !remotable.name().isEmpty) {
          clsName = remotable.name()
        }
        map + (clsName -> cls)
    }
  }

  /** Translates a map from the classes method to a Direct API object
    */
  def config: Api = {
    var actions = List[Action]()
    classes.foreach {
      case (className, cls) =>
        actions ::= Action(
          className,
          cls.getDeclaredMethods.foldLeft(List[Method]()) {
            // Only @Remotable methods
            case (list, method: java.lang.reflect.Method) if (method.getAnnotation(classOf[Remotable]) != null || method.getAnnotation(classOf[FormHandler]) != null) =>
              var remotable = method.getAnnotation(classOf[Remotable])
              var methodName = if (remotable != null && !remotable.name().isEmpty) remotable.name() else method.getName
              list :+ Method(
                methodName,
                method.getParameterTypes.length,
                method.getAnnotation(classOf[FormHandler]) != null)
            case (list, _) => list
          })
    }

    Api(actions)
  }

}