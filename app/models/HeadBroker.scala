package models

/**
 * This actor is supervisor off all brokers
 */

import akka.actor._
import akka.actor.SupervisorStrategy._

trait HeadBrokerResponse

object HeadBroker {
  def props = Props[HeadBroker]

  case class AddNewTopic(topicName:String)
  case class TopicAdded(broker:ActorRef)
  case object TopicExist
}

class HeadBroker extends Actor{
  import HeadBroker._

  var brokerList = List[ActorRef]()
  var topics = Set[String]()

  override val supervisorStrategy = OneForOneStrategy(){
    case _:Exception => Restart
  }

  def receive = {
    case AddNewTopic(topicName) => addNewTopic(topicName)
  }

  def addNewTopic(topicName:String) = {
    if(topics.contains(topicName))
      sender() ! TopicExist
    else{
      val actorName = "Broker" + topicName
      val actorRef = context.actorOf(Broker.props,actorName)
      topics += topicName
      sender() ! TopicAdded(actorRef)
    }
  }
}