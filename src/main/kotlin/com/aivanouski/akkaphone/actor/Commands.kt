package com.aivanouski.akkaphone.actor

import akka.actor.typed.ActorRef
import com.aivanouski.akkaphone.serialization.AkkaSerializable
import java.util.UUID

interface BookingCommand : AkkaSerializable

abstract class PhoneBookingCommand(
    val phoneUuid: UUID,
    val personName: String? = null
) : BookingCommand

class AddPhoneBookingCommand(
    phoneUuid: UUID,
    personName: String
) : PhoneBookingCommand(phoneUuid, personName)

class ReturnPhoneBookingCommand(
    phoneUuid: UUID
) : PhoneBookingCommand(phoneUuid)

class GetPhoneBookingCommand(
    phoneUuid: UUID,
    val replyTo: ActorRef<PhoneBookingState>
) : PhoneBookingCommand(phoneUuid)

