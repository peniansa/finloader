package finloader

import com.typesafe.config.ConfigFactory
import java.io.File
import scala.slick.lifted.TableQuery
import scala.slick.jdbc.JdbcBackend.Database
import finloader.entities._
import scala.slick.driver.JdbcDriver.simple._

/**
 * @author Paul Lysak
 *         Date: 04.07.13
 *         Time: 23:20
 */
object ITUtils {
  lazy val config = ConfigFactory.parseFile(new File("it.conf"))

  lazy val db = {
    val d = Database.forURL(config.getString("database.testUrl"), driver = config.getString("database.driver"))
    createSchema(d)
    d
  }

  private def createSchema(d: Database) {
    println("creating DB schema...")
    d withSession {
      implicit session =>
      TableQuery[Expenses].ddl.create
      TableQuery[Balances].ddl.create
      TableQuery[Incomes].ddl.create
      TableQuery[ExchangeRates].ddl.create
//      println("ddl="+TableQuery[ExpenseTags].ddl.createStatements.mkString("\n"))
      TableQuery[ExpenseTags].ddl.create
      TableQuery[FileInfos].ddl.create
    }
    println("DB schema created")
  }
}
