package com.scalext.tests.direct.remoting.api

import org.specs2.mutable._
import com.scalext.direct.remoting.api.ApiFactory
import play.api.test.Helpers._
import play.api.test.FakeApplication

class ApiFactoryTest extends Specification {

  "ApiFactory" should {


    "build classes from a comma separated string" in {
      running(FakeApplication()) {
        val classes = ApiFactory.buildClasses("com.scalext.tests.direct.Form")
        classes must have size 1
      }
    }

    "return an empty list when no classes are configured" in {
      running(FakeApplication()) {
        val classes = ApiFactory.buildClasses("")
        classes must have size 0
      }
    }

    "classes must retrieve name from remotable annotation" in {
      running(FakeApplication()) {
        val classes = ApiFactory.buildClasses("com.scalext.tests.direct.Form")
        classes must have key "test.ProfileForm"
      }
    }

  }

  "Formhandlers created by ApiFactory" should {

    "be visible in the api" in {
      running(FakeApplication()) {
        val classes = ApiFactory.buildClasses("com.scalext.tests.direct.Form")
        classes must have key "test.ProfileForm"
      }
    }

    "receive formhandler when generated from apifactory" in {
      running(FakeApplication()) {
        val classes = ApiFactory.buildClasses("com.scalext.tests.direct.Form")
        val config = ApiFactory.buildConfigFromClasses(classes)
        val form = config.flatMap(action => action.methods).find(method => method.formHandler)
        form must not be null
      }
    }
  }
}
