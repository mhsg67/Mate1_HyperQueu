package controllers

import javax.inject.Inject
import akka.actor._
import akka.util.Timeout
import com.typesafe.config.ConfigFactory
import scala.concurrent.duration._
import play.api.mvc._
import models._

class HyperQueueController @Inject() (system: ActorSystem) extends Controller {

  implicit val timeout = Timeout(ConfigFactory.load().getString("sessionTimeout").toInt second)
  val headBroker = system.actorOf(HeadBroker.props, "HeadBrokerActor")
  var topicToActor = Map[String,ActorRef]()

  def createNewTopic = ???

  def addNewEvent = ???

  def getNextInTopic(topicName:String) = ???
}