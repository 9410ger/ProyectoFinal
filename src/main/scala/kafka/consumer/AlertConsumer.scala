package kafka.consumer

import Repository.AlertDAO
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.scala.DefaultScalaModule
import com.typesafe.scalalogging.Logger
import kafka.consumer.BraceletConsumer.{kafkaStreamProps, streamTopology}
import kafka.producer.entity.Alert
import org.apache.kafka.streams.KafkaStreams
import org.apache.kafka.streams.scala.StreamsBuilder
import org.apache.kafka.streams.scala.ImplicitConversions._
import org.apache.kafka.streams.scala.serialization.Serdes._

import java.util.Properties

object AlertConsumer extends App {

  val LOGGER = Logger("AlertConsumer")
  val mapper = new ObjectMapper()
  mapper.registerModule(DefaultScalaModule)

  val kafkaStreamProps: Properties = {
    val props = new Properties()
    props.put("bootstrap.servers", "localhost:9092")
    props.put("application.id", "consumer-alerts")
    //    props.put("auto.offset.reset", "latest")
    props
  }

  val streams = new KafkaStreams(streamTopology, kafkaStreamProps)
  Runtime.getRuntime.addShutdownHook(new Thread(() => streams.close()))
  streams.start()


  val alertDao = AlertDAO.getAlertDAO

  def streamTopology = {
    val streamsBuilder = new StreamsBuilder()
    streamsBuilder
      .stream[String, String]("alert-topic")
      .foreach((key, value) => {
        val alert = mapper.readValue(value, classOf[Alert])
        alertDao.saveAlert(alert)
        }
      )
    streamsBuilder.build()
  }

}
