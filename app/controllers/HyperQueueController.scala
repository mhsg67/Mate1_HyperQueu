package controllers

import javax.inject.Inject
import akka.actor._
import akka.util.Timeout
import com.typesafe.config.ConfigFactory
import models.Broker.{EndOfQueue, GetNextEvent, AddNewEvent}
import models.HeadBroker._
import play.api.data.Forms._
import scala.concurrent.duration._
import play.api.mvc._
import models._
import play.api.libs.concurrent.Execution.Implicits._
import play.api.data._
import akka.pattern._

import scala.util.Random

class HyperQueueController @Inject() (system: ActorSystem) extends Controller {

  implicit val timeout = Timeout(ConfigFactory.load().getString("sessionTimeout").toInt second)
  val headBroker = system.actorOf(HeadBroker.props, "HeadBrokerActor")
  var topicToActor = Map[String,ActorRef]()

  case class newData(data:String)
  val usrForm = Form(mapping("data"->text)(newData.apply)(newData.unapply))


  def index = Action {
    val session = Random.nextString(ConfigFactory.load().getString("cookieSize").toInt)
    val topics = topicToActor.toList.map(x => x._1)
    Ok(views.html.firstPage(topics)).withSession("connected" -> session)
  }

  def producerFirstPage = Action{
    val topics = topicToActor.toList.map(x => x._1)
    Ok(views.html.producerFirstPage(topics))
  }

  def createNewTopic = Action.async {implicit request =>
    val userData = usrForm.bindFromRequest().get
    (headBroker ? AddNewTopic(userData.data)).map(result => if(result.equals(TopicExist)) Conflict else Created)
  }

  def addNewEvent(topicName:String) = Action {implicit request =>
    val userData = usrForm.bindFromRequest().get
    topicToActor.get(topicName) match {
      case None => NotFound("No such topic")
      case Some(actorRef) => {
        actorRef ! new AddNewEvent(Event(userData.data))
        Created
      }
    }
  }


  def getNextInTopic(topicName:String) = Action.async { request =>
    val actorRef = topicToActor.get(topicName).get
    val consumerId = request.session.get("connected").get
    (actorRef ? GetNextEvent(consumerId)).map(result => if(result.equals(EndOfQueue)) Ok("biib") else Ok("did"))
  }
}