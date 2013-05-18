package com.scalext.direct.remoting.api

import scala.reflect.runtime.universe._
import controllers.DirectTest
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

// http://www.veebsbraindump.com/2013/01/reflecting-annotations-in-scala-2-10/
// http://stackoverflow.com/questions/4315678/how-to-use-scalas-singleton-object-types

/*
{
Ext.app.REMOTING_API = {
    "url":"remote\/router.php",
    "type":"remoting",
    "actions":{
        "AlbumList":[{
            "name":"getAll",
            "len":0
        },{
            "name":"add",
            "len":1
        }]
    }
};
}
 var actions = Json.obj()

    var classType = universe.typeOf[DirectTest.type]

    // check if not parent method.owner == classType.typeSymbol

    var annos = classType.typeSymbol.asClass.annotations

    var remotableName = ""

    annos.foreach { anno =>
      remotableName = anno.scalaArgs(0).toString
      remotableName = remotableName.substring(1, remotableName.length - 1)
    }

    var methods = List[Method]()

    classType.members.view.foreach {
      case method: MethodSymbol if !method.isConstructor =>
        method.annotations.foreach { anno =>
          var methodName = anno.scalaArgs(0).toString
          methods ::= Method(methodName.substring(1, methodName.size - 1), method.paramss.size)
        }
      case _ =>
    }

    var methodsArr = Json.arr()
    methods.foreach { method =>
      methodsArr = methodsArr :+ method.toApiJson
    }

    actions = actions + (remotableName -> methodsArr) 
 */

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

      classes += (clsName -> cls)
    }

    classes
  }

  def config: JsObject = {

    var actions = Json.obj()

    getClasses().foreach {
      case (className, cls) =>

        actions += (className -> cls.getDeclaredMethods.foldLeft(Json.arr()) {
          case (list, method: java.lang.reflect.Method) if (method.getAnnotation(classOf[Remotable]) != null) =>
            var remotable = method.getAnnotation(classOf[Remotable])
            var methodName = if (!remotable.name().isEmpty) remotable.name() else method.getName
            list :+ Method(
              methodName,
              method.getParameterTypes.length).toApiJson
          case (list, _) => list
        })
    }

    Json.obj(
      "type" -> "remoting",
      "url" -> routes.Api.directApi().url,
      "actions" -> actions)
  }

}