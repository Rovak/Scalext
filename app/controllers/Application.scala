package controllers

import play.api.mvc.Action
import play.api.mvc.Controller
import com.scalext.frontend.Panel
import play.api.mvc.WebSocket
import play.api.libs.json.JsValue

object Application extends Controller {
  def dashboard = Action {

    Ok(views.html.dashboard())
  }

  def index = Action {

    var testPanel1 = new Panel { title = "Root" }
    testPanel1 += new Panel { title = "SubChild" }
    testPanel1 += new Panel { title = "SubChild" }
    testPanel1 += new Panel { title = "SubChild" }
    testPanel1 += new Panel { title = "SubChild" }

    Ok(testPanel1.toJson)
  }

  def live = WebSocket.async[JsValue] { request =>
    game.actors.LiveActor.join()
  }
}