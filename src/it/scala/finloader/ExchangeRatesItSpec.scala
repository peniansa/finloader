package finloader

import org.specs2.mutable.Specification
import scala.slick.jdbc.JdbcBackend.Database
import scala.slick.driver.JdbcDriver.simple._
import scala.slick.lifted.TableQuery
import ITUtils.db
import com.github.tototoshi.csv.DefaultCSVFormat
import finloader.loader.ExchangeRatesLoader
import finloader.entities.{ExchangeRate, ExchangeRates}
import org.joda.time.LocalDate
/**
 * @author Paul Lysak
 *         Date: 07.02.14
 *         Time: 22:38
 */
class ExchangeRatesItSpec extends Specification {

  "ExchangeRatesLoader" should {
    "load rates" in {
      val url1 = getClass.getResource("/rate_2014.csv")
      loader(',').load(url1, "pref_")
      db.withSession {implicit session =>
        val actualBalances = TableQuery[ExchangeRates].list.toSet
        actualBalances must be equalTo(sampleRates)
      }
   }
  }

  private val sampleRates = Set(
    ExchangeRate(id="pref_1", fileCode = "pref_", date = new LocalDate(2014, 1, 20), currency = "USD", rate = BigDecimal("8.50"), comment = "growing"),
    ExchangeRate(id="pref_2", fileCode = "pref_", date = new LocalDate(2014, 1, 20), currency = "EUR", rate = BigDecimal("11.30"))
  )

  private def loader(sep: Char) = new ExchangeRatesLoader(db)(new DefaultCSVFormat {override val delimiter = sep})

}

