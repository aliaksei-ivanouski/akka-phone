package com.aivanouski.akkaphone

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.cache.annotation.EnableCaching

@EnableCaching
@SpringBootApplication
class AkkaPhoneApplication

fun main(args: Array<String>) {
    runApplication<AkkaPhoneApplication>(*args)
}
