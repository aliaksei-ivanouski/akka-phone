package com.aivanouski.akkaphone.actor

import akka.actor.typed.ActorRef
import com.aivanouski.akkaphone.state.AbstractBaseState
import com.aivanouski.akkaphone.error.ErrorState
import com.aivanouski.akkaphone.serialization.AkkaSerializable
import java.util.UUID

interface BookingCommand : AkkaSerializable

abstract class PhoneBookingCommand(
    val phoneUuid: UUID,
    val personName: String? = null
) : BookingCommand

class AddPhoneBookingCommand(
    phoneUuid: UUID,
    personName: String,
    val replyTo: ActorRef<AbstractBaseState>
) : PhoneBookingCommand(phoneUuid, personName)

class ReturnPhoneBookingCommand(
    phoneUuid: UUID,
    val replyTo: ActorRef<AbstractBaseState>
) : PhoneBookingCommand(phoneUuid)

class GetPhoneBookingCommand(
    phoneUuid: UUID,
    val replyTo: ActorRef<AbstractBaseState>
) : PhoneBookingCommand(phoneUuid)

class ErrorCommand(
    phoneUuid: UUID,
    val replyTo: ActorRef<ErrorState>
) : PhoneBookingCommand(phoneUuid)
