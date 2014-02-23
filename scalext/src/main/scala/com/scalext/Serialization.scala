package com.scalext

import com.google.gson.{FieldNamingPolicy, GsonBuilder}
import play.api.libs.json.{JsValue, JsNull, Json}

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
      case _ =>
        Json.parse(gson.toJson(value))
    }
  }
}
