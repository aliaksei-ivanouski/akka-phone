package com.aivanouski.akkaphone.user

import com.aivanouski.akkaphone.LoggerDelegate
import com.aivanouski.akkaphone.message.PhoneBookingActionMessage
import com.aivanouski.akkaphone.message.PhoneBookingActionType
import com.aivanouski.akkaphone.rabbitmq.RabbitMqConfig.Companion.EXCHANGE
import com.aivanouski.akkaphone.rabbitmq.RabbitMqConfig.Companion.ROUTING_KEY
import com.fasterxml.jackson.databind.ObjectMapper
import org.springframework.amqp.rabbit.core.RabbitTemplate
import org.springframework.stereotype.Service
import java.util.UUID

@Service
class PhoneBookingProducerService(
    private val rabbitTemplate: RabbitTemplate,
    private val objectMapper: ObjectMapper
) {

    private val logger by LoggerDelegate()

    fun bookPhone(phoneUuid: UUID, personName: String) {
        logger.info("Sending event to book the phone with IMEI: $phoneUuid by $personName")
        val message = PhoneBookingActionMessage(phoneUuid, personName, PhoneBookingActionType.BOOK)
        rabbitTemplate.convertAndSend(EXCHANGE, ROUTING_KEY,
            objectMapper.writeValueAsBytes(message)
        )
    }

    fun returnPhone(phoneUuid: UUID) {
        logger.info("Sending event to return the phone with IMEI: $phoneUuid")
        val message = PhoneBookingActionMessage(phoneUuid, null, PhoneBookingActionType.RETURN)
        rabbitTemplate.convertAndSend(EXCHANGE, ROUTING_KEY,
            objectMapper.writeValueAsBytes(message)
        )
    }

}
