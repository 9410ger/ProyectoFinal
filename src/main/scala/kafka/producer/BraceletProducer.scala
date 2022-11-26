package kafka.producer

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.scala.DefaultScalaModule
import com.typesafe.scalalogging.Logger
import kafka.producer.entity.{HumanSignal, Position}
import org.apache.kafka.clients.producer.{KafkaProducer, ProducerRecord}
import org.apache.kafka.common.serialization.StringSerializer
import java.util.{Calendar, Properties}

object BraceletProducer extends App {

  val LOGGER = Logger("BraceletProducer")
  val mapper = new ObjectMapper()
  mapper.registerModule(DefaultScalaModule)

  val kafkaProducerProps: Properties = {
    val props = new Properties()
    props.put("bootstrap.servers", "localhost:9092")
    props.put("key.serializer", classOf[StringSerializer].getName)
    props.put("value.serializer", classOf[StringSerializer].getName)
    props
  }

  val producer = new KafkaProducer[String, String](kafkaProducerProps)

  while (true) {
    val position = Position(1.236,58.653)
    val humanSignal = HumanSignal(90, 0.96, 0.8, 200,
    "Steal", panicButton = true, position, new java.sql.Timestamp(Calendar.getInstance().getTimeInMillis))
    val humanSignalString = mapper.writeValueAsString(humanSignal)
    Thread.sleep(5000)
    producer.send(new ProducerRecord[String, String]("bracelet-topic", "key",
      humanSignalString)).get()
    LOGGER.info(s"Message Sent - ${humanSignalString}")
  }
  producer.close()

}
