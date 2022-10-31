package com.aivanouski.akkaphone.actor

import akka.cluster.sharding.typed.javadsl.ClusterSharding
import com.aivanouski.akkaphone.LoggerDelegate
import com.aivanouski.akkaphone.message.PhoneBookingActionMessage
import com.aivanouski.akkaphone.message.PhoneBookingActionType
import com.aivanouski.akkaphone.props.EventSourcingProperties
import kotlinx.coroutines.reactor.awaitSingle
import org.springframework.stereotype.Service
import reactor.kotlin.core.publisher.toMono
import java.time.Duration
import java.util.UUID
import java.util.concurrent.CompletionStage

interface PhoneBookingService {
    suspend fun applyPhoneBookingAction(action: PhoneBookingActionMessage): PhoneBookingState
    suspend fun getPhoneInfo(phoneUuid: UUID): PhoneBookingState
    suspend fun getAllPhonesInfo(phoneUuids: List<UUID>): List<PhoneBookingState>
}

@Service
class DefaultPhoneBookingService(
    props: EventSourcingProperties,
    private val sharding: ClusterSharding
) : PhoneBookingService {

    private val logger by LoggerDelegate()

    private val askDuration = Duration.ofSeconds(props.askTimeoutSeconds)

    override suspend fun applyPhoneBookingAction(action: PhoneBookingActionMessage): PhoneBookingState = when (action.actionType) {
        PhoneBookingActionType.BOOK -> arrayListOf(action.phoneUuid)
            .map<UUID, CompletionStage<PhoneBookingState>> { imei ->
                imei.toString().entityRef()
                    .ask({ replyTo -> AddPhoneBookingCommand(imei, action.personName!!, replyTo) }, askDuration)
            }
        PhoneBookingActionType.RETURN -> arrayListOf(action.phoneUuid)
            .map<UUID, CompletionStage<PhoneBookingState>> { imei ->
                imei.toString().entityRef()
                    .ask({ replyTo -> ReturnPhoneBookingCommand(imei, replyTo) }, askDuration)
            }
    }
        .map { it.toCompletableFuture() }
        .map { it.toMono().awaitSingle() }
        .first()
        .also { logger.info("Phone booking action, action type: ${action.actionType}") }

    override suspend fun getPhoneInfo(phoneUuid: UUID): PhoneBookingState = arrayListOf(phoneUuid)
        .also { logger.info("Get phone info, IMEI: $phoneUuid") }
        .map<UUID, CompletionStage<PhoneBookingState>> { imei ->
            phoneUuid.toString().entityRef()
                .ask({ replyTo -> GetPhoneBookingCommand(imei, replyTo) }, askDuration)
        }
        .map { it.toCompletableFuture() }
        .map { it.toMono().awaitSingle() }
        .first()

    override suspend fun getAllPhonesInfo(phoneUuids: List<UUID>): List<PhoneBookingState> = phoneUuids
        .also { logger.info("Get all phone infos, IMEIs: $phoneUuids") }
        .map<UUID, CompletionStage<PhoneBookingState>> { phoneUuid ->
            phoneUuid.toString().entityRef()
                .ask({ replyTo -> GetPhoneBookingCommand(phoneUuid, replyTo) }, askDuration)
        }
        .map { it.toCompletableFuture() }
        .map { it.toMono().awaitSingle() }

    private fun String.entityRef() = sharding.entityRefFor(PhoneBookingActor.ENTITY_TYPE_KEY, this)
}
