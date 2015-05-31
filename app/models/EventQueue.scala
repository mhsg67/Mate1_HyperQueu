package models

/**
 * This class represents each Queue
 */

import scala.util._

case class Event(id:String)

final class EventQueue {

  private var queue:Vector[Event] = Vector.empty

  def getNextEvent(offset:Int):Try[Event] = Try(queue(offset))

  def addNewEvent(event:Event):Unit = queue = queue :+ event

}

object EventQueue {
  def apply() = new EventQueue()
}