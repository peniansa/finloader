package finloader

import org.specs2.mutable.Specification
import ITUtils.db
import scala.slick.jdbc.JdbcBackend.Database
import scala.slick.driver.JdbcDriver.simple._
import scala.slick.lifted.TableQuery
import org.joda.time.LocalDate
import com.github.tototoshi.csv.DefaultCSVFormat
import finloader.loader.BalancesLoader
import finloader.entities.{Balance, Balances, Expenses, Expense}
import org.junit.runner.RunWith

/**
 * @author Paul Lysak
 *         Date: 02.07.13
 *         Time: 23:37
 */
class BalancesItSpec extends Specification {

  "BalancesLoader" should {
    "load balances" in {
      val url1 = getClass.getResource("/chk_2013.csv")
      loader(',').load(url1, "pref_")
      db.withSession {implicit session =>
        val actualBalances = TableQuery[Balances].list.toSet
        actualBalances must be equalTo(sampleBalances)
      }
   }
  }


  private val sampleBalances = Set(
    Balance(id = "pref_1", fileCode = "pref_", snapshotId = "pref_1", date = new LocalDate(2013, 5, 10), place = "cash", amount = 20000, currency = "USD", comment = "com1"),
    Balance(id = "pref_2", fileCode = "pref_", snapshotId = "pref_1", date = new LocalDate(2013, 5, 10), place = "cash", amount = 50000, currency = "UAH", comment = ""),
    Balance(id = "pref_3", fileCode = "pref_", snapshotId = "pref_2", date = new LocalDate(2013, 6, 10), place = "cash", amount = 15000, currency = "USD", comment = ""),
    Balance(id = "pref_4", fileCode = "pref_", snapshotId = "pref_2", date = new LocalDate(2013, 6, 10), place = "cash", amount = 70000, currency = "UAH", comment = "com2"),
    Balance(id = "pref_5", fileCode = "pref_", snapshotId = "pref_2", date = new LocalDate(2013, 6, 10), place = "card", amount = 150000, currency = "UAH", comment = "")
  )

  private def loader(sep: Char) = new BalancesLoader(db)(new DefaultCSVFormat {override val delimiter = sep})
}
