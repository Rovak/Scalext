package com.scalext.json

import com.google.gson._
import java.lang.reflect.Type


class MapSerializer extends JsonSerializer[Map[_,_]] {
  def serialize(mapValue: Map[_,_], srcType: Type, context: JsonSerializationContext): JsonElement = {

    val obj = new JsonObject()
    for (pair: Pair[Any, Any] <- mapValue: Map[_,_]) {
      obj.add(pair._1.toString, context.serialize(pair._2.asInstanceOf))
    }
     obj
  }
}

