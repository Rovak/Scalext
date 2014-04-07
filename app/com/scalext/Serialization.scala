package com.scalext

import com.google.gson.{FieldNamingPolicy, GsonBuilder}
import play.api.libs.json.{JsValue, JsNull, Json}
import com.google.gson.reflect.TypeToken

object Serialization {

  protected val gson = new GsonBuilder()
    .setFieldNamingPolicy(FieldNamingPolicy.LOWER_CASE_WITH_UNDERSCORES)
    .create()

  def toJson(value: Any): JsValue = {
    value match {
      case list: Seq[Any] =>
        Json.parse(gson.toJson(list.toArray[Any]))
      case null =>
        JsNull
      case mapValue: Map[_, _] =>
        mapValue.foldLeft(Json.obj()) {
          case (result, (fieldKey: String, fieldValue: Any)) =>
            result ++ Json.obj(fieldKey -> toJson(fieldValue))
        }
      case _ =>
        Json.parse(gson.toJson(value))
    }
  }
}
