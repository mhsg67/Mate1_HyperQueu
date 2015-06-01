package controllers

import javax.inject.Inject
import akka.actor._
import akka.util.Timeout
import com.typesafe.config.ConfigFactory
import models.Broker.{EndOfQueue, GetNextEvent, AddNewEvent}
import models.HeadBroker._
import play.api.data.Forms._
import scala.concurrent.Future
import scala.concurrent.duration._
import play.api.mvc._
import models._
import play.api.libs.concurrent.Execution.Implicits._
import play.api.data._
import akka.pattern._
import play.api.libs.json._

import scala.util.Random

class HyperQueueController @Inject() (system: ActorSystem) extends Controller {

  implicit val timeout = Timeout(ConfigFactory.load().getString("sessionTimeout").toInt second)
  val headBroker = system.actorOf(HeadBroker.props, "HeadBrokerActor")
  var topicToActor = Map[String,ActorRef]()

  case class newData(data:String)
  val usrForm = Form(mapping("data"->text)(newData.apply)(newData.unapply))


  def index = Action {
    val session = Random.nextString(ConfigFactory.load().getString("cookieSize").toInt)
    val topics = topicToActor.toList.map(x => x._1).sortWith((x,y) => x <= y )
    Ok(views.html.firstPage(topics)).withSession("connected" -> session)
  }

  def producerFirstPage = Action{
    val topics = topicToActor.toList.map(x => x._1).sortWith((x,y) => x <= y )
    Ok(views.html.producerFirstPage(topics))
  }

  def createNewTopic = Action.async {implicit request =>
    val userData = usrForm.bindFromRequest().get
    (headBroker ? AddNewTopic(userData.data)).map(
      result => result match {
        case TopicAdded(actorRef) => {
          topicToActor = topicToActor.updated(userData.data,actorRef)
          Ok("ACK")
        }
        case _ => Ok("NCK")
      }

    )
  }

  def addNewEvent(topicName:String) = Action {implicit request =>
    val userData = usrForm.bindFromRequest().get
    topicToActor.get(topicName).map(actorRef => {
      actorRef ! new AddNewEvent(Event(userData.data))
      Ok("ACK")
    }
    ).getOrElse(Ok("NCK"))
  }

  def getNextInTopic(topicName:String) = Action.async { request =>
    topicToActor.get(topicName).map(
      actorRef => request.session.get("connected").map(
        consumerId => (actorRef ?GetNextEvent(consumerId)).map(
          result => result match {
            case EndOfQueue => Ok("EOQ")
            case x:Event => Ok(Json.obj("data" -> x.id))
          }
        )
      ).getOrElse(Future(Ok("NCK")))
    ).getOrElse(Future(Ok("NCK")))
  }
}