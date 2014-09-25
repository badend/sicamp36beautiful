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

object VersionCaseClassMapping extends App {

  // the base query for the Versions table
  val versionss = TableQuery[Versions]


  DB.db.withSession { implicit session =>

    // create the schema
    versionss.ddl.create

    // insert two Version instances

    // print the versionss (select * from USERS)
    println(versionss.list)
  }

}

case class Version( id: Int,
                     application:String,
                     updatedt:Option[Timestamp])

class Versions(tag: Tag) extends Table[Version](tag, "LOCATIONS")  {
  // Auto Increment the id primary key column
  def id = column[Int]("id", O.PrimaryKey, O.AutoInc)
  def application = column[String]("updatedt", O.Nullable, O.DBType("VARCHAR(200)"))
  def updatedt= column[Timestamp]("updatedt", O.Nullable, O.DBType("TIMESTAMP")
  )


  // the * projection (e.g. select * ...) auto-transforms the tupled
  // column values to / from a Version
  def * = (id, application, updatedt.?) <> (Version.tupled, Version.unapply)
}

class VersionCRUD extends VersionCRUDT with BadendTypedActorSupervisor{
  val tversion = TableQuery[Versions]


  def apply(application:Option[String]): Seq[Version] = {
    val versions: Seq[Version] =  DB.db.withSession { implicit session =>
      if(application.isEmpty) tversion.run
      else tversion.filter(x => x.application === application.get).run
    }

    println(versions.size)
    versions.foreach(x=>println(x))
    versions

  }

  def apply(id:Int): Seq[Version] = {
    val versions: Seq[Version] =  DB.db.withSession { implicit session =>
      if(id==0) tversion.run
      else tversion.filter(x => x.id === id).run
    }

    println(versions.size)
    versions.foreach(x=>println(x))
    versions

  }
  def apply(version:Version): MySQLDriver.InsertInvoker[Versions#TableElementType]#SingleInsertResult ={
    val r = DB.db.withSession { implicit session =>
      tversion += version
    }
    println(r)
    r
  }
  def apply(version:Seq[Version]): MySQLDriver.InsertInvoker[Versions#TableElementType]#MultiInsertResult ={
    val r = DB.db.withSession { implicit session =>
      tversion ++= version
    }
    println(r)
    r
  }
  def update(version:Version): Int ={
    DB.db.withSession { implicit session =>
      tversion.filter(_.id === version.id).update(version)
    }
  }

  def delete(version:Version): Int ={
    DB.db.withSession { implicit session =>
      tversion.filter(_.id === version.id).delete
    }
  }

  def delete(id:Int): Int ={
    DB.db.withSession { implicit session =>
      tversion.filter(_.id=== id).delete
    }
  }



}


object Versions{
  val typed = Actors.typed
  val version: VersionCRUDT= typed.typedActorOf(TypedProps[VersionCRUD]())
}
trait VersionCRUDT {

  val tversion: TableQuery[Versions]

  def apply(name: Option[String]): Seq[Version]

  def apply(version: Version):MySQLDriver.InsertInvoker[Versions#TableElementType]#SingleInsertResult

  def apply(version: Seq[Version]):MySQLDriver.InsertInvoker[Versions#TableElementType]#MultiInsertResult

  def update(version: Version):Int

  def delete(version: Version):Int

  def delete(id: Int): Int

}

