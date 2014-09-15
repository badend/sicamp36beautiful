package controllers

import actors._
import akka.actor._
import akka.actor.ActorRef
import com.fasterxml.jackson.databind.JsonNode
import play.api.mvc._
import play.libs.Akka
import play.libs.F
import repository.Users
import scala.Option
import play.api.libs.json.Json

/**
 * Created by badend on 9/12/14.
 */
class Api extends Controller {
  def root() = Action {
    import scala.pickling._
    import json._
    Ok(Users(0).pickle.value)
  }
}
