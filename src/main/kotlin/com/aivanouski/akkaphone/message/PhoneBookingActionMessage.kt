package com.aivanouski.akkaphone.message

import com.aivanouski.akkaphone.serialization.AkkaSerializable
import java.util.UUID

enum class PhoneBookingActionType {
    BOOK,
    RETURN
}

data class PhoneBookingActionMessage(
    val phoneUuid: UUID,
    val personName: String? = null,
    val actionType: PhoneBookingActionType
) : AkkaSerializable

