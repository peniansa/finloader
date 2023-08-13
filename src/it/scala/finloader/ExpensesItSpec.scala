package finloader

import org.specs2.mutable.Specification
import ITUtils.db
import scala.slick.ast.{TableExpansion, TableNode}
import scala.slick.jdbc.JdbcBackend.Database
import scala.slick.driver.JdbcDriver.simple._
import scala.slick.lifted.TableQuery
import org.joda.time.LocalDate
import com.github.tototoshi.csv.DefaultCSVFormat
import finloader.loader.ExpensesLoader
import finloader.entities.{ExpenseTags, Expenses, Expense}

/**
 * @author Paul Lysak
 *         Date: 02.07.13
 *         Time: 23:37
 */
class ExpensesItSpec extends Specification {
  sequential

  "ExpensesLoader" should {
    "load expenses" in {
      cleanExpenses


      loadFile("/exp_201306.csv", ',', sampleExpenses)

      db.withSession { implicit session =>
          val t = TableQuery[ExpenseTags].filter(_.expenseId === "pref_1").list.toSet

          val actualTags = TableQuery[ExpenseTags].where(_.expenseId === "pref_1").map(_.tag).list.toSet
          actualTags must be equalTo(Set("food", "sm1", "drink", "eat"))
      }
   }

    "merge expenses" in {
      loadFile("/exp_201306_.csv", ';', mergedExpenses)
    }

    "substitute skipped date" in {
      cleanExpenses
      loadFile("/exp_201306_sk.csv", ',', skippedDateExpenses)
    }

    "recognize currency" in {
      cleanExpenses

      loadFile("/exp_curr.csv", ',', sampleExpensesCurr)
    }

    "autogenerate IDs" in {
      cleanExpenses

      loadFile("/exp_201306_noid.csv", ',', sampleExpenses)
    }

  }

  private def cleanExpenses {
      db.withSession { implicit session =>
          TableQuery[ExpenseTags].delete

          val query = TableQuery[Expenses]
          query.delete
          val exp = query.list.toSet
          exp must be equalTo(Set())
      }
  }

  private def loadFile(path: String, separator: Char, expectedContent: Set[Expense]) = {
      val url1 = getClass.getResource(path)
      loader(separator).load(url1, "pref_")
      db.withSession { implicit session =>
        val actualExpenses = TableQuery[Expenses].list.toSet
        actualExpenses must be equalTo(expectedContent)
      }
  }

  private val sampleExpenses = Set(
    Expense(id = "pref_1", fileCode = "pref_", date = new LocalDate(2013, 6, 10), amount = 10000, currency = "UAH", category = "food", comment = "supermarket"),
    Expense(id = "pref_2", fileCode = "pref_", date = new LocalDate(2013, 6, 11), amount = 35050, currency = "UAH", category = "household"),
    Expense(id = "pref_3", fileCode = "pref_", date = new LocalDate(2013, 6, 12), amount = 32000, currency = "UAH", category = "car_fuel", comment = "30L")
  )

  private val mergedExpenses = Set(
    Expense(id = "pref_1", fileCode = "pref_", date = new LocalDate(2013, 6, 10), amount = 10000, currency = "UAH", category = "food", comment = "supermarket"),
    Expense(id = "pref_2", fileCode = "pref_", date = new LocalDate(2013, 6, 15), amount = 55050, currency = "UAH",  category = "household", comment = "repair something"),
    Expense(id = "pref_3", fileCode = "pref_", date = new LocalDate(2013, 6, 12), amount = 32000, currency = "UAH",  category = "car_fuel", comment = "30L"),
    Expense(id = "pref_4", fileCode = "pref_", date = new LocalDate(2013, 6, 13), amount = 22000, currency = "UAH",  category = "food", comment = "fruits")
  )

  private val skippedDateExpenses = Set(
    Expense(id = "pref_1", fileCode = "pref_", date = new LocalDate(2013, 6, 10), amount = 10000, currency = "UAH",  category = "food", comment = "supermarket"),
    Expense(id = "pref_2", fileCode = "pref_", date = new LocalDate(2013, 6, 10), amount = 35050, currency = "UAH",  category = "household"),
    Expense(id = "pref_3", fileCode = "pref_", date = new LocalDate(2013, 6, 10), amount = 7000, currency = "UAH",  category = "transport", comment = "taxi"),
    Expense(id = "pref_4", fileCode = "pref_", date = new LocalDate(2013, 6, 12), amount = 32000, currency = "UAH",  category = "car_fuel", comment = "30L")
  )

  private val sampleExpensesCurr = Set(
    Expense(id = "pref_1", fileCode = "pref_", date = new LocalDate(2013, 6, 10), amount = 10000, currency = "UAH", category = "food", comment = "supermarket"),
    Expense(id = "pref_2", fileCode = "pref_", date = new LocalDate(2013, 6, 11), amount = 3550, currency = "USD", category = "household"),
    Expense(id = "pref_3", fileCode = "pref_", date = new LocalDate(2013, 6, 12), amount = 3200, currency = "EUR", category = "car_fuel", comment = "30L")
  )


  private def loader(sep: Char) = new ExpensesLoader(db)(new DefaultCSVFormat {override val delimiter = sep})
}
