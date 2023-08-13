package finloader

import finloader.entities._

import scala.slick.jdbc.JdbcBackend.Database
import scala.slick.lifted.{TableQuery}

/**
 * Created by gefox on 29.12.14.
 */
class DbService(implicit val db: Database) extends DbUtils {
  def ensureTablesCreated(): Unit = {
    ensureTableCreated(TableQuery[Balances])
    ensureTableCreated(TableQuery[Incomes])
    ensureTableCreated(TableQuery[Expenses])
    ensureTableCreated(TableQuery[ExpenseTags])
    ensureTableCreated(TableQuery[ExchangeRates])

    ensureTableCreated(TableQuery[FileInfos])
  }

}
