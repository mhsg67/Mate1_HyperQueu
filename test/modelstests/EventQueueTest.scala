package modelstests

/**
 * This class test EventQueue class
 */
import models._

class EventQueueTest extends UnitSpec {

    "queue" should "throw exception" in {
        val EQ = EventQueue()
        val result = EQ.getNextEvent(0)
        result should be a 'failure
    }

    "queue" should "return the first event" in {
        val EQ = EventQueue()
        EQ.addNewEvent(new Event("1"))
        val result = EQ.getNextEvent(0)
        result should be a 'success
        result.get shouldBe a [Event]
        result.get.id should be ("1")
    }
}
