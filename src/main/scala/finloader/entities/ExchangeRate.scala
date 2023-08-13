package finloader.entities

import org.joda.time.LocalDate
import scala.slick.lifted.Tag
import com.github.tototoshi.slick.JdbcJodaSupport._
import scala.slick.driver.JdbcDriver
import JdbcDriver._
import scala.slick.driver.JdbcDriver.Implicit._
/**
 * @author Paul Lysak
 *         Date: 07.02.14
 *         Time: 22:17
 */
case class ExchangeRate(id: String, fileCode: String, date: LocalDate, currency: String, rate: BigDecimal, comment: String = "")

class ExchangeRates (tag: Tag) extends Table[ExchangeRate](tag, "rate") {
  def id = column[String]("id", O.PrimaryKey, O.DBType("VARCHAR(64)"))

  def fileCode = column[String]("fileCode", O.DBType("VARCHAR(128)"))

  def date = column[LocalDate]("date")

  def currency = column[String]("currency", O.DBType("VARCHAR(8)"))

  def rate = column[BigDecimal]("rate")

  def comment = column[String]("comment", O.DBType("TEXT"))


  def * = (id, fileCode, date, currency, rate, comment) <> (ExchangeRate.tupled, ExchangeRate.unapply _)


  def dateIndex = index("exrate_date_index", date)
}
