import com.aclys.eventtracker.service.CamelRunner
import org.specs2._
import specification._

class EventRegistrationSpec extends Specification with CamelRunner with ApiClient {

  override def map(fs: =>Fragments) = Step(start) ^ super.map(fs) ^
    Step(stop)

  def is =

  "This is a specification for the event registration feature" ^
    p ^
    "The event registration feature enables" ^
    "using client API to register a valid event" ! registerValidEvent ^
    "using client API to generate error when event is not a valid json structure" ! registerNonJsonEvent ^
    "using client API to generate error when event is missing either event type or userId" ! registerWithMissingData ^
    end

  val aValidEvent =
    """
      |{
      |    "eventType": "userArrival",
      |    "userId": "user:7",
      |    "searchKeyword": "event",
      |    "referringWebsite": "http://www.google.com",
      |    "productId": "product:1",
      |    "category": "Laptop"
      |}
    """.stripMargin

  val anInvalidEvent =
    """
      |{
      |    "searchKeyword": "event",
      |    "referringWebsite": "http://www.google.com",
      |    "productId": "product:1",
      |    "category": "Laptop"
      |}
    """.stripMargin

  val serviceUri = "/event"

  def registerValidEvent = {
    val r = registerEvent(aValidEvent)
    r must startWith("""{"creationDate":"201""")
  }

  def registerNonJsonEvent = {
    val r = registerEvent("Non JSon Event")
    r must startWith("""{"status":"fail","data":"unknown token N""")
  }

  def registerWithMissingData = {
    val r = registerEvent(anInvalidEvent)
    r must be equalTo("""{"status":"fail","data":["eventType is mandatory","userId is mandatory"]}""")
  }

}