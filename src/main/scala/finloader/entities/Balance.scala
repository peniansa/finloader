package finloader.entities

import org.joda.time.LocalDate
import com.github.tototoshi.slick.JdbcJodaSupport._
import scala.slick.driver.JdbcDriver
import JdbcDriver._
import scala.slick.driver.JdbcDriver.Implicit._
import scala.slick.lifted.Tag

/**
 * @author Paul Lysak
 *         Date: 15.08.13
 *         Time: 21:47
 */

case class Balance(id: String, fileCode: String, snapshotId: String, date: LocalDate, place: String, amount: Long, currency: String, comment: String = "")

class Balances(tag: Tag) extends Table[Balance](tag, "balance") {
  def id = column[String]("id", O.PrimaryKey, O.DBType("VARCHAR(64)"))

  def fileCode = column[String]("fileCode", O.DBType("VARCHAR(128)"))

  def snapshotId = column[String]("snapshot_id", O.DBType("VARCHAR(64)"))

  def date = column[LocalDate]("date")

  def place = column[String]("place", O.DBType("VARCHAR(128)"))

  def amount = column[Long]("amount")

  def currency = column[String]("currency", O.DBType("VARCHAR(8)"))

  def comment = column[String]("comment", O.DBType("TEXT"))

  def * = (id, fileCode, snapshotId, date, place, amount, currency, comment) <> (Balance.tupled, Balance.unapply _)


  def snapshotIndex = index("balance_snapshot_id_index", snapshotId)

  def dateIndex = index("balance_date_index", date)
}