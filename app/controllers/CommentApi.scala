package controllers

import java.sql.Timestamp

import org.apache.commons.lang3.time.FastDateFormat
import org.joda.time.DateTime
import org.joda.time.format.ISODateTimeFormat
import org.json4s._
import org.json4s.jackson.Serialization
import org.json4s.jackson.Serialization.{read => r, write => w}
import play.api.data.Forms._
import play.api.data.format.Formats._
import play.api.data._
import play.api.data.format.Formatter
import play.api.mvc._
import repository._



object CommentApi extends Controller {
  val format = "yyyy-MM-dd'T'HH:mm:ss.SSS"

  implicit val formats = org.json4s.DefaultFormats ++ org.json4s.ext.JodaTimeSerializers.all

  val fmt = ISODateTimeFormat.dateHourMinuteSecondMillis()
  val commentForm = Form(
    mapping( "id"-> optional(number),
      "user_id" -> text,
      "editor" -> text,
      "content" -> optional(text),
      "updatedt" -> jodaDate(format),
      "location_id" -> number,
      "score" -> optional(number))
      (Comment.apply)(Comment.unapply))

  def readAll = Action { implicit req =>

    Ok(w(Comments.comment.applyByTime()))
  }

  def read(id: Int) = Action { implicit req =>

    Ok(w(Comments.comment.apply(id)))
  }

  def readFromId(id: Int) = Action { implicit req =>

    Ok(w(Comments.comment.applyFrom(id)))
  }

  def readByLocationId(id:Int) =Action { implicit req =>

    Ok(w(Comments.comment.applyByLocationId(id)))
  }
  def readByTime(time: Long) = Action { implicit req =>

    Ok(w(Comments.comment.applyByTime(new DateTime(time))))
  }

  def readByTimeStr(tstr:String) = Action { implicit req =>

    Ok(w(Comments.comment.applyByTime(fmt.parseDateTime(tstr))))
  }

  def create() = Action { implicit req =>
    val bind = commentForm.bindFromRequest

    bind.errors.foreach(x=>println(x))
    val comment = bind.get
    Comments.comment.apply(comment)
    Ok(utils.ReturnCode.SUCCESS)
  }

  def createJson() = Action { implicit req =>
    val comment = r[Comment](req.body.asText.get)
    Comments.comment.apply(comment)
    Ok(utils.ReturnCode.SUCCESS)
  }

  def update = Action { implicit req =>
    val comment = commentForm.bindFromRequest.get
    println(s"controller = $comment")
    Comments.comment.update(comment)
    Ok(utils.ReturnCode.SUCCESS)
  }

  def updateJson = Action { implicit req =>
    val comment = r[Comment](req.body.asText.get)
    Comments.comment.update(comment)
    Ok(utils.ReturnCode.SUCCESS)
  }


  def delete(id: Int) = Action { req =>
    Comments.comment.delete(id)
    Ok(utils.ReturnCode.SUCCESS)
  }

  def edit = Action { implicit req =>
    val data = collection.mutable.Map.empty[String, Seq[String]] ++ req.body.asFormUrlEncoded.get
    val oper = data.remove("oper").get.mkString


    oper match {
      case "del" => {
        val id = data.remove("id").get.mkString
        Comments.comment.delete(id.toInt)

      }
      case "add" => {
        val id = data.remove("id").get.mkString
        Comments.comment(commentForm.bind(data.map(x => (x._1, x._2.mkString)).toMap).get)
      }
      case "edit" => {
        val comment = commentForm.bind(data.map(x => (x._1, x._2.mkString)).toMap).get
        println(Comment)
        Comments.comment.update(comment)
      }
      case _ => {

      }
    }
    Ok("err")
  }
}
