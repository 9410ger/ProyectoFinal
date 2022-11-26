package kafka.producer

import Repository.HumanSignalDAO
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.scala.DefaultScalaModule
import com.typesafe.scalalogging.Logger
import kafka.producer.entity.{Alert, HumanSignal}
import org.apache.kafka.clients.producer.{KafkaProducer, ProducerRecord}
import org.apache.kafka.common.serialization.StringSerializer

import java.util.{Calendar, Properties}

object AlertsProducer extends App {

  val LOGGER = Logger("AlertsProducer")
  val mapper = new ObjectMapper()
  mapper.registerModule(DefaultScalaModule)

  val kafkaProducerProps: Properties = {
    val props = new Properties()
    props.put("bootstrap.servers", "localhost:9092")
    props.put("key.serializer", classOf[StringSerializer].getName)
    props.put("value.serializer", classOf[StringSerializer].getName)
    props
  }

  val humanSignalDAO = HumanSignalDAO.getHumanSignalDAO



  val producer = new KafkaProducer[String,String](kafkaProducerProps)

  while(true){
    Thread.sleep(1000)
    val humanSignalList = humanSignalDAO.getLastMinuteHumanSignals()
    if(humanSignalList.nonEmpty){
      val result = validateHumanSignals(humanSignalList)
      if(result){
        LOGGER.warn("Alerta!!!!! latitude: {} - longitude: {}", humanSignalList.head.currentPosition.latitude,
          humanSignalList.head.currentPosition.longitude)
        val alert = Alert(humanSignalList.head.currentPosition,
          new java.sql.Timestamp(Calendar.getInstance().getTimeInMillis))
        val alertString = mapper.writeValueAsString(alert)
        producer.send(new ProducerRecord[String,String]("alert-topic", "Warning", alertString))
      }
    }else{
      LOGGER.info("Nothing :(")
    }
  }

  producer.close()

  def validateHumanSignals(humanSignalsList: List[HumanSignal]): Boolean={
    var result = true
    for( hs: HumanSignal <- humanSignalsList){
      if(hs.panicButton &&
        hs.heartbeat <= 53 && hs.heartbeat < 60 &&
        !hs.externalNoise.equals("Steal") &&
        hs.decibelsVoice < 150 &&
        hs.stressPercentage >= 0.7 &&
        hs.oxygenPercentage > 0.95){
        result = false
      }
    }
    result
  }


}
