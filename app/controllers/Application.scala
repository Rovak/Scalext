package controllers

import play.api.mvc.Action
import play.api.mvc.Controller
import com.scalext.frontend.Panel
import play.api.mvc.WebSocket
import play.api.libs.json.JsValue
import com.scalext.frontend.grid.{ Panel => GridPanel }
import com.scalext.frontend.grid.Column

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