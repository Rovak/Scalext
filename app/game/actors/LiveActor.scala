package game.actors

import scala.concurrent.duration.DurationInt
import akka.actor.Actor
import akka.actor.Props
import akka.actor.actorRef2Scala
import akka.pattern.ask
import akka.util.Timeout
import play.api.Play.current
import play.api.libs.concurrent.Akka
import play.api.libs.concurrent.Execution.Implicits.defaultContext
import play.api.libs.iteratee.Concurrent
import play.api.libs.iteratee.Done
import play.api.libs.iteratee.Enumerator
import play.api.libs.iteratee.Input
import play.api.libs.iteratee.Iteratee
import play.api.libs.json.JsValue
import play.api.libs.json.Json
import play.api.libs.json.Json.toJsFieldJsValueWrapper
import play.api.libs.iteratee.Concurrent.Channel

object LiveActor {

  implicit val timeout = Timeout(2 second)

  lazy val actor = Akka.system.actorOf(Props[LiveActor], "live")

  def join(): scala.concurrent.Future[(Iteratee[JsValue, _], Enumerator[JsValue])] = {

    (actor ? Join()) map {

      case Connected(session) =>

        actor ! SendToSession(session, UpdateStatus("Connected"))

        val iteratee = Iteratee.foreach[JsValue] { event =>
          println("recieved")
          // On Message
        }.mapDone { _ =>
          // On Connection Closed
          actor ! Disconnect(session)
        }

        (iteratee, session.enumerator)

      case CannotConnect(error) =>
        val iteratee = Done[JsValue, Unit]((), Input.EOF)
        val enumerator = Enumerator[JsValue](Json.obj("error" -> error)).andThen(Enumerator.enumInput(Input.EOF))
        (iteratee, enumerator)

    }
  }
}

class LiveActor extends Actor {

  var sessions = Map[String, Session]()

  def createSession(): Session = {
    val sessionId = java.util.UUID.randomUUID().toString()
    val (chatEnumerator, liveChannel) = Concurrent.broadcast[JsValue]
    Session(sessionId, liveChannel, chatEnumerator)
  }

  def receive = {
    case Join() => {
      val session = createSession()
      sessions += session.id -> session
      sender ! Connected(session)
    }
    case Disconnect(session) => {
      sessions -= session.id
    }
    case Broadcast(message) => {
      sessions.foreach {
        case (sessionId, session) => session.channel.push(Json.obj("message" -> message))
      }
    }
    case SendToSession(session: Session, message: JsonMessage) => {
      sessions(session.id).channel.push(message.toJson)
    }
    case jsonMessage: JsonMessage => {
      sessions.foreach {
        case (sessionId, session) => session.channel.push(jsonMessage.toJson)
      }
    }
    case "sessions" => {
      sender ! SessionList(sessions.foldLeft(List[Session]()) {
        case (list, (key, value)) => list :+ value
      })
    }
  }
}

trait JsonMessage {
  def toJson: JsValue
}
case class Join()
case class Connected(session: Session)
case class CannotConnect(msg: String)
case class Broadcast(message: String) {
  def toJson = Json.obj("message" -> message)
}
case class UpdateStatus(message: String) extends JsonMessage {
  def toJson = Json.obj(
    "action" -> "status",
    "message" -> message)
}
case class Session(id: String, channel: Channel[JsValue], enumerator: Enumerator[JsValue]) {
  var values: Map[String, String] = Map()
}
case class SessionList(sessions: List[Session])
case class Disconnect(session: Session)
case class SendToSession(session: Session, message: JsonMessage)
