package repository

/**
 * Created by badend on 9/16/14.
 */

import java.sql.Timestamp
import java.util.Date

import actors.Actors
import akka.actor.TypedProps
import utils.{BadendTypedActorSupervisor, BeautifulConfig}

import scala.slick.driver.MySQLDriver
import scala.slick.driver.MySQLDriver.simple._
import scala.slick.jdbc.meta.MTable
import scala.slick.lifted.TableQuery

object LocationCaseClassMapping extends App {

  // the base query for the Locations table
  val locationss = TableQuery[Locations]


  DB.db.withSession { implicit session =>

    // create the schema
    locationss.ddl.create

    // insert two Location instances
    
    // print the locationss (select * from USERS)
    println(locationss.list)
  }

}

case class Location( id: Int,
category_name:Option[String],
area_name:Option[String],
name:Option[String],
createdt:Option[java.sql.Timestamp],
addr:Option[String],
homepage:Option[String],
phone:Option[String],
description:Option[String],
image0:Option[Int],
image1:Option[Int],
image2:Option[Int],
image3:Option[Int],
image4:Option[Int],
latitude:Option[Double],
longitude:Option[Double])

class Locations(tag: Tag) extends Table[Location](tag, "LOCATIONS")  {
  /*
id integer not null,
category_id integer not null,
area_id integer not null,
name text not null,
time text,
address text,
homepage text,
phone text,
description text,
image_0 text,
image_1 text,
image_2 text,
image_3 text,
image_4 text,
latitude real not null,
longitude real not null,
point real not null default 0
   */
  // Auto Increment the id primary key column
  def id = column[Int]("id", O.PrimaryKey, O.AutoInc)
  def category_name = column[String]("category_name", O.Nullable, O.DBType("VARCHAR(200)"))
  def area_name = column[String]("area_name", O.Nullable, O.DBType("VARCHAR(200)"))
  def name = column[String]("name", O.Nullable, O.DBType("VARCHAR(200)"))
  def createdt = column[Timestamp]("createdt", O.Nullable, O.DBType("TIMESTAMP"))
  def addr = column[String]("addr", O.Nullable, O.DBType("VARCHAR(1000)"))
  def homepage = column[String]("homepage", O.Nullable, O.DBType("VARCHAR(500)"))
  def phone = column[String]("phone", O.Nullable, O.DBType("VARCHAR(30)"))
  def description = column[String]("description", O.Nullable, O.DBType("TEXT"))
  def image0 = column[Int]("image0", O.Nullable, O.DBType("VARCHAR(200)"))
  def image1 = column[Int]("image1", O.Nullable,O.DBType("VARCHAR(200)"))
  def image2 = column[Int]("image2", O.Nullable,O.DBType("VARCHAR(200)"))
  def image3 = column[Int]("image3", O.Nullable,O.DBType("VARCHAR(200)"))
  def image4 = column[Int]("image4", O.Nullable,O.DBType("VARCHAR(200)"))
  def latitude = column[Double]("latitude", O.Nullable, O.DBType("FLOAT( 10, 6 )"))
  def longitude = column[Double]("longitude", O.Nullable, O.DBType("FLOAT( 10, 6 )"))


  // the * projection (e.g. select * ...) auto-transforms the tupled
  // column values to / from a Location
  def * = (id, category_name.?, area_name.?, name.?,
    createdt.?, addr.?, homepage.?, phone.?, description.?,
    image0.?, image1.?, image2.?, image3.?, image4.?,
    latitude.?, longitude.?) <> (Location.tupled, Location.unapply)
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

