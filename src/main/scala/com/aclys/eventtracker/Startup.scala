import com.aclys.eventtracker.service.CamelRunner
import java.io._

object Startup extends CamelRunner {
  def main(args: Array[String]) {

    start
    println("\npress enter to exit application")
    
    val br = new BufferedReader(new InputStreamReader(System.in));
    br.readLine();    
    
    stop
  }
}
