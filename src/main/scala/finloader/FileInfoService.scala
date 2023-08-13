package finloader

import finloader.entities.{FileInfo, FileInfos}
import org.joda.time.LocalDateTime

import com.github.tototoshi.slick.JdbcJodaSupport._
import scala.slick.lifted.TableQuery
import scala.slick.jdbc.JdbcBackend.Database
import scala.slick.driver.JdbcDriver.simple._

/**
 * Created by gefox on 26.12.14.
 */
class FileInfoService(db: Database) extends DbUtils {
  def needsUpdate(fileCode: String, dateTime: LocalDateTime): Boolean = {
    val fiExists = db.withSession { implicit session =>
      erQuery.where(fi => fi.fileCode === fileCode && fi.updatedDateTime >= dateTime).exists.run
    }
    !fiExists
  }

  def setUpdatedDateTime(fileCode: String, dateTime: LocalDateTime): Unit = {
    db.withSession {implicit session =>
      val updCount = erQuery.where(_.fileCode === fileCode).map(_.updatedDateTime).update(dateTime)
      if(updCount == 0)
        erQuery.map(fi => (fi.fileCode, fi.updatedDateTime)).insert((fileCode, dateTime))
    }

  }

  private val erQuery = TableQuery[FileInfos]
}
