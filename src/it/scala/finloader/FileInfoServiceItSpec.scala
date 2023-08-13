package finloader

import finloader.entities.FileInfos
import org.joda.time.LocalDateTime
import org.specs2.mutable.Specification

import ITUtils.db
import scala.slick.driver.JdbcDriver.simple._
import scala.slick.lifted.TableQuery

/**
 * Created by gefox on 26.12.14.
 */
class FileInfoServiceItSpec extends Specification {
  sequential

  "FileInfoService" should {
    cleanFileInfo

    "detect missing record" in {
      fiService.needsUpdate(SAMPLE_FILE, SAMPLE_DATE1) must beTrue
    }

    "remember file date" in {
      fiService.setUpdatedDateTime(SAMPLE_FILE, SAMPLE_DATE1)

      fiService.needsUpdate(SAMPLE_FILE, SAMPLE_DATE1) must beFalse
    }

    "detect outdated record" in {
      fiService.needsUpdate(SAMPLE_FILE, SAMPLE_DATE2) must beTrue
    }

    "overwrite file date" in {
     fiService.setUpdatedDateTime(SAMPLE_FILE, SAMPLE_DATE2)

      fiService.needsUpdate(SAMPLE_FILE, SAMPLE_DATE1) must beFalse
      fiService.needsUpdate(SAMPLE_FILE, SAMPLE_DATE2) must beFalse
    }

    "avoid redundant records" in {
      db.withSession { implicit session =>
        fiQuery.list.size must beEqualTo(1)
      }
    }
  }

  private val SAMPLE_FILE = "sampleFile"

  private val SAMPLE_DATE1 = LocalDateTime.parse("2014-12-24")

  private val SAMPLE_DATE2 = LocalDateTime.parse("2014-12-25")

  private val fiService = new FileInfoService(ITUtils.db)

  private val fiQuery = TableQuery[FileInfos]

  private def cleanFileInfo {
    db.withSession { implicit session =>
        fiQuery.delete
        fiQuery.list.toSet must beEmpty
    }
  }

}
