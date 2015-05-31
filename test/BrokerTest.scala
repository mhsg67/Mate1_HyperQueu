/**
 * Created by MHSG on 2015-05-31.
 */


import akka.actor.Status.Success
import akka.util.Timeout
import models.Broker._
import models._
import akka.actor.ActorSystem
import akka.testkit.TestActorRef
import com.typesafe.config.ConfigFactory
import akka.pattern.ask
import scala.concurrent.duration._

class BrokerTest extends UnitSpec {
  implicit val timeout = Timeout(5 seconds)

  "borker" should "return nothing since queue is empty" in{
    implicit  val actorSystem = ActorSystem("testActorSystem", ConfigFactory.load())
    val broker = TestActorRef(Broker.props)
    val future = broker ? GetNextEvent("c1")
    val result = future.value.get
    result should be a 'success
    result.get should be (EndOfQueue)
  }

  "broker" should "add new event to queue" in {
    implicit  val actorSystem = ActorSystem("testActorSystem", ConfigFactory.load())
    val broker = TestActorRef(Broker.props)
    broker ! AddNewEvent(Event("1"))
    val future = broker ? GetNextEvent("c1")
    val result = future.value.get
    result should be a 'success
    result.get shouldBe a [Event]
    result.get.asInstanceOf[Event].id should be ("1")
  }

  "broker" should "retrive just one event" in {
    implicit  val actorSystem = ActorSystem("testActorSystem", ConfigFactory.load())
    val broker = TestActorRef(Broker.props)
    broker ! AddNewEvent(Event("1"))

    val future1 = broker ? GetNextEvent("c1")
    val result1 = future1.value.get
    result1 should be a 'success
    result1.get shouldBe a [Event]
    result1.get.asInstanceOf[Event].id should be ("1")

    val future2 = broker ? GetNextEvent("c1")
    val result2 = future2.value.get
    result2 should be a 'success
    result2.get should be (EndOfQueue)
  }
}
