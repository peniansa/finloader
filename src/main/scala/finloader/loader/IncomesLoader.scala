package finloader.loader

import scala.slick.jdbc.JdbcBackend.Database
import scala.slick.driver.JdbcDriver.simple._
import java.io.File
import java.net.URL
import com.github.tototoshi.csv.{CSVFormat, CSVReader}
import finloader.entities.{Income, Incomes}
import scala.slick.lifted.TableQuery
import org.slf4j.LoggerFactory
import finloader.{DbUtils, FinloaderUtils}
import FinloaderUtils._


/**
  * @author Paul Lysak
  *         Date: 05.07.13
  *         Time: 21:55
  */
class IncomesLoader(db: Database)(implicit csvFormat: CSVFormat) extends DataLoader {
   private implicit val dbImpl = db

   def load(source: URL, idPrefix: String = "") {
     log.info(s"Loading incomes from $source")
     log.debug(s"Using CSV separator ${csvFormat.delimiter}")
     clear(idPrefix)
     val reader = CSVReader.open(new File(source.toURI))
     var count = 0
     val incomes = reader.toStream() match {
       case firstRow #:: body =>
         val p = firstRow.zipWithIndex.toMap
         for((row, rowIndex) <- body.zipWithIndex) yield {
           val r = row.toIndexedSeq
           val (amt, curr) = parseAmount(r(p("amount")))
           count += 1
           Income(id = idPrefix + (rowIndex + 1),
             fileCode = idPrefix,
             date = parseDate(r(p("date"))),
             amount = amt,
             currency = curr,
             source = r(p("source")),
             comment = r(p("comment")))
         }
       case _ =>
         log.error("can't find first line")
         Stream()
     }

     lazy val defaultedIncomes: Stream[Income] = (Income(null, null, null, 0, null, null, null) #:: defaultedIncomes).zip(incomes).
            map({case (prev, curr) =>
          val date = if(curr.date == null) prev.date else curr.date
          curr.copy(date = date)
     })


     insertAll(defaultedIncomes)

     log.info(s"Loaded $count incomes from $source")
   }

   private def clear(fileCode: String) {
    db.withSession {implicit session =>
      incQuery.where(_.fileCode === fileCode).delete
    }
   }

   private def insertAll(incomes: Iterable[Income]) {
     db.withSession {implicit session =>
       incQuery.insertAll(incomes.toSeq: _*)
     }
   }

   private val incQuery = TableQuery[Incomes]
   private val log = LoggerFactory.getLogger(classOf[IncomesLoader])
 }
