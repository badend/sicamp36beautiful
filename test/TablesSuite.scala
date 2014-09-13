import org.scalatest._
import repository.{Coffees, Suppliers}

import utils.BeautifulConfig
import scala.slick.driver.MySQLDriver.simple._
import scala.slick.jdbc.meta._


class TablesSuite extends FunSuite with BeforeAndAfter {

  val suppliers = TableQuery[Suppliers]
  val coffees = TableQuery[Coffees]

  before {
    session = Database.forURL(
      url=BeautifulConfig.db_default_url,
      user=BeautifulConfig.db_default_user,
      password=BeautifulConfig.db_default_password,
      driver = BeautifulConfig.db_default_driver).createSession()
  }
  implicit var session: Session = _

  def createSchema() = (suppliers.ddl ++ coffees.ddl).create
  
  def insertSupplier(): Int = suppliers += (101, "Acme, Inc.", "99 Market Street", "Groundsville", "CA", "95199")
  

  test("Creating the Schema works") {
    createSchema()
    
    val tables = MTable.getTables.list

    assert(tables.count(_.name.name.equalsIgnoreCase("suppliers")) == 1)
    assert(tables.count(_.name.name.equalsIgnoreCase("coffees")) == 1)
  }

  test("Inserting a Supplier works") {
    createSchema()
    
    val insertCount = insertSupplier()
    assert(insertCount == 1)
  }
  
  test("Query Suppliers works") {
    createSchema()
    insertSupplier()
    val results = suppliers.list
    assert(results.size == 1)
    assert(results.head._1 == 101)
  }
  
  after {
    session.close()
  }

}