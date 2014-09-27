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

object CommentCaseClassMapping extends App {

  // the base query for the Comments table
  val Commentss = TableQuery[Comments]



  DB.db.withSession { implicit session  =>
    //Commentss.ddl.drop
    // create the schema
    Commentss.ddl.create

    // insert two Comment instances

    // print the Commentss (select * from USERS)
    println(Commentss.list)
  }

}

case class Comment( id: Option[Int],
user_id:String,
editor:String,
content:Option[String],
updatedt:DateTime,
location_id:Int,
score:Option[Int])

class Comments(tag: Tag) extends Table[Comment](tag, "COMMENTS")  {


  // Auto Increment the id primary key column
  def id = column[Int]("id", O.PrimaryKey, O.AutoInc)
  def user_id = column[String]("user_id", O.Nullable, O.DBType("VARCHAR(1000)"))
  def editor = column[String]("editor", O.Nullable, O.DBType("VARCHAR(200)"))
  def content = column[String]("content", O.Nullable, O.DBType("TEXT"))
  def updatedt = column[DateTime]("updatedt", O.Nullable, O.DBType("TIMESTAMP"), O.Default(DateTime.now))
  def location_id = column[Int]("location_id", O.DBType("INT"))
  def score = column[Int]("score", O.Nullable, O.DBType("INT"))
  def idx = index("idx_updatedt", (updatedt))

  // the * projection (e.g. select * ...) auto-transforms the tupled
  // column values to / from a Comment
  def * = (id.?, user_id, editor, content.?, updatedt, location_id, score.?) <> (Comment.tupled, Comment.unapply)
}

class CommentCRUD extends CommentCRUDT with BadendTypedActorSupervisor{
  val tComment = TableQuery[Comments]



  def apply(id:Int): Seq[Comment] = {
    val comment: Seq[Comment] =  DB.db.withSession { implicit session =>
      if(id==0) tComment.run
      else tComment.filter(x => x.id === id).run
    }

    println(comment.size)
    comment.foreach(x=>println(x))
    comment
  }

  def applyFrom(id:Int): Seq[Comment] = {
    val comment: Seq[Comment] =  DB.db.withSession { implicit session =>
      tComment.filter(x => x.id >= id).run
    }

    println(comment.size)
    comment.foreach(x=>println(x))
    comment
  }


  def applyByLocationId(id:Int): Seq[Comment] = {
   DB.db.withSession { implicit session =>
      tComment.filter(x=>x.location_id === id).run
    }
  }

  def applyByTime(ts:DateTime=new DateTime(0)): Seq[Comment] = {
    val comment: Seq[Comment] =  DB.db.withSession { implicit session =>
      tComment.filter(x => x.updatedt >= ts).sortBy(_.updatedt.asc).run
    }

    println(comment.size)
    comment.foreach(x=>println(x))
    comment
  }

  def apply(comment:Comment): MySQLDriver.InsertInvoker[Comments#TableElementType]#SingleInsertResult ={
    val r = DB.db.withSession { implicit session =>
      
      tComment += comment
    }
    println(r)
    r
  }
  def apply(comment:Seq[Comment]): MySQLDriver.InsertInvoker[Comments#TableElementType]#MultiInsertResult ={
    val r = DB.db.withSession { implicit session =>
      tComment ++= comment
    }
    println(r)
    r
  }
  def update(comment:Comment): Int ={
    DB.db.withSession { implicit session =>
      println(comment)
      tComment.filter(_.id === comment.id).update(comment)
    }
  }

  def delete(comment:Comment): Int ={
    DB.db.withSession { implicit session =>
      tComment.filter(_.id === comment.id).delete
    }
  }

  def delete(id:Int): Int ={
    DB.db.withSession { implicit session =>
      tComment.filter(_.id=== id).delete
    }
  }



}


object Comments{
  val typed = Actors.typed
  val comment: CommentCRUDT= typed.typedActorOf(TypedProps[CommentCRUD]())
}
trait CommentCRUDT {

  val tComment: TableQuery[Comments]

  def apply(id:Int): Seq[Comment]

  def applyByLocationId(id:Int): Seq[Comment]
  def apply(Comment: Comment):MySQLDriver.InsertInvoker[Comments#TableElementType]#SingleInsertResult

  def apply(Comment: Seq[Comment]):MySQLDriver.InsertInvoker[Comments#TableElementType]#MultiInsertResult

  def applyFrom(id:Int): Seq[Comment]
  def applyByTime(ts:DateTime=new DateTime(0)): Seq[Comment]

  def update(Comment: Comment):Int

  def delete(Comment: Comment):Int

  def delete(id: Int): Int

}
