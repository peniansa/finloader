package finloader.loader

import java.net.URL
import finloader.entities.{ExchangeRate, ExchangeRates}
import scala.slick.lifted.TableQuery
import scala.slick.jdbc.JdbcBackend.Database
import scala.slick.driver.JdbcDriver.simple._
import finloader.{FinloaderUtils, DbUtils}
import com.github.tototoshi.csv.{CSVReader, CSVFormat}
import org.slf4j.LoggerFactory
import java.io.File

import finloader.FinloaderUtils._

/**
 * @author Paul Lysak
 *         Date: 07.02.14
 *         Time: 22:34
 */
class ExchangeRatesLoader(db: Database)(implicit csvFormat: CSVFormat) extends DataLoader {
  private implicit val dbImpl = db

  def load(source: URL, idPrefix: String) = {
    log.info(s"Loading exchange rates from $source")
    log.debug(s"Using CSV separator ${csvFormat.delimiter}")
    clear(idPrefix)
    val reader = CSVReader.open(new File(source.toURI))
    var count = 0
    val ratesStream: Stream[ExchangeRate] =  (reader.toStream() match {
      case firstRow #:: body =>
        val p = firstRow.zipWithIndex.toMap

        for(row <- body) yield {
          val r = row.toIndexedSeq
          count += 1
          ExchangeRate(id = idPrefix+r(p("id")),
            idPrefix,
            date = parseDate(r(p("date"))),
            currency = r(p("currency")),
            rate = BigDecimal(r(p("rate"))),
            comment = r(p("comment")))
        }
      case _ =>
        log.error("can't find first line")
        Stream()
    })

    lazy val defaultedRates: Stream[ExchangeRate] = (ExchangeRate(null, null, null, null, 0, null) #:: defaultedRates).zip(ratesStream).
          map({case (prev, curr) =>
        val date = if(curr.date == null) prev.date else curr.date
        curr.copy(date = date)
    })

    insertAll(defaultedRates)

    log.info(s"Loaded $count exchange rates from $source")
  }

  private def insertAll(exRates: Seq[ExchangeRate]) = {
    db.withSession {implicit session =>
      erQuery.insertAll(exRates: _*)
    }
  }


  private def clear(fileCode: String) {
    db.withSession {implicit session =>
      erQuery.where(_.fileCode === fileCode).delete
    }
  }


  private val erQuery = TableQuery[ExchangeRates]

  private val log = LoggerFactory.getLogger(classOf[ExchangeRatesLoader])
}
