package controllers

import play.api.mvc.Action
import play.api.mvc.Controller

object Examples extends Controller {
  def index = Action {

    Ok(views.html.examples())
  }
}