package controllers

import org.joda.time.DateTime
import org.joda.time.format.ISODateTimeFormat
import org.json4s.jackson.Serialization.{read => r, write => w}
import play.api.data.Forms._
import play.api.data._
import play.api.data.format.Formats._
import play.api.data.validation.Constraint
import play.api.mvc._
import repository._


object LocationApi extends Controller {
  val format = "yyyy-MM-dd'T'HH:mm:ss.SSS"

  implicit val formats = org.json4s.DefaultFormats ++ org.json4s.ext.JodaTimeSerializers.all

  val fmt = ISODateTimeFormat.dateHourMinuteSecondMillis()


  /*val locationForm = Form(
    mapping("id" -> optional(number),
      "category_name" -> optional(text),
      "area_name" -> optional(text),
      "name" -> optional(text),
      "updatedt" -> optional(jodaDate(format)),
      "editor" -> optional(text),
      "addr" -> optional(text),
      "homepage" -> optional(text),
      "phone" -> optional(text),
      "description" -> optional(text),
     "image0"-> optional(text),
        "image1" -> optional(text),
        "image2" -> optional(text),
        "image3" -> optional(text),
        "image4" -> optional(text),
      "latitude" -> optional(of[Double]),
      "longitude" -> optional(of[Double]),
      "restroom" -> optional(text),
      "deleted" -> optional(text),
      "opentime" -> optional(text))
      (Location.apply)(Location.unapply)
     )*/



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


  def readByTimeStr(tstr: String) = Action { implicit req =>

    Ok(w(Locations.location.applyByTime(fmt.parseDateTime(tstr))))
  }

  def map(m:Map[String, Seq[String]]) = {
    val dt= m.getOrElse("updatedt", null)
    val jdt = if(dt != null && dt.size>=1){
      Option(fmt.parseDateTime(dt.head))
    } else None
    Location(
      Option(m.getOrElse("id", Seq.empty[String]).headOption.getOrElse("0").toInt),
      m.getOrElse("category_name", Seq.empty[String]).headOption,
      m.getOrElse("area_name", Seq(m.getOrElse("addr", Seq("없음")).head.substring(0,2))).headOption,
      m.getOrElse("name", Seq.empty[String]).headOption,
      jdt,
      m.getOrElse("editor", Seq.empty[String]).headOption,
      m.getOrElse("addr", Seq.empty[String]).headOption,
      m.getOrElse("homepage", Seq.empty[String]).headOption,
      m.getOrElse("phone", Seq.empty[String]).headOption,
      m.getOrElse("description", Seq.empty[String]).headOption,
      m.getOrElse("image0", Seq.empty[String]).headOption,
      m.getOrElse("image1", Seq.empty[String]).headOption,
      m.getOrElse("image2", Seq.empty[String]).headOption,
      m.getOrElse("image3", Seq.empty[String]).headOption,
      m.getOrElse("image4", Seq.empty[String]).headOption,
      Option(m.getOrElse("latitude", Seq.empty[String]).headOption.getOrElse("0").toDouble),
      Option(m.getOrElse("longitude", Seq.empty[String]).headOption.getOrElse("0").toDouble),
      m.getOrElse("restroom", Seq("0")).headOption,
      m.getOrElse("deleted", Seq("0")).headOption,
      m.getOrElse("opentime", Seq.empty[String]).headOption)

  }

  def create() = Action { implicit req =>
    val bind: Map[String, Seq[String]] = req.body.asFormUrlEncoded.get



    val location = map(bind)
    Locations.location.apply(location)
    Ok(utils.ReturnCode.SUCCESS)
  }

  def createJson() = Action { implicit req =>
    val location = r[Location](req.body.asText.get)
    Locations.location.apply(location)
    Ok(utils.ReturnCode.SUCCESS)
  }

  def update = Action { implicit req =>
    val bind: Map[String, Seq[String]] = req.body.asFormUrlEncoded.get



    val location = map(bind)
    Locations.location.update(location)
    Ok(utils.ReturnCode.SUCCESS)
  }

  def updateJson = Action { implicit req =>
    val location = r[Location](req.body.asText.get)
    Locations.location.update(location)
    Ok(utils.ReturnCode.SUCCESS)
  }


  def delete(id: Int) = Action { req =>
    Locations.location.delete(id)
    Ok(utils.ReturnCode.SUCCESS)
  }

}