package controllers

import org.json4s._
import org.json4s.jackson.Serialization
import org.json4s.jackson.Serialization.{read => r, write => w}
import play.api.data.Forms._
import play.api.data._
import play.api.mvc._
import repository._

/**
 * Created by badend on 9/12/14.
 */
object JdbcApi extends Controller {
  implicit val formats = Serialization.formats(NoTypeHints)
  /*
  Map(ip -> , hostname -> , jdbc -> , driver -> , manager -> , repo_cate -> , user -> , password -> )

   */
  val jdbcForm = Form(
    mapping("name" -> text, "ip" -> text, "hostname" -> text, "user" -> text, "password" -> text, "jdbc" -> text
      , "repo_cate" -> text, "manager" -> text, "driver" -> text, "id" -> optional(number), "poolsize" -> optional(number))(JDBC.apply)(JDBC.unapply))

  def read(name: String) = Action { implicit req =>

    Ok(w(JDBCs.jdbc.apply(Option(name))))
  }

  def create() = Action { implicit req =>
    val jdbc = jdbcForm.bindFromRequest.get
    JDBCs.jdbc.apply(jdbc)
    Ok("ok")
  }

  def createJson() = Action { implicit req =>
    val jdbc = r[JDBC](req.body.asText.get)
    JDBCs.jdbc.apply(jdbc)
    Ok("ok")
  }

  def update = Action { implicit req =>
    val jdbc = jdbcForm.bindFromRequest.get
    JDBCs.jdbc.update(jdbc)
    Ok("ok")
  }

  def updateJson = Action { implicit req =>
    val jdbc = r[JDBC](req.body.asText.get)
    JDBCs.jdbc.update(jdbc)
    Ok("ok")
  }

  def delete(name: String) = Action { req =>
    JDBCs.jdbc.delete(name)
    Ok("ok")
  }

  def delete(id: Int) = Action { req =>
    JDBCs.jdbc.delete(id)
    Ok("ok")
  }

  def edit = Action { implicit req =>
    val data = collection.mutable.Map.empty[String, Seq[String]] ++ req.body.asFormUrlEncoded.get
    val oper = data.remove("oper").get.mkString


    oper match {
      case "del" => {
        val id = data.remove("id").get.mkString
        JDBCs.jdbc.delete(id.toInt)

      }
      case "add" => {
        val id = data.remove("id").get.mkString
        JDBCs.jdbc(jdbcForm.bind(data.map(x => (x._1, x._2.mkString)).toMap).get)
      }
      case "edit" => {
        val jdbc = jdbcForm.bind(data.map(x => (x._1, x._2.mkString)).toMap).get
        println(jdbc)
        JDBCs.jdbc.update(jdbc)
      }
      case _ => {

      }
    }
    Ok("err")
  }
}


object ApiApi extends Controller {
  implicit val formats = Serialization.formats(NoTypeHints)

  val apiForm = Form(
    mapping("apicode" -> text,
      "jdbcname" -> text,
      "dbname" -> text,
      "tablename" -> text,
      "keycols" -> text,
      "valcols" -> text,
      "expireSec" -> longNumber,
      "redis" -> text, "id" -> optional(number), "manager" -> optional(text), "rangecol" -> optional(text))(API.apply)(API.unapply))


  def read(name: String) = Action { implicit req =>

    Ok(w(APIs.api.apply(Option(name))))
  }

  def create() = Action { implicit req =>
    val api = apiForm.bindFromRequest.get
    APIs.api.apply(api)
    Ok("ok")
  }

  def createJson() = Action { implicit req =>
    val api = r[API](req.body.asText.get)
    APIs.api.apply(api)
    Ok("ok")
  }

  def update = Action { implicit req =>
    val api = apiForm.bindFromRequest.get
    APIs.api.update(api)
    Ok("ok")
  }

  def updateJson = Action { implicit req =>
    val api = r[API](req.body.asText.get)
    APIs.api.update(api)
    Ok("ok")
  }

  def delete(name: String) = Action { req =>
    APIs.api.delete(name)
    Ok("ok")
  }

  def delete(id: Int) = Action { req =>
    APIs.api.delete(id)
    Ok("ok")
  }

  def edit = Action { implicit req =>
    val data = collection.mutable.Map.empty[String, Seq[String]] ++ req.body.asFormUrlEncoded.get
    val oper = data.remove("oper").get.mkString


    oper match {
      case "del" => {
        val id = data.remove("id").get.mkString
        APIs.api.delete(id.toInt)

      }
      case "add" => {
        val id = data.remove("id").get.mkString
        APIs.api(apiForm.bind(data.map(x => (x._1, x._2.mkString)).toMap).get)
      }
      case "edit" => {
        val api = apiForm.bind(data.map(x => (x._1, x._2.mkString)).toMap).get
        println(api)
        APIs.api.update(api)
      }
      case _ => {

      }
    }
    Ok("err")
  }
}

object ServiceK1V1Api extends Controller {
  implicit val formats = Serialization.formats(NoTypeHints)

  val serviceForm = Form(
    tuple("k1" -> text,
      "v1" -> text,
      "range" -> optional(longNumber)
      ))


  def read(apicode:String, name: String) = Action { implicit req =>

    Ok(w(ServiceCRUDs.DBs(apicode)(Option(name))))
  }

  def readRange(apicode:String, from:Long, to:Long) = Action { implicit req =>

    Ok(w(ServiceCRUDs.DBs(apicode)(from, to)))
  }
  def readCount(apicode:String, from:Long=0L, count:Long=1) = Action { implicit req =>

    Ok(w(ServiceCRUDs.DBs(apicode).readWithCount(from, count)))
  }

  def readCountNoFrom(apicode:String, count:Long=1) = Action { implicit req =>

    val api = ServiceCRUDs.DBs.get(apicode)
    println("--------------------"+api)
    println(api)
    Ok(w(api.readWithCount(0L, count)))
  }
  def create(apicode:String) = Action { implicit req =>
    val service = serviceForm.bindFromRequest.get


    Ok("ok")
  }
  /*

  def createJson() = Action { implicit req =>
    val api = r[(String, String)](req.body.asText.get)
    APIs.api.apply(api)
    Ok("ok")
  }

  def update = Action { implicit req =>
    val api = apiForm.bindFromRequest.get
    APIs.api.update(api)
    Ok("ok")
  }

  def updateJson = Action { implicit req =>
    val api = r[API](req.body.asText.get)
    APIs.api.update(api)
    Ok("ok")
  }

  def delete(name: String) = Action { req =>
    APIs.api.delete(name)
    Ok("ok")
  }

  def delete(id: Int) = Action { req =>
    APIs.api.delete(id)
    Ok("ok")
  }

  def edit = Action { implicit req =>
    val data = collection.mutable.Map.empty[String, Seq[String]] ++ req.body.asFormUrlEncoded.get
    val oper = data.remove("oper").get.mkString


    oper match {
      case "del" => {
        val id = data.remove("id").get.mkString
        APIs.api.delete(id.toInt)

      }
      case "add" => {
        val id = data.remove("id").get.mkString
        APIs.api(apiForm.bind(data.map(x => (x._1, x._2.mkString)).toMap).get)
      }
      case "edit" => {
        val api = apiForm.bind(data.map(x => (x._1, x._2.mkString)).toMap).get
        println(api)
        APIs.api.update(api)
      }
      case _ => {

      }
    }
    Ok("err")
  }
  */
}
