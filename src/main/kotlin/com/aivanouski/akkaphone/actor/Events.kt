package com.aivanouski.akkaphone.actor

import com.aivanouski.akkaphone.serialization.AkkaSerializable
import java.time.Instant
import java.util.UUID

interface PhoneBookingEvent : AkkaSerializable {
    fun applyTo(phoneBookingState: PhoneBookingState) : PhoneBookingState
}

data class AddPhoneBookingEvent(val phoneUuid: UUID, val personName: String, val time: Instant) : PhoneBookingEvent {
    override fun applyTo(phoneBookingState: PhoneBookingState) = phoneBookingState.apply {
        phoneBookingState.personName = personName
        phoneBookingState.time = time
        phoneBookingState.status = BookingStatus.BOOKED
    }
}

data class ReturnPhoneBookingEvent(val phoneUuid: UUID, val time: Instant) : PhoneBookingEvent {
    override fun applyTo(phoneBookingState: PhoneBookingState) = phoneBookingState.apply {
        phoneBookingState.time = time
        phoneBookingState.status = BookingStatus.AVAILABLE
    }
}
