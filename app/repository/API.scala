package repository

import actors.Actors
import akka.actor.TypedProps
import utils.BadendTypedActorSupervisor

import scala.slick.driver.MySQLDriver
import scala.slick.driver.MySQLDriver.simple._
import scala.slick.lifted.TableQuery

object APICaseClassMapping extends App {

  // the base query for the Locations table
  val jdbc = TableQuery[APIs]


  DB.db.withSession { implicit session =>

    // create the schema
    jdbc.ddl.create

    // insert two Location instances

    // print the locationss (select * from USERS)
    println(jdbc.list)
  }

}

case class API( apicode: String,
                 jdbcname:String,
                 dbname:String,
                 tablename:String,
                 keycols:String,
                 valcols:String,
                 expireSec:Long,
                 redis: String, id: Option[Int] = None, manager: Option[String], rangecol: Option[String])


class APIs(tag: Tag) extends Table[API](tag, "API")  {

  def apicode = column[String]("apicode", O.PrimaryKey, O.DBType("VARCHAR(45)"))
  def jdbcname = column[String]("jdbcname", O.DBType("VARCHAR(45)"))
  def dbname = column[String]("dbname", O.DBType("VARCHAR(45)"))
  def tablename = column[String]("tablename", O.DBType("VARCHAR(45)"))
  def keycols = column[String]("keycols", O.DBType("VARCHAR(1000)"))
  def valcols = column[String]("valcols", O.DBType("VARCHAR(2000)"))

  def expireSec = column[Long]("expireSec", O.DBType("INT"))
  def redis = column[String]("redis", O.DBType("CHAR(1)"))

  def id = column[Int]("id", O.PrimaryKey, O.AutoInc, O.DBType("INT"))

  def manager = column[String]("manager", O.Nullable, O.DBType("VARCHAR(45)"))

  def rangecol = column[String]("rangecol", O.Nullable, O.DBType("VARCHAR(45)"))


  def * = (apicode, jdbcname, dbname, tablename, keycols, valcols, expireSec, redis, id.?, manager.?, rangecol.?) <>(API.tupled, API.unapply)
}

class APICRUD extends APICRUDT with BadendTypedActorSupervisor{
  val tapi = TableQuery[APIs]


  def apply(name:Option[String]): Seq[API] = {
    val apis: Seq[API] =  DB.db.withSession { implicit session =>
      if(name.isEmpty) tapi.run
      else tapi.filter(x => x.apicode === name.get).run
    }

    println(apis.size)
    apis.foreach(x=>println(x))
    apis

  }
  def apply(api:API): MySQLDriver.InsertInvoker[APIs#TableElementType]#SingleInsertResult ={
    val r = DB.db.withSession { implicit session =>
      tapi += api
    }
    println(r)
    r
  }
  def apply(api:Seq[API]): MySQLDriver.InsertInvoker[APIs#TableElementType]#MultiInsertResult ={
    val r = DB.db.withSession { implicit session =>
      tapi ++= api
    }
    println(r)
    r
  }
  def update(api:API): Int ={
    DB.db.withSession { implicit session =>
      tapi.filter(_.id === api.id).update(api)
    }
  }

  def delete(api:API): Int ={
    DB.db.withSession { implicit session =>
      tapi.filter(_.apicode === api.apicode).delete
    }
  }

  def delete(name:String): Int ={
    DB.db.withSession { implicit session =>
      tapi.filter(_.apicode === name).delete
    }
  }

  def delete(id: Int): Int = {
    DB.db.withSession { implicit session =>
      tapi.filter(_.id === id).delete
    }
  }


}


object APIs{
  val typed = Actors.typed
  val api: APICRUDT= typed.typedActorOf(TypedProps[APICRUD]())
}
trait APICRUDT {

  val tapi: TableQuery[APIs]

  def apply(name: Option[String]): Seq[API]

  def apply(api: API):MySQLDriver.InsertInvoker[APIs#TableElementType]#SingleInsertResult

  def apply(api: Seq[API]):MySQLDriver.InsertInvoker[APIs#TableElementType]#MultiInsertResult

  def update(api: API):Int

  def delete(api: API):Int

  def delete(id: Int): Int
  def delete(name:String):Int
}

