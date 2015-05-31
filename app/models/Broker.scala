package models

/**
 * This is real broker class, for each topic we
 * create a broker which hold the event queue and
 * consumer's offsets in the queue
 */

import akka.actor._

object Broker {
  case class getNextEvent(consumerId:String)
  case class addNewEvent(event:Event)
  case class closeSession(consumerId:String)
}

class Broker extends Actor{
  import Broker._

  val eventsQueue = EventQueue()
  var consumerIdToOffset = Map[String,Int]()

  def receive = {
    case getNextEvent(consumerId) => ???
    case addNewEvent(event) => ???
    case closeSession(consumerId) => ???
  }
}