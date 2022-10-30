package com.aivanouski.akkaphone.rabbitmq

import org.springframework.amqp.core.AmqpAdmin
import org.springframework.amqp.core.Binding
import org.springframework.amqp.core.Declarables
import org.springframework.amqp.core.DirectExchange
import org.springframework.amqp.core.Queue
import org.springframework.amqp.rabbit.connection.ConnectionFactory
import org.springframework.amqp.rabbit.core.RabbitAdmin
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration


@Configuration
class RabbitMqConfig(
    private val connectionFactory: ConnectionFactory
) {

    @Bean
    fun rabbitAdmin(): AmqpAdmin? {
        return RabbitAdmin(connectionFactory)
    }

    @Bean
    fun declareQueue() = Queue(QUEUE, true, false, false)

    @Bean
    fun declareDirectExchange(): DirectExchange? {
        return DirectExchange(EXCHANGE, true, false)
    }

    @Bean
    fun declareBindings(): Declarables? {
        return Declarables(
            Binding(QUEUE, Binding.DestinationType.QUEUE, EXCHANGE, ROUTING_KEY, null)
        )
    }

    companion object {
        const val ROUTING_KEY = "phone_booking_queue_routing_key"
        const val EXCHANGE = "phone_booking_exchange"
        const val QUEUE = "phone_booking_queue"
    }
}
