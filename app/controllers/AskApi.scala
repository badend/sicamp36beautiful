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



object AskApi extends Controller {
  val format = "yyyy-MM-dd'T'HH:mm:ss.SSS"

  implicit val formats = org.json4s.DefaultFormats ++ org.json4s.ext.JodaTimeSerializers.all

  val fmt = ISODateTimeFormat.dateHourMinuteSecondMillis()
  val askForm = Form(
    mapping( "id"-> optional(number),
      "editor" -> optional(text),
      "email" -> optional(text),
      "content" -> optional(text),
      "updatedt" -> optional(jodaDate(format)),
      "location_id" -> optional(number))
       (Ask.apply)(Ask.unapply))

  def readAll = Action { implicit req =>

    Ok(w(Asks.ask.applyByTime()))
  }

  def read(id: Int) = Action { implicit req =>

    Ok(w(Asks.ask.apply(id)))
  }

  def readFromId(id: Int) = Action { implicit req =>

    Ok(w(Asks.ask.applyFrom(id)))
  }

  def readByLocationId(id:Int) =Action { implicit req =>

    Ok(w(Asks.ask.applyByLocationId(id)))
  }
  def readByTime(time: Long) = Action { implicit req =>

    Ok(w(Asks.ask.applyByTime(new DateTime(time))))
  }

  def readByTimeStr(tstr:String) = Action { implicit req =>

    Ok(w(Asks.ask.applyByTime(fmt.parseDateTime(tstr))))
  }

  def create() = Action { implicit req =>
    val bind = askForm.bindFromRequest

    bind.errors.foreach(x=>println(x))
    val ask = bind.get
    Asks.ask.apply(ask)
    Ok(utils.ReturnCode.SUCCESS)
  }

  def createJson() = Action { implicit req =>
    val ask = r[Ask](req.body.asText.get)
    Asks.ask.apply(ask)
    Ok(utils.ReturnCode.SUCCESS)
  }

  def update = Action { implicit req =>
    val ask = askForm.bindFromRequest.get
    println(s"controller = $ask")
    Asks.ask.update(ask)
    Ok(utils.ReturnCode.SUCCESS)
  }

  def updateJson = Action { implicit req =>
    val ask = r[Ask](req.body.asText.get)
    Asks.ask.update(ask)
    Ok(utils.ReturnCode.SUCCESS)
  }


  def delete(id: Int) = Action { req =>
    Asks.ask.delete(id)
    Ok(utils.ReturnCode.SUCCESS)
  }

  def edit = Action { implicit req =>
    val data = collection.mutable.Map.empty[String, Seq[String]] ++ req.body.asFormUrlEncoded.get
    val oper = data.remove("oper").get.mkString


    oper match {
      case "del" => {
        val id = data.remove("id").get.mkString
        Asks.ask.delete(id.toInt)

      }
      case "add" => {
        val id = data.remove("id").get.mkString
        Asks.ask(askForm.bind(data.map(x => (x._1, x._2.mkString)).toMap).get)
      }
      case "edit" => {
        val ask = askForm.bind(data.map(x => (x._1, x._2.mkString)).toMap).get
        println(Ask)
        Asks.ask.update(ask)
      }
      case _ => {

      }
    }
    Ok("err")
  }
}
