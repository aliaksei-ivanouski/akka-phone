package com.aivanouski.akkaphone.error

import com.aivanouski.akkaphone.state.AbstractBaseState

class ErrorState(
    override val entityId: String,
    var errorCode: String
) : AbstractBaseState(entityId)

class ErrorCode {
    companion object {
        const val NOT_FOUND = "NOT_FOUND"
        const val ALREADY_BOOKED = "ALREADY_BOOKED"
        const val ALREADY_AVAILABLE = "ALREADY_AVAILABLE"
    }
}