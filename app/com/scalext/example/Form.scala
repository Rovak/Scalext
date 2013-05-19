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
      false,
      errors = Map("name" -> "wrong info"))
  }

  @FormHandler
  def getPhoneInfo(profile: Profile): FormResult = {
    FormResult(
      null,
      errors = Map("name" -> "wrong info"))
  }

  @FormHandler
  def getLocationInfo(profile: Profile): FormResult = {
    FormResult(
      null,
      errors = Map("name" -> "wrong info"))
  }
}