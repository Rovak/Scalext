package controllers

import play.api.mvc.Action
import play.api.mvc.Controller
import com.scalext.frontend.Panel
import play.api.mvc.WebSocket
import play.api.libs.json.JsValue
import com.scalext.frontend.grid.{ Panel => GridPanel }
import com.scalext.frontend.grid.Column
import game.actors.LiveActor
import game.actors.UpdateStatus

object Application extends Controller {

  def dashboard = Action {

    LiveActor.actor ! UpdateStatus("User joined..")

    Ok(views.html.dashboard())
  }

  def index = Action {
    var gridPanel = new GridPanel {
      title = "Test Titel"
      columns ++= List(
        Column("Id"),
        Column("Artikel"))
    }

    Ok(gridPanel.toJson)
  }

  def live = WebSocket.async[JsValue] { request =>
    game.actors.LiveActor.join()
  }
}