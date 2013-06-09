package com.scalext.tests

import org.specs2.mutable._
import play.api.libs.json.JsArray


class ApiControllerTest extends Specification {

  "ApiController" should {
    "filter Ext.Direct keys from data" in {
      val postValues = Map(
        "extType" -> Seq("rpc"),
        "extUpload" -> Seq("false"),
        "extMethod" -> Seq("TestMethod"),
        "extTID" -> Seq("100"),
        "extAction" -> Seq("ExtObject"))

      val rpc = com.scalext.direct.RPCFactory.buildRpc(postValues)
      val data = rpc.data.asInstanceOf[JsArray]

      (data \\ "extType").length must equalTo(0)
      (data \\ "extUpload").length must equalTo(0)
      (data \\ "extMethod").length must equalTo(0)
      (data \\ "extTID").length must equalTo(0)
      (data \\ "extAction").length must equalTo(0)
    }
  }
}

