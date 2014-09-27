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
import scala.slick.lifted.{CanBeQueryCondition, Column, TableQuery}
import com.github.tototoshi.slick._
import com.github.tototoshi.slick.JdbcJodaSupport._

object AskCaseClassMapping extends App {

  // the base query for the Asks table
  val Askss = TableQuery[Asks]



  DB.db.withSession { implicit session  =>
    //Askss.ddl.drop
    // create the schema
    Askss.ddl.create

    // insert two Ask instances

    // print the Askss (select * from USERS)
    println(Askss.list)
  }

}

case class Ask( id: Option[Int],
                    editor:Option[String],
                    email:Option[String],
                    content:Option[String],
                    updatedt:DateTime,
                    location_id:Option[Int])

class Asks(tag: Tag) extends Table[Ask](tag, "ASKS")  {


  // Auto Increment the id primary key column
  def id = column[Int]("id", O.PrimaryKey, O.AutoInc)
  def editor = column[String]("editor", O.Nullable, O.DBType("VARCHAR(200)"))
  def email = column[String]("email", O.Nullable, O.DBType("VARCHAR(200)"))
  def content = column[String]("content", O.Nullable, O.DBType("TEXT"))
  def updatedt = column[DateTime]("updatedt", O.Nullable, O.DBType("TIMESTAMP"), O.Default(DateTime.now))
  def location_id = column[Int]("location_id", O.DBType("INT"))
  def idx = index("idx_updatedt", (updatedt))

  // the * projection (e.g. select * ...) auto-transforms the tupled
  // column values to / from a Ask
  def * = (id.?, editor.?, email.?, content.?, updatedt, location_id.?) <> (Ask.tupled, Ask.unapply)
}

class AskCRUD extends AskCRUDT with BadendTypedActorSupervisor{
  val tAsk = TableQuery[Asks]



  def apply(id:Int): Seq[Ask] = {
    val ask: Seq[Ask] =  DB.db.withSession { implicit session =>
      if(id==0) tAsk.run
      else tAsk.filter(x => x.id === id).run
    }

    println(ask.size)
    ask.foreach(x=>println(x))
    ask
  }

  def applyFrom(id:Int): Seq[Ask] = {
    val ask: Seq[Ask] =  DB.db.withSession { implicit session =>
      tAsk.filter(x => x.id >= id).run
    }

    println(ask.size)
    ask.foreach(x=>println(x))
    ask
  }


  def applyByLocationId(id:Int): Seq[Ask] = {
    DB.db.withSession { implicit session =>
      tAsk.filter(x=>x.location_id === id).run
    }
  }

  def applyByTime(ts:DateTime=new DateTime(0)): Seq[Ask] = {
    val ask: Seq[Ask] =  DB.db.withSession { implicit session =>
      tAsk.filter(x => x.updatedt >= ts).sortBy(_.updatedt.asc).run
    }

    println(ask.size)
    ask.foreach(x=>println(x))
    ask
  }

  def apply(ask:Ask): MySQLDriver.InsertInvoker[Asks#TableElementType]#SingleInsertResult ={
    val r = DB.db.withSession { implicit session =>

      tAsk += ask
    }
    println(r)
    r
  }
  def apply(ask:Seq[Ask]): MySQLDriver.InsertInvoker[Asks#TableElementType]#MultiInsertResult ={
    val r = DB.db.withSession { implicit session =>
      tAsk ++= ask
    }
    println(r)
    r
  }
  def update(ask:Ask): Int ={
    DB.db.withSession { implicit session =>
      println(ask)
      tAsk.filter(_.id === ask.id).update(ask)
    }
  }

  def delete(ask:Ask): Int ={
    DB.db.withSession { implicit session =>
      tAsk.filter(_.id === ask.id).delete
    }
  }

  def delete(id:Int): Int ={
    DB.db.withSession { implicit session =>
      tAsk.filter(_.id=== id).delete
    }
  }



}


object Asks{
  val typed = Actors.typed
  val ask: AskCRUDT= typed.typedActorOf(TypedProps[AskCRUD]())
}
trait AskCRUDT {

  val tAsk: TableQuery[Asks]

  def apply(id:Int): Seq[Ask]

  def applyByLocationId(id:Int): Seq[Ask]
  def apply(Ask: Ask):MySQLDriver.InsertInvoker[Asks#TableElementType]#SingleInsertResult

  def apply(Ask: Seq[Ask]):MySQLDriver.InsertInvoker[Asks#TableElementType]#MultiInsertResult

  def applyFrom(id:Int): Seq[Ask]
  def applyByTime(ts:DateTime=new DateTime(0)): Seq[Ask]

  def update(Ask: Ask):Int

  def delete(Ask: Ask):Int

  def delete(id: Int): Int

}
