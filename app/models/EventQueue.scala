package models

/**
 * This class represents each Queue
 */
case class Event(id:String)

final class EventQueue {

  private var queue:Vector[Event] = Vector.empty

  def getNextEvent(offset:Int):Event = ???

  def addNewEvent(event:Event):Unit = ???

}

object EventQueue {
  def apply() = new EventQueue()
}