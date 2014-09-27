package repository

/**
 * Created by badend on 9/16/14.
 */

import com.github.nscala_time.time.Imports._

import actors.Actors
import akka.actor.TypedProps
import org.joda.time.DateTime
import utils.{BadendTypedActorSupervisor, BeautifulConfig}

import scala.slick.driver.MySQLDriver
import scala.slick.driver.MySQLDriver.simple._
import scala.slick.jdbc.meta.MTable
import scala.slick.lifted.TableQuery
import com.github.tototoshi.slick._
import com.github.tototoshi.slick.JdbcJodaSupport._

object LocationCaseClassMapping extends App {

  // the base query for the Locations table
  val locationss = TableQuery[Locations]



  DB.db.withSession { implicit session =>
    locationss.ddl.drop
    // create the schema
    locationss.ddl.create

    // insert two Location instances
    
    // print the locationss (select * from USERS)
    println(locationss.list)
  }

}

case class Location( id: Option[Int],
category_name:Option[String],
area_name:Option[String],
name:Option[String],
updatedt:Option[DateTime],
editor:Option[String],
addr:Option[String],
homepage:Option[String],
phone:Option[String],
description:Option[String],
image0:Option[String],
image1:Option[String],
image2:Option[String],
image3:Option[String],
image4:Option[String],
latitude:Option[Double],
longitude:Option[Double],
restroom:Option[String],
deleted:Option[String],
opentime:Option[String])
/*case class Image(
                  )

object Location2 {
  def apply (id: Option[Int],
                       category_name:Option[String],
                       area_name:Option[String],
                       name:Option[String],
                       updatedt:Option[DateTime],
                       editor:Option[String],
                       addr:Option[String],
                       homepage:Option[String],
                       phone:Option[String],
                       description:Option[String],
                       image0:Option[String],
                       image1:Option[String],
                       image2:Option[String],
                       image3:Option[String],
                       image4:Option[String],
                       latitude:Option[Double],
                       longitude:Option[Double],
                       restroom:Option[String],
                       deleted:Option[String],
                       opentime:Option[String]) = {
    Location(id,
      category_name,
      area_name,
      name,
      updatedt,
      editor,
      addr,
      homepage,
      phone,
      description,
      Image(image0,image1,image2,image3,image4),
      latitude,
      longitude,
      restroom,
      deleted,
      opentime)
  }

  def unapply(l:Location) = {
    (l.id,
      l.category_name,
      l.area_name,
      l.name,
      l.updatedt,
      l.editor,
      l.addr,
      l.homepage,
      l.phone,
      l.description,
      l.image.image0,l.image.image1,l.image.image2,l.image.image3,l.image.image4,
      l.latitude,
      l.longitude,
      l.restroom,
      l.deleted,
      l.opentime)
  }
}
*/
class Locations(tag: Tag) extends Table[Location](tag, "LOCATIONS")  {


  // Auto Increment the id primary key column
  def id = column[Int]("id", O.PrimaryKey, O.AutoInc)
  def category_name = column[String]("category_name", O.Nullable, O.DBType("VARCHAR(200)"))
  def area_name = column[String]("area_name", O.Nullable, O.DBType("VARCHAR(200)"))
  def name = column[String]("name", O.Nullable, O.DBType("VARCHAR(200)"))
  def updatedt= column[DateTime]("updatedt", O.Nullable, O.DBType("TIMESTAMP"), O.Default(DateTime.now))
  def editor = column[String]("editor", O.Nullable, O.DBType("VARCHAR(200)"))
  def addr = column[String]("addr", O.Nullable, O.DBType("VARCHAR(1000)"))
  def homepage = column[String]("homepage", O.Nullable, O.DBType("VARCHAR(500)"))
  def phone = column[String]("phone", O.Nullable, O.DBType("VARCHAR(30)"))
  def description = column[String]("description", O.Nullable, O.DBType("TEXT"))
  def image0 = column[String]("image0", O.Nullable, O.DBType("VARCHAR(200)"))
  def image1 = column[String]("image1", O.Nullable,O.DBType("VARCHAR(200)"))
  def image2 = column[String]("image2", O.Nullable,O.DBType("VARCHAR(200)"))
  def image3 = column[String]("image3", O.Nullable,O.DBType("VARCHAR(200)"))
  def image4 = column[String]("image4", O.Nullable,O.DBType("VARCHAR(200)"))
  def latitude = column[Double]("latitude", O.Nullable, O.DBType("FLOAT( 10, 6 )"))
  def longitude = column[Double]("longitude", O.Nullable, O.DBType("FLOAT( 10, 6 )"))
  def restroom = column[String]("restroom", O.DBType("VARCHAR(2)"))
  def deleted = column[String]("deleted", O.DBType("VARCHAR(2)"))
  def opentime = column[String]("opentime", O.Nullable,O.DBType("VARCHAR(20)"))
  def idx = index("idx_updatedt", (updatedt))

  // the * projection (e.g. select * ...) auto-transforms the tupled
  // column values to / from a Location
  def * = (id.?, category_name.?, area_name.?, name.?,
    updatedt.?, editor.?, addr.?, homepage.?, phone.?, description.?,
    image0.?, image1.?, image2.?, image3.?, image4.?,
    latitude.?, longitude.?, restroom.?, deleted.?, opentime.?) <> (Location.tupled, Location.unapply)

}


class LocationCRUD extends LocationCRUDT with BadendTypedActorSupervisor{
  val tlocation = TableQuery[Locations]


  def apply(name:Option[String]): Seq[Location] = {
    val locations: Seq[Location] =  DB.db.withSession { implicit session =>
      if(name.isEmpty) tlocation.run
      else tlocation.filter(x => x.name === name.get).run
    }

    locations

  }

  def apply(id:Int): Seq[Location] = {
    val locations: Seq[Location] =  DB.db.withSession { implicit session =>
      if(id==0) tlocation.run
      else tlocation.filter(x => x.id === id).run
    }

    println(locations.size)
    locations.foreach(x=>println(x))
    locations
  }

  def applyFrom(id:Int): Seq[Location] = {
    val locations: Seq[Location] =  DB.db.withSession { implicit session =>
     tlocation.filter(x => x.id >= id).run
    }

    println(locations.size)
    locations.foreach(x=>println(x))
    locations
  }

  def applyByTime(ts:DateTime=new DateTime(0)): Seq[Location] = {
    val locations: Seq[Location] =  DB.db.withSession { implicit session =>
      tlocation.filter(x => x.updatedt >= ts).sortBy(_.updatedt.asc).run
    }

    println(locations.size)
    locations.foreach(x=>println(x))
    locations
  }

  def apply(location:Location): MySQLDriver.InsertInvoker[Locations#TableElementType]#SingleInsertResult ={
    val r = DB.db.withSession { implicit session =>
      tlocation += location
    }
    println(r)
    r
  }
  def apply(location:Seq[Location]): MySQLDriver.InsertInvoker[Locations#TableElementType]#MultiInsertResult ={
    val r = DB.db.withSession { implicit session =>
      tlocation ++= location
    }
    println(r)
    r
  }
  def update(location:Location): Int ={
    DB.db.withSession { implicit session =>
      tlocation.filter(_.id === location.id).update(location)
    }
  }

  def delete(location:Location): Int ={
    DB.db.withSession { implicit session =>
      tlocation.filter(_.id === location.id).delete
    }
  }

  def delete(id:Int): Int ={
    DB.db.withSession { implicit session =>
      tlocation.filter(_.id=== id).delete
    }
  }



}


object Locations{
  val typed = Actors.typed
  val location: LocationCRUDT= typed.typedActorOf(TypedProps[LocationCRUD]())
}
trait LocationCRUDT {

  val tlocation: TableQuery[Locations]

  def apply(name: Option[String]): Seq[Location]

  def apply(id:Int): Seq[Location]

  def apply(location: Location):MySQLDriver.InsertInvoker[Locations#TableElementType]#SingleInsertResult

  def apply(location: Seq[Location]):MySQLDriver.InsertInvoker[Locations#TableElementType]#MultiInsertResult

  def applyFrom(id:Int): Seq[Location]
  def applyByTime(ts:DateTime=new DateTime(0)): Seq[Location]

  def update(location: Location):Int

  def delete(location: Location):Int

  def delete(id: Int): Int

}

