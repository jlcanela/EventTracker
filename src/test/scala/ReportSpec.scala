import org.specs2._

class ReportSpec extends Specification { def is =

  "This is a specification for the userArrival Event report"                                      ^
    p^
    "The event registration feature enables"                                                      ^
    "using client API to register a valid event"                                            ! todo^
    "using client API to generate error when event is not a valid json structure"           ! todo^
    "using client API to generate error when event is missing either event type or userId"  ! todo^
    end

  val reportTemplate =
    """
      |{
      |    "date-start": "2012-05-31",
      |    "date-end": "2012-06-01",
      |    "data": [{
      |        "date": "2012-05-31",
      |        "value": 1
      |    },
      |    {
      |        "date": "2012-06-01",
      |        "value": 4
      |    }]
      |}
    """.stripMargin

  val serviceUri = "reports/userArrival"

//  def e1 = "Hello world" must have size(11)
//  def e2 = "Hello world" must startWith("Hello")
//  def e3 = "Hello world" must endWith("world")
}