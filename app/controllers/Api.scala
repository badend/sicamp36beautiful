package controllers

import actors._
import akka.actor._
import akka.actor.ActorRef
import com.fasterxml.jackson.databind.JsonNode
import play.api.mvc._
import play.libs.Akka
import play.libs.F
import play.mvc._
import repository.Users
import scala.Option
import play.api.libs.json.Json

/**
 * Created by badend on 9/12/14.
 */
class Api extends Controller {
  def root() = Action {

    Ok(Json.toJson(Users(0)))
  }
}
