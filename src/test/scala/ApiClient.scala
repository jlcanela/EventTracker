import dispatch._

trait ApiClient {

  private def localhost = host("localhost", 8080)

  def registerEvent(event: String) : String = {
    val req = (localhost / "event").POST.setBody(event)
    Http(req > As.string).apply()
  }

  def getReport(report : String = "userArrival") = {
    val req = (localhost / "reports" / report)
    Http(req > As.string).apply()
  }
}
