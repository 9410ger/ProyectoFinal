package kafka.producer.entity

import java.sql.Timestamp

case class HumanSignal(heartbeat: Integer, oxygenPercentage: Double, stressPercentage: Double,
                  decibelsVoice: Integer, externalNoise: String, panicButton: Boolean,
                  currentPosition: Position, currentDate: Timestamp)

case class Position(latitude: Double, longitude: Double)
