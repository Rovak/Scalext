package com.scalext.tests.direct

import com.scalext.annotations.Remotable
import com.scalext.direct.remoting.FormResult


@Remotable(name = "test.ProfileForm")
class Form {

  def getProfile() = {
    FormResult(Map("user" ->  "Roy"), true)
  }
}
