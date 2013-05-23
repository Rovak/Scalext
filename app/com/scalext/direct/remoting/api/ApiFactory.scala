package com.scalext.direct.remoting.api

import play.api.libs.json.JsObject
import play.api.libs.json.Json
import com.typesafe.config.ConfigFactory
import com.typesafe.config.impl.ConfigString
import com.typesafe.config.impl.ConfigString
import play.Configuration
import play.api.Play.current
import play.api.Play
import com.scalext.annotations.Remotable
import java.lang.reflect.Modifier
import com.scalext.controllers.routes
import com.scalext.annotations.FormHandler

object ApiFactory {

  /**
   * Build classes from the configuration
   */
  def getClasses(): Map[String, Class[_]] = {
    val directClasses = Play.configuration.getString("scalext.direct.classes").getOrElse("")

    var classes = Map[String, Class[_]]()

    directClasses.split(",").foreach { className =>
      var cls = Class.forName(className)
      var clsName = cls.getSimpleName()
      var remotable = cls.getAnnotation(classOf[Remotable])
      if (remotable != null && !remotable.name().isEmpty) {
        clsName = remotable.name()
      }
      classes += clsName -> cls
    }

    classes
  }

  /**
   * Direct Api Configuration
   */
  def config: Api = {

    var actions = List[Action]()

    getClasses().foreach {
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