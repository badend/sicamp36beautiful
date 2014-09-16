package controllers.s

import play.api.mvc._
import repository.Users

/**
 * Created by badend on 9/12/14.
 */
object Api extends Controller {
  def root() = Action {
    import scala.pickling._
    import scala.pickling.json._
    val users = Users(None)
    Ok(users.pickle.value)
  }
}
