package repository

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



object UtilApi extends Controller {

  def upload = Action(parse.multipartFormData) { request =>
    request.body.file("file").map { picture =>
      import java.io.File
      val filename = picture.filename
      val contentType = picture.contentType
      picture.ref.moveTo(new File(s"/var/www/files/$filename"))

    }
    Ok(utils.ReturnCode.SUCCESS)
  }
}
