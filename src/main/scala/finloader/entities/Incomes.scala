package finloader.entities

import com.github.tototoshi.slick.JdbcJodaSupport._
import org.joda.time.LocalDate
import scala.slick.driver.JdbcDriver
import JdbcDriver._
import scala.slick.driver.JdbcDriver.Implicit._
import scala.slick.lifted.Tag

/**
 * @author Paul Lysak
 *         Date: 02.07.13
 *         Time: 23:05
 */
case class Income(id: String, fileCode: String, date: LocalDate, amount: Long, currency: String, source: String, comment: String = "")

class Incomes(tag: Tag) extends Table[Income](tag, "income") {
  def id = column[String]("id", O.PrimaryKey, O.DBType("VARCHAR(64)"))

  def fileCode = column[String]("fileCode", O.DBType("VARCHAR(128)"))

  def date = column[LocalDate]("date")

  def amount = column[Long]("amount")

  def currency = column[String]("currency", O.DBType("VARCHAR(8)"))

  def source = column[String]("source", O.DBType("VARCHAR(128)"))

  def comment = column[String]("comment", O.DBType("TEXT"))


  def * = (id, fileCode, date, amount, currency, source, comment) <> (Income.tupled, Income.unapply _)


  def dateIndex = index("income_date_index", date)
}
