package finloader

import java.net.URL
import java.io.File
import java.util.Date
import org.joda.time.LocalDateTime
import org.slf4j.LoggerFactory
import finloader.loader.{ExchangeRatesLoader, IncomesLoader, BalancesLoader, ExpensesLoader}

/**
 * @author Paul Lysak
 *         Date: 01.08.13
 *         Time: 23:20
 */
class FinloaderService(scopes: Seq[LoaderScope], fileInfoService: FileInfoService) {

  def loadData(folderUrl: URL) {
    log.info(s"Loading data from $folderUrl...")
    try {
      scopes.foreach({case LoaderScope(locator, loader) =>
        val files = locator.locate(folderUrl)
        files.map(file => (file, idPrefix(file))).
        filter({case (file, code) => fileInfoService.needsUpdate(code, fileUpdDate(file))}).
        foreach({case (file, code) =>
          loader.load(file.toURI.toURL, code)
          fileInfoService.setUpdatedDateTime(code, fileUpdDate(file))
        })
      })
    } catch {
      //unfortunately, this kind of exception doesn't display its true reason unless explicitly asked by getNextException
      case e: java.sql.BatchUpdateException =>
        log.error("Batch update failed", e)
        throw e.getNextException;
    }

    log.info(s"Finished loading data from $folderUrl")
  }

  private def fileUpdDate(file: File) = LocalDateTime.fromDateFields(new Date(file.lastModified()))

  private def idPrefix(file: File) = {
      file.getName.toLowerCase.stripSuffix(".csv") + "_"
  }

  private val log = LoggerFactory.getLogger(classOf[FinloaderService])
}
