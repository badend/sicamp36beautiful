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



object LocationApi extends Controller {
  val format = "yyyy-MM-dd'T'HH:mm:ss.SSS"
  implicit val formats = Serialization.formats(NoTypeHints)
  val fmt = ISODateTimeFormat.dateHourMinuteSecondMillis()
  val locationForm = Form(
    mapping( "id"-> optional(number),
"category_name"->optional(text),
"area_name"->optional(text),
"name"->optional(text),
"updatedt"->optional(jodaDate(format)),
"addr"->optional(text),
"homepage"->optional(text),
"phone"->optional(text),
"description"->optional(text),
"image0"->optional(text),
"image1"->optional(text),
"image2"->optional(text),
"image3"->optional(text),
"image4"->optional(text),
"latitude"->optional(of[Double]),
"longitude"->optional(of[Double]))
      (Location.apply)(Location.unapply))


  def readByName(name: String) = Action { implicit req =>

    Ok(w(Locations.location.apply(Option(name))))
  }

  def readAll = Action { implicit req =>

    Ok(w(Locations.location.apply(None)))
  }

  def read(id: Int) = Action { implicit req =>

    Ok(w(Locations.location.apply(id)))
  }

  def readFromId(id: Int) = Action { implicit req =>

    Ok(w(Locations.location.applyFrom(id)))
  }

  def readByTime(time: Long) = Action { implicit req =>

    Ok(w(Locations.location.applyByTime(new DateTime(time))))
  }

  def readByTimeStr(tstr:String) = Action { implicit req =>

    Ok(w(Locations.location.applyByTime(fmt.parseDateTime(tstr))))
  }

  def create() = Action { implicit req =>
    val bind = locationForm.bindFromRequest

    bind.errors.foreach(x=>println(x))
    val location = bind.get
    Locations.location.apply(location)
    Ok("ok")
  }

  def createJson() = Action { implicit req =>
    val location = r[Location](req.body.asText.get)
    Locations.location.apply(location)
    Ok("ok")
  }

  def update = Action { implicit req =>
    val location = locationForm.bindFromRequest.get
    Locations.location.update(location)
    Ok("ok")
  }

  def updateJson = Action { implicit req =>
    val location = r[Location](req.body.asText.get)
    Locations.location.update(location)
    Ok("ok")
  }


  def delete(id: Int) = Action { req =>
    Locations.location.delete(id)
    Ok("ok")
  }

  def edit = Action { implicit req =>
    val data = collection.mutable.Map.empty[String, Seq[String]] ++ req.body.asFormUrlEncoded.get
    val oper = data.remove("oper").get.mkString


    oper match {
      case "del" => {
        val id = data.remove("id").get.mkString
        Locations.location.delete(id.toInt)

      }
      case "add" => {
        val id = data.remove("id").get.mkString
        Locations.location(locationForm.bind(data.map(x => (x._1, x._2.mkString)).toMap).get)
      }
      case "edit" => {
        val location = locationForm.bind(data.map(x => (x._1, x._2.mkString)).toMap).get
        println(location)
        Locations.location.update(location)
      }
      case _ => {

      }
    }
    Ok("err")
  }
}