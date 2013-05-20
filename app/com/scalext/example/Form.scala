package com.scalext.example

import com.scalext.annotations.Remotable
import com.scalext.direct.remoting.api.FormResult
import com.scalext.annotations.FormHandler

case class Profile(name: String, email: String, company: String)

@Remotable(name = "Scalext.example.Profile")
class Form {

  @Remotable
  def getBasicInfo(): FormResult = {
    var profile = Profile(
      "Roy van Kaathoven",
      "ik@royvankaathoven.nl",
      "Roy")

    FormResult(profile)
  }

  @FormHandler
  def updateBasicInfo(profile: Profile): FormResult = {
    FormResult(
      null,
      errors = Map("name" -> "wrong info"))
  }

  @Remotable
  def getPhoneInfo(): FormResult = {
    FormResult(Map(
      "office" -> "1-800-CALLEXT",
      "cell" -> "443-555-1234",
      "home" -> ""))
  }

  @Remotable
  def getLocationInfo(): FormResult = {
    FormResult(Map(
      "street" -> "streetval",
      "city" -> "cityval",
      "state" -> "stateval",
      "zip" -> "zival"))
  }
}