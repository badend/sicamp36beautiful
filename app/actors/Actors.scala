package actors


import akka.actor.{TypedActor, TypedProps, ActorSystem}
import com.typesafe.config.ConfigFactory

/**
 * Created by badend on 9/22/14.
 */

object Actors {


  val config = ConfigFactory.load()
  implicit val system_actor = ActorSystem("beautiful-actor-system", config.withFallback(config))

  val typed = TypedActor(system_actor)

}


