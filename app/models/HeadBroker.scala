package models

/**
 * This actor is supervisor off all brokers
 */

import akka.actor._
import akka.actor.SupervisorStrategy._

object HeadBroker {
  def props = Props[HeadBroker]

  case class addNewTopic(topicName:String)
}

class HeadBroker extends Actor{
  import HeadBroker._

  override val supervisorStrategy = OneForOneStrategy {
    case _:Exception => Restart
  }

  def receive = {
    case addNewTopic(topicName) => ???
  }
}