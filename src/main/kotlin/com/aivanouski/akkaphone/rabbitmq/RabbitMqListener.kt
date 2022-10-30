package com.aivanouski.akkaphone.rabbitmq

import com.aivanouski.akkaphone.message.PhoneBookingActionMessage
import com.aivanouski.akkaphone.actor.PhoneBookingService
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue
import org.springframework.amqp.rabbit.annotation.RabbitListener
import org.springframework.stereotype.Component

@Component
class RabbitMqListener(
    private val objectMapper: ObjectMapper,
    private val phoneBookingService: PhoneBookingService
) {

    @RabbitListener(queues = [RabbitMqConfig.QUEUE])
    fun listen(messagePayload: String) {
        messagePayload.parseToPhoneBookingActionMessage().also { phoneBookingService.applyPhoneBookingAction(it) }
    }

    private fun String.parseToPhoneBookingActionMessage(): PhoneBookingActionMessage = objectMapper.readValue(this)

}
