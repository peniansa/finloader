package finloader.loader

import com.github.tototoshi.csv.{CSVReader, CSVFormat}
import java.net.URL
import org.slf4j.LoggerFactory
import finloader.entities.{Balance, Balances}
import scala.slick.jdbc.JdbcBackend.Database
import scala.slick.driver.JdbcDriver.simple._
import scala.slick.lifted.TableQuery
import java.io.File
import finloader.{DbUtils, FinloaderUtils}
import finloader.FinloaderUtils._

/**
 * @author Paul Lysak
 *         Date: 15.08.13
 *         Time: 21:43
 */
class BalancesLoader(db: Database)(implicit csvFormat: CSVFormat) extends DataLoader {
  private implicit val dbImpl = db

  def load(source: URL, idPrefix: String) {
    log.info(s"Loading balances from $source")
    log.debug(s"Using CSV separator ${csvFormat.delimiter}")
    clear(idPrefix)
    val reader = CSVReader.open(new File(source.toURI))
    var count = 0
    val balances = reader.toStream() match {
      case firstRow #:: body =>
        val p = firstRow.zipWithIndex.toMap
        for(row <- body) yield {
          val r = row.toIndexedSeq
          val (amt, curr) = FinloaderUtils.parseAmount(r(p("amount")))
          count += 1
          Balance(id = idPrefix+count,
            idPrefix,
            snapshotId = idPrefix+r(p("snapshotId")),
            date = parseDate(r(p("date"))),
            place = r(p("place")),
            amount = amt,
            currency = curr,
            comment = r(p("comment")))
        }
      case _ =>
        log.error("can't find first line")
        Stream()
    }

    lazy val defaultedBalances: Stream[Balance] = (Balance(null, null, null, null, null, 0, null) #:: defaultedBalances).zip(balances).
      map({case (prev, current) =>
        val snapshotId = if(current.snapshotId == idPrefix) prev.snapshotId else current.snapshotId
        val date = if(current.date == null) prev.date else current.date
        current.copy(snapshotId = snapshotId, date = date)
    })

    insertAll(defaultedBalances)

    log.info(s"Loaded $count balances from $source")
  }

  private def insertAll(balanceItems: Seq[Balance]) = {
    db.withSession {implicit session =>
      balQuery.insertAll(balanceItems: _*)
    }
  }

  private def clear(fileCode: String) {
    db.withSession {implicit session =>
      balQuery.where(_.fileCode === fileCode).delete
    }
  }

  private val balQuery = TableQuery[Balances]
  private val log = LoggerFactory.getLogger(classOf[BalancesLoader])
}
