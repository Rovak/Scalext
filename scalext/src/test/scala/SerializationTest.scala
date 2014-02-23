package com.scalext.tests

import org.specs2.mutable._
import com.scalext.frontend.Panel

class SerializationTest extends Specification {

  "Json Serializaton" should {
    "serialize a panel to json" in {
      val panel = new Panel {
        title = "Unittest Panel"
      }

      (panel.toJson \ "title").as[String] must equalTo("Unittest Panel")
    }

    "serialize a panel with child items" in {
      val panel = new Panel {
        title = "Parent"
        items = List(
          new Panel {
            title = "Child 1"
          },
          new Panel {
            title = "Child 2"
          })
      }

      (panel.toJson \\ "title").length must equalTo(3)
      (panel.toJson \ "items" \\ "title").length must equalTo(2)
    }
  }
}
