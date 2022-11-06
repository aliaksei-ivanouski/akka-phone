package com.aivanouski.akkaphone.actor

import com.aivanouski.akkaphone.state.AbstractBaseState
import com.aivanouski.akkaphone.serialization.AkkaSerializable
import com.fasterxml.jackson.annotation.JsonInclude
import com.fasterxml.jackson.databind.JsonNode
import java.time.Instant

data class PhoneBookingState(
    val imei: String,
    var brand: String? = null,
    var device: String? = null,
    var band: String? = null,
    @JsonInclude(JsonInclude.Include.NON_NULL)
    var personName: String? = null,
    var time: Instant? = null,
    var status: BookingStatus = BookingStatus.AVAILABLE,
    var fonoInfo: JsonNode? = null
) : AbstractBaseState(imei), AkkaSerializable
