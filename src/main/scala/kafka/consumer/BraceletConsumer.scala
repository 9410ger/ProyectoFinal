package kafka.consumer

import Repository.HumanSignalDAO
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.scala.DefaultScalaModule
import com.typesafe.scalalogging.Logger
import kafka.producer.entity.HumanSignal
import org.apache.kafka.streams.KafkaStreams
import org.apache.kafka.streams.scala.StreamsBuilder
import org.apache.kafka.streams.scala.ImplicitConversions._
import org.apache.kafka.streams.scala.serialization.Serdes._

import java.util.Properties

object BraceletConsumer extends App {

  val LOGGER = Logger("BraceletConsumer")
  val mapper = new ObjectMapper()
  mapper.registerModule(DefaultScalaModule)

  val kafkaStreamProps: Properties = {
    val props = new Properties()
    props.put("bootstrap.servers", "localhost:9092")
    props.put("application.id", "consumer-stream-sample")
    //    props.put("auto.offset.reset", "latest")
    props
  }

  val humanSignalDAO = HumanSignalDAO.getHumanSignalDAO

  val streams = new KafkaStreams(streamTopology, kafkaStreamProps)
  Runtime.getRuntime.addShutdownHook(new Thread(() => streams.close()))
  streams.start()

  def streamTopology = {
    val streamsBuilder = new StreamsBuilder()
    streamsBuilder
      .stream[String, String]("bracelet-topic")
      .foreach((key, value) => {
        val humanSignal = mapper.readValue(value, classOf[HumanSignal])
        humanSignalDAO.saveHumanSignal(humanSignal)
        }
      )
    streamsBuilder.build()
  }

}
