package models

/**
 * This is real broker class, for each topic we
 * create a broker which hold the event queue and
 * consumer's offsets in the queue
 */

import akka.actor._
import com.typesafe.config.ConfigFactory
import scala.collection.mutable._
import scala.concurrent.duration._


object Broker {
  def props = Props[Broker]

  case class GetNextEvent(consumerId:String)
  case class AddNewEvent(event:Event)
  case class CloseSession(consumerRef:ActorRef,consumerId:String)
  case object EndOfQueue
}

class Broker extends Actor{
  import Broker._

  val eventsQueue = EventQueue()
  val consumerIdToOffset = Map[String,Int]()

  def receive = {
    case GetNextEvent(consumerId) => getNextEvent(consumerId)
    case AddNewEvent(event) => addNewEvent(event)
    case CloseSession(consumerRef,consumerId) => closeSession(consumerRef,consumerId)
  }

  import context.dispatcher
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
      val consumerRef = sender()
      val sessionHoldOnTime = ConfigFactory.load().getString("sessionTimeout").toInt.milliseconds
      context.system.scheduler.scheduleOnce(sessionHoldOnTime, self, new CloseSession(consumerRef,consumerId))
    }
  }

  def addNewEvent(event:Event) = eventsQueue.addNewEvent(event)

  def closeSession(consumerRef:ActorRef,consumerId:String) = {
    val offset = consumerIdToOffset.get(consumerId).getOrElse(0)
    val data = eventsQueue.getNextEvent(offset).getOrElse(null)
    if(data != null){
      consumerRef ! data
      consumerIdToOffset.update(consumerId,consumerIdToOffset.get(consumerId).get + 1)
    }
    else{
      consumerRef ! EndOfQueue
    }
  }
}