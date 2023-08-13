package finloader

import scala.slick.jdbc.JdbcBackend.Database
import scala.slick.driver.JdbcDriver.simple._
import scala.slick.jdbc.meta.MTable
import scala.slick.lifted.{TableQuery}
import scala.slick.ast.{TableExpansion, TableNode}

/**
 * @author Paul Lysak
 *         Date: 13.09.13
 *         Time: 22:56
 */
trait DbUtils {
  def ensureTableCreated[E <: Table[_]](tableQueryObject: TableQuery[E])(implicit db: Database) {
    db.withSession {implicit session =>
      val tableName = tableQueryObject.toNode.asInstanceOf[TableExpansion].table.asInstanceOf[TableNode].tableName
      if(MTable.getTables(tableName).firstOption.isEmpty)
          tableQueryObject.ddl.create
    }
  }
}
