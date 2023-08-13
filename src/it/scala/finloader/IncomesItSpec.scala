package finloader

import org.specs2.mutable.Specification
import ITUtils.db
import scala.slick.jdbc.JdbcBackend.Database
import scala.slick.driver.JdbcDriver.simple._
import org.joda.time.LocalDate
import com.github.tototoshi.csv.DefaultCSVFormat
import finloader.loader.{IncomesLoader, ExpensesLoader}
import finloader.entities.{Income, Incomes, Expenses, Expense}
import scala.slick.lifted.TableQuery

/**
 * @author Paul Lysak
 *         Date: 02.07.13
 *         Time: 23:37
 */
class IncomesItSpec extends Specification {

  "IncomesLoader" should {
    "load incomes" in {
      val url1 = getClass.getResource("/inc_2013.csv")
      loader(',').load(url1, "pref_")
      db.withSession {implicit session =>
        val actualIncomes = TableQuery[Incomes].list.toSet
        actualIncomes must be equalTo(sampleIncomes)
      }
   }
  }


  private val sampleIncomes = Set(
    Income(id = "pref_1", fileCode = "pref_", date = new LocalDate(2013, 6, 10), amount = 1000000, currency = "UAH", source = "job", comment = "for may 2013"),
    Income(id = "pref_2", fileCode = "pref_", date = new LocalDate(2013, 6, 12), amount = 20000, currency = "UAH", source = "sell", comment = "old furniture"),
    Income(id = "pref_3", fileCode = "pref_", date = new LocalDate(2013, 6, 12), amount = 50000, currency = "UAH", source = "sell", comment = "old computer")
  )

  private def loader(sep: Char) = new IncomesLoader(db)(new DefaultCSVFormat {override val delimiter = sep})
}
