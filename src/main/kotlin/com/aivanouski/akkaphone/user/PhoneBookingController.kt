package com.aivanouski.akkaphone.user

import com.aivanouski.akkaphone.LoggerDelegate
import com.aivanouski.akkaphone.web.Success
import io.micrometer.core.annotation.Timed
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import reactor.core.publisher.Mono
import java.util.UUID
import javax.validation.Valid
import javax.validation.constraints.NotBlank

@RestController
@RequestMapping("/api/v1/users")
class PhoneBookingController(
    private val phoneBookingProducerService: PhoneBookingProducerService
) {

    private val logger by LoggerDelegate()

    @PostMapping("/book-phone")
    @Timed
    fun bookPhone(
        @Valid @RequestBody payload: BookPhonePayload
    ): Mono<Success> {
        logger.info("Booking phone ${payload.imei} by ${payload.personName}")
        phoneBookingProducerService.bookPhone(
            phoneUuid = payload.imei,
            personName = payload.personName
        )
        return Mono.just(Success(true))
    }

    @PostMapping("/return-phone")
    @Timed
    fun returnPhone(
        @Valid @RequestBody payload: ReturnPhonePayload
    ): Mono<Success> {
        logger.info("Returning phone ${payload.imei}")
        phoneBookingProducerService.returnPhone(
            phoneUuid = payload.imei
        )
        return Mono.just(Success(true))
    }

    data class BookPhonePayload(
        @NotBlank(message = "phone IMEI number must not be null or empty")
        val imei: UUID,
        @NotBlank(message = "person name must not be null or empty")
        val personName: String
    )

    data class ReturnPhonePayload(
        @NotBlank(message = "phone IMEI number must not be null or empty")
        val imei: UUID
    )
}
