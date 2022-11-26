package Repository

import com.typesafe.scalalogging.Logger
import kafka.producer.entity.Alert
import org.apache.commons.dbcp2.BasicDataSource

case class AlertDAO(dataSource: BasicDataSource){

  val LOGGER = Logger("AlertDAO")

  val saveAlertQuery = s"INSERT INTO alerts (latitude, longitude, created_at) VALUES(?, ?, ?)"

  def saveAlert(alert: Alert): Unit={
    try{
      val connection= dataSource.getConnection
      val preparedStatement = connection.prepareStatement(saveAlertQuery)
      preparedStatement.setDouble(1, alert.position.latitude)
      preparedStatement.setDouble(2, alert.position.longitude)
      preparedStatement.setTimestamp(3, alert.createdAt)
      preparedStatement.executeUpdate()
      connection.close()
    }catch {
      case e: Exception => LOGGER.error(e.getMessage)
    }

  }

}

object AlertDAO {
  def getAlertDAO = {
    val dbUrl = "jdbc:postgresql://localhost:5432/postgres"
    val connectionPool = new BasicDataSource()
    connectionPool.setUsername("postgres")
    connectionPool.setPassword("postgres")
    connectionPool.setDriverClassName("org.postgresql.Driver")
    connectionPool.setUrl(dbUrl)
    connectionPool.setInitialSize(5)
    AlertDAO(connectionPool)
  }
}
