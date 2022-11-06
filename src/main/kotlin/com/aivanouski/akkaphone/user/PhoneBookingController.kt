package com.aivanouski.akkaphone.user

import com.aivanouski.akkaphone.LoggerDelegate
import com.aivanouski.akkaphone.actor.PhoneBookingService
import com.aivanouski.akkaphone.message.PhoneBookingActionMessage
import com.aivanouski.akkaphone.message.PhoneBookingActionType
import io.micrometer.core.annotation.Timed
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import java.util.UUID
import javax.validation.Valid
import javax.validation.constraints.NotBlank

@RestController
@RequestMapping("/api/v1/users")
class PhoneBookingController(
    private val phoneBookingService: PhoneBookingService
) {

    private val logger by LoggerDelegate()

    @PostMapping("/book-phone")
    @Timed
    suspend fun bookPhone(
        @Valid @RequestBody payload: BookPhonePayload
    ) = phoneBookingService.applyPhoneBookingAction(
        PhoneBookingActionMessage(
            phoneUuid = payload.imei,
            personName = payload.personName,
            actionType = PhoneBookingActionType.BOOK
        )
    )
        .also { logger.info("Booking phone ${payload.imei} by ${payload.personName}") }

    @PostMapping("/return-phone")
    @Timed
    suspend fun returnPhone(
        @Valid @RequestBody payload: ReturnPhonePayload
    ) = phoneBookingService.applyPhoneBookingAction(
        PhoneBookingActionMessage(
            phoneUuid = payload.imei,
            actionType = PhoneBookingActionType.RETURN
        )
    )
        .also { logger.info("Returning phone ${payload.imei}") }

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
