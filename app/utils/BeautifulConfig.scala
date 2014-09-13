package utils

/**
 * Created by badend on 9/12/14.
 */
object BeautifulConfig {
  import com.typesafe.config.ConfigFactory

  /*

db.default.url="mysql://wheeltrip.net:3306/beautiful?characterEncoding=UTF-8"
db.default.driver=com.mysql.jdbc.Driver

db.default.user=beautiful
db.default.password=beautiful_2014

   */
  val conf = ConfigFactory.load()
  val db_default_url = conf.getString("db.default.url")
  val db_default_driver = conf.getString("db.default.driver")
  val db_default_user = conf.getString("db.default.user")
  val db_default_password = conf.getString("db.default.password")


}
