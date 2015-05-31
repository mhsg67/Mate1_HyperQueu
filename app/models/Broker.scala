package models

/**
 * This is real broker class, for each topic we
 * create a broker which hold the event queue and
 * consumer's offsets in the queue
 */

import akka.actor._
import scala.collection.mutable._

object Broker {
  def props = Props[Broker]

  case class GetNextEvent(consumerId:String)
  case class AddNewEvent(event:Event)
  case class CloseSession(consumerId:String)
  case object EndOfQueue
}

class Broker extends Actor{
  import Broker._

  val eventsQueue = EventQueue()
  val consumerIdToOffset = Map[String,Int]()

  def receive = {
    case GetNextEvent(consumerId) => getNextEvent(consumerId)
    case AddNewEvent(event) => addNewEvent(event)
    case CloseSession(consumerId) => ???
  }

  def getNextEvent(consumerId:String) = {
    val offset = consumerIdToOffset.get(consumerId).getOrElse(0)
    if(offset == 0)
      consumerIdToOffset.update(consumerId,0)

    val data = eventsQueue.getNextEvent(offset).getOrElse(null)
    if(data != null){
      sender() ! data
      consumerIdToOffset.update(consumerId,consumerIdToOffset.get(consumerId).get + 1)
    }
    else{
      sender() ! EndOfQueue
      //TODO:scheduler a self message (closesession()) to remove this consumer after a while
    }
  }


  def addNewEvent(event:Event) = eventsQueue.addNewEvent(event)
}