package repository

import utils.BeautifulConfig

import scala.slick.driver.MySQLDriver
import scala.slick.driver.MySQLDriver.simple._
import scala.slick.jdbc.meta.MTable

object CaseClassMapping extends App {

  // the base query for the Users table
  val users = TableQuery[Users]

  val db: MySQLDriver.backend.DatabaseDef = Database.forURL(
    url=BeautifulConfig.db_default_url,
    user=BeautifulConfig.db_default_user,
    password=BeautifulConfig.db_default_password,
    driver = BeautifulConfig.db_default_driver)
  db.withSession { implicit session =>

    // create the schema
    users.ddl.create

    // insert two User instances
    users += User("John Doe")
    users += User("Fred Smith")

    // print the users (select * from USERS)
    println(users.list)
  }

}

case class User(name: String, id: Option[Int] = None)

class Users(tag: Tag) extends Table[User](tag, "USERS") {
  // Auto Increment the id primary key column
  def id = column[Int]("ID", O.PrimaryKey, O.AutoInc)
  // The name can't be null
  def name = column[String]("NAME", O.NotNull)
  // the * projection (e.g. select * ...) auto-transforms the tupled
  // column values to / from a User
  def * = (name, id.?) <> (User.tupled, User.unapply)
}

object Users{
  val users = TableQuery[Users]


  val db = Database.forURL(
      url=BeautifulConfig.db_default_url,
      user=BeautifulConfig.db_default_user,
      password=BeautifulConfig.db_default_password,
      driver = BeautifulConfig.db_default_driver)


  def apply(id:Int)={
    db.withSession { implicit session =>
      users.filter(x=>x.id === id).run
     }.map(x=>User.apply _)


  }





  //def insertSupplier(): Int = suppliers += (101, "Acme, Inc.", "99 Market Street", "Groundsville", "CA", "95199")




}