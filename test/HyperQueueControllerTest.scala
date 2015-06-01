import org.specs2.mutable._
import org.specs2.runner._
import org.junit.runner._
import play.api.libs.json._

import play.api.test._
import play.api.test.Helpers._

@RunWith(classOf[JUnitRunner])
class HyperQueueControllerTest extends Specification {

  "controller" should {
    "send created topic" in new WithApplication{
      val json = Json.obj("data" -> "t1")
      val future = route(FakeRequest(POST,"/createNewTopic",FakeHeaders(),json)).get

      status(future) must equalTo(OK)
      contentAsString(future) must contain ("ACK")
    }

    "send confilct topic" in new WithApplication() {
      val json = Json.obj("data" -> "t1")
      val result1 = route(FakeRequest(POST,"/createNewTopic",FakeHeaders(),json)).get
      status(result1) must equalTo(OK)
      contentAsString(result1) must contain ("ACK")

      val result2 = route(FakeRequest(POST,"/createNewTopic",FakeHeaders(),json)).get
      status(result2) must equalTo(OK)
      contentAsString(result2) must contain ("NCK")
      
    }
  }

}
