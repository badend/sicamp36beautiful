package repository

import utils.BeautifulConfig

import scala.slick.driver.MySQLDriver
import scala.slick.driver.MySQLDriver.simple._

/**
 * Created by badend on 9/16/14.
 */
object DB {

  val db: MySQLDriver.backend.DatabaseDef = Database.forURL(
    url=BeautifulConfig.db_default_url,
    user=BeautifulConfig.db_default_user,
    password=BeautifulConfig.db_default_password,
    driver = BeautifulConfig.db_default_driver)

}
