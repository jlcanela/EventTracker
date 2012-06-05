import org.specs2._

class EventRegistrationSpec extends Specification {  def is =


  "This is a specification for the event registration feature"                                    ^
    p^
    "The event registration feature enables"                                                      ^
    "using client API to register a valid event"                                            ! todo^
    "using client API to generate error when event is not a valid json structure"           ! todo^
    "using client API to generate error when event is missing either event type or userId"  ! todo^
    end

  val eventTemplate =
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

  val serviceUri = "/event"


//  def e1 = "Hello world" must have size(11)
//  def e2 = "Hello world" must startWith("Hello")
//  def e3 = "Hello world" must endWith("world")
}