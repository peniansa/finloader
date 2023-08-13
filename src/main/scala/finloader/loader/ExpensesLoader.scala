package finloader.loader

import scala.slick.jdbc.JdbcBackend.Database
import scala.slick.driver.JdbcDriver.simple._
import java.io.{File}
import java.net.URL
import com.github.tototoshi.csv.{CSVFormat, CSVReader}
import finloader.entities.{ExpenseTag, ExpenseTags, Expenses, Expense}
import scala.slick.lifted.TableQuery
import org.slf4j.LoggerFactory
import finloader.{FinloaderUtils, DbUtils}
import finloader.FinloaderUtils._


/**
 * @author Paul Lysak
 *         Date: 05.07.13
 *         Time: 21:55
 */
class ExpensesLoader(db: Database)(implicit csvFormat: CSVFormat) extends DataLoader {
  private implicit val dbImpl = db

  def load(source: URL, idPrefix: String = "") {
    log.info(s"Loading expenses from $source")
    log.debug(s"Using CSV separator ${csvFormat.delimiter}")
    clear(idPrefix)
    val reader = CSVReader.open(new File(source.toURI))
    var count = 0
    val expensesStream: Stream[(Expense, String)] =  (reader.toStream() match {
      case firstRow #:: body =>
        val p = firstRow.zipWithIndex.toMap

        for((row, rowIndex) <- body.zipWithIndex) yield {
          val r = row.toIndexedSeq
          val (amt, curr) = FinloaderUtils.parseAmount(r(p("amount")))
          count += 1
          (Expense(id = idPrefix + (rowIndex + 1),
            fileCode = idPrefix,
            date = parseDate(r(p("date"))),
            amount = amt,
            currency = curr,
            category = r(p("category")),
            comment = r(p("comment"))),
          r(p("tags")))
        }
      case _ =>
        log.error("can't find first line")
        Stream()
    })

    lazy val defaultedExpenses: Stream[(Expense, String)] = ((Expense(null, null, null, 0, null, null), "") #:: defaultedExpenses).zip(expensesStream).
          map({case ((prevExp, prevTags), (thisExp, thisTags)) =>
        val date = if(thisExp.date == null) prevExp.date else thisExp.date
      (thisExp.copy(date = date), thisTags)
    })

   defaultedExpenses.foreach(insert.tupled)

    log.info(s"Loaded $count expenses from $source")
  }

  private def clear(fileCode: String) {
    db.withSession {implicit session =>
      val expToClear = expQuery.filter(_.fileCode === fileCode)
      expenseTags.filter(t => expToClear.filter(_.id === t.expenseId).exists).delete
      expenseTags.filter(t => t.expenseId.startsWith(fileCode)).delete
      expToClear.delete
    }
  }


  private val insert = {(expense: Expense, tagsString: String) =>
    db.withSession {implicit session =>
      expQuery.insert(expense)
      updateTags(expense.id, expense.category, tagsString)
    }
  }

  private def updateTags(expenseId: String, category: String, tagsString: String)(implicit session: Session) {
//    expenseTags.where(_.expenseId === expenseId).delete
    val tags = tagsString.split(" |,").filter(_.nonEmpty) :+ category
    val ins = tags.toSet[String].map(t => (expenseId, t))
    expenseTags.map(et => (et.expenseId, et.tag)) ++= ins
  }

  private val expenseTags = TableQuery[ExpenseTags]
  private val expQuery = TableQuery[Expenses]
  private val log = LoggerFactory.getLogger(classOf[ExpensesLoader])
}
