package kafka.producer.entity

import java.sql.Timestamp

case class Alert(position: Position, createdAt: Timestamp)
