package finloader.entities

import com.github.tototoshi.slick.JdbcJodaSupport._
import org.joda.time.LocalDateTime
import scala.slick.driver.JdbcDriver
import JdbcDriver._
import scala.slick.driver.JdbcDriver.Implicit._
import scala.slick.lifted.{TableQuery, Tag}


/**
 * Created by gefox on 26.12.14.
 */
case class FileInfo(id: Option[Long], fileCode: String, updatedDateTime: LocalDateTime)

class FileInfos(t: Tag) extends Table[FileInfo](t, "file_infos") {
  def id = column[Long]("id", O.PrimaryKey, O.DBType("SERIAL"))//O.AutoInc doesnt' work directly

  def fileCode = column[String]("file_code", O.DBType("VARCHAR(128)"))

  def updatedDateTime = column[LocalDateTime]("upd_date_time")


  def * = (id.?, fileCode, updatedDateTime) <> (FileInfo.tupled, FileInfo.unapply _)


  def tagIndex = index("file_code_index", fileCode)
}
