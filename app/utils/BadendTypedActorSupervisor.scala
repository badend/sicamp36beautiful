package utils

import akka.actor.TypedActor

/**
 * Created by badend on 9/18/14.
 */
trait BadendTypedActorSupervisor  extends TypedActor.Supervisor {

  import akka.actor.OneForOneStrategy
  import akka.actor.SupervisorStrategy._
  import scala.concurrent.duration._

  override val supervisorStrategy =
    OneForOneStrategy(maxNrOfRetries = 10, withinTimeRange = 1 minute) {
      case e: ArithmeticException => {
        println(e.getMessage); Resume
      }
      case e: NullPointerException => {
        println(e.getMessage); Resume
      }
      case e: IllegalArgumentException => {
        println(e.getMessage); Resume
      }
      case e: Exception => {
        println(e.getMessage); Resume
      }
    }
}