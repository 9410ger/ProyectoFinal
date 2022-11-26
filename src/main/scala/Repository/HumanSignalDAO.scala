package Repository

import com.typesafe.scalalogging.Logger
import kafka.producer.entity.{HumanSignal, Position}
import org.apache.commons.dbcp2.BasicDataSource
import scala.collection.mutable.ListBuffer

case class HumanSignalDAO(dataSource: BasicDataSource) {

  val LOGGER = Logger("HumanSignalDAO")

  val saveHumanSignalQuery = s"INSERT INTO humansignals (heartbeat, oxygen_percentage," +
    s"stress_percentage, decibels_voice, external_noise, panic_button, latitude, longitude," +
    s"signal_date) VALUES(?, ?, ?, ?, ?, ?, ?, ?, ?)"

  val getLastMinuteHumanSignalsQuery = s"SELECT * FROM humansignals WHERE signal_date > now() - interval '60 seconds'"

  def saveHumanSignal(humanSignal: HumanSignal): Unit ={
    try{
      val connection= dataSource.getConnection
      val preparedStatement = connection.prepareStatement(saveHumanSignalQuery)
      preparedStatement.setInt(1, humanSignal.heartbeat)
      preparedStatement.setDouble(2, humanSignal.oxygenPercentage)
      preparedStatement.setDouble(3, humanSignal.stressPercentage)
      preparedStatement.setInt(4, humanSignal.decibelsVoice)
      preparedStatement.setString(5, humanSignal.externalNoise)
      preparedStatement.setBoolean(6, humanSignal.panicButton)
      preparedStatement.setDouble(7, humanSignal.currentPosition.latitude)
      preparedStatement.setDouble(8, humanSignal.currentPosition.longitude)
      preparedStatement.setTimestamp(9, humanSignal.currentDate)
      preparedStatement.executeUpdate()
      connection.close()
    }catch {
      case e: Exception => LOGGER.error(e.getMessage)
    }
  }

  def getLastMinuteHumanSignals(): List[HumanSignal]={
    var humanSignalList = new ListBuffer[HumanSignal]
    try{
      val connection= dataSource.getConnection
      val preparedStatement = connection.prepareStatement(getLastMinuteHumanSignalsQuery)
      val resultSet = preparedStatement.executeQuery()
      var idx = 0
      while(resultSet.next()){
        val humanSignal = HumanSignal(
          resultSet.getInt(1),
          resultSet.getDouble(2),
          resultSet.getDouble(3),
          resultSet.getInt(4),
          resultSet.getString(5),
          resultSet.getBoolean(6),
          Position(resultSet.getDouble(7), resultSet.getDouble(8)),
          resultSet.getTimestamp(9)
        )
        humanSignalList.insert(idx, humanSignal)
        idx += 1
      }
      connection.close()
      humanSignalList.toList
    }catch {
      case e: Exception => LOGGER.error(e.getMessage)
        new ListBuffer[HumanSignal].toList
    }
  }

}

object HumanSignalDAO {
  def getHumanSignalDAO = {
    val dbUrl = "jdbc:postgresql://localhost:5432/postgres"
    val connectionPool = new BasicDataSource()
    connectionPool.setUsername("postgres")
    connectionPool.setPassword("postgres")
    connectionPool.setDriverClassName("org.postgresql.Driver")
    connectionPool.setUrl(dbUrl)
    connectionPool.setInitialSize(5)
    HumanSignalDAO(connectionPool)
  }
}
