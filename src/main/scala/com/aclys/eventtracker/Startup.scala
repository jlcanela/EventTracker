import java.io._ 

object Startup {
  def main(args: Array[String]) {
    val cs = new com.aclys.eventtracker.service.CamelService
  
    println("\npress enter to exit application")
    
    val br = new BufferedReader(new InputStreamReader(System.in));
    br.readLine();    
    
    cs.shutdown
  }
}
