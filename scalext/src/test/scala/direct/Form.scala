package com.scalext.tests.direct

import com.scalext.annotations.{FormHandler, Remotable}
import com.scalext.direct.remoting.FormResult


@Remotable(name = "test.ProfileForm")
class Form {

  def getProfile() = {
    FormResult(Map("user" ->  "Roy"), true)
  }

  @FormHandler
  def postProfile() = {
    FormResult(Map("user" ->  "Form Post"), true)
  }
}
