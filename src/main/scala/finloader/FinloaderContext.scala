package finloader

import scala.slick.jdbc.JdbcBackend.Database
import com.typesafe.config.ConfigFactory
import java.io.File
import scala.collection.JavaConversions._
import com.github.tototoshi.csv.DefaultCSVFormat
import org.slf4j.LoggerFactory
import finloader.loader.{ExchangeRatesLoader, IncomesLoader, BalancesLoader, ExpensesLoader}

/**
 * @author Paul Lysak
 *         Date: 01.08.13
 *         Time: 23:24
 */
class FinloaderContext(configFile: File) {
  private val fallbackConfig = ConfigFactory.parseMap(Map("csv.separator" -> ","))
  val config = ConfigFactory.parseFile(configFile).withFallback(fallbackConfig)
  val db = Database.forURL(config.getString("database.url"), driver = config.getString("database.driver"))

  implicit private val csvFormat = new DefaultCSVFormat {
    override val delimiter = config.getString("csv.separator").head
  }


  val dbService = new DbService()(db)

  val fileInfoService = new FileInfoService(db)

  private val loaderScopes = Seq(
    LoaderScope(new SourceLocator("exp_"), new ExpensesLoader(db)),
    LoaderScope(new SourceLocator("chk_"), new BalancesLoader(db)),
    LoaderScope(new SourceLocator("inc_"), new IncomesLoader(db)),
    LoaderScope(new SourceLocator("rate_"), new ExchangeRatesLoader(db))
  )
  val finloaderService = new FinloaderService(loaderScopes, fileInfoService)

  private val log = LoggerFactory.getLogger(classOf[FinloaderContext])
}
