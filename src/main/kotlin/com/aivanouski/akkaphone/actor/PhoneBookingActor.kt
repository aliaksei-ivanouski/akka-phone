package com.aivanouski.akkaphone.actor

import akka.actor.typed.Behavior
import akka.actor.typed.SupervisorStrategy
import akka.actor.typed.javadsl.ActorContext
import akka.actor.typed.javadsl.Behaviors
import akka.cluster.sharding.typed.javadsl.EntityTypeKey
import akka.persistence.typed.PersistenceId
import akka.persistence.typed.javadsl.CommandHandler
import akka.persistence.typed.javadsl.Effect
import akka.persistence.typed.javadsl.EventHandler
import akka.persistence.typed.javadsl.EventSourcedBehavior
import akka.persistence.typed.javadsl.RetentionCriteria
import akka.persistence.typed.javadsl.SnapshotCountRetentionCriteria
import com.aivanouski.akkaphone.fonoapi.FonoAPIClient
import com.aivanouski.akkaphone.phone.PhoneInfoRepository
import com.aivanouski.akkaphone.props.EventSourcingProperties
import org.springframework.data.repository.findByIdOrNull
import java.time.Duration
import java.time.Instant
import java.util.UUID

class PhoneBookingActor(
    private val phoneUuid: String,
    private val context: ActorContext<PhoneBookingCommand>,
    private val props: EventSourcingProperties,
    private val phoneInfoRepository: PhoneInfoRepository,
    private val fonoAPIClient: FonoAPIClient,
    persistenceId: PersistenceId?
) : EventSourcedBehavior<PhoneBookingCommand, PhoneBookingEvent, PhoneBookingState>(
    persistenceId, SupervisorStrategy.restartWithBackoff(
        Duration.ofSeconds(props.restartBackOffMinSeconds),
        Duration.ofSeconds(props.restartBackOffMaxSeconds),
        props.restartBackOffRandomFactor
    )
) {

    companion object {
        var ENTITY_TYPE_KEY: EntityTypeKey<PhoneBookingCommand> =
            EntityTypeKey.create(PhoneBookingCommand::class.java, "PhoneBooking")

        fun createBehaviour(
            phoneUuid: String,
            persistenceId: PersistenceId,
            phoneInfoRepository: PhoneInfoRepository,
            fonoAPIClient: FonoAPIClient,
            props: EventSourcingProperties
        ): Behavior<PhoneBookingCommand> =
            Behaviors.setup { context ->
                PhoneBookingActor(
                    phoneUuid,
                    context,
                    props,
                    phoneInfoRepository,
                    fonoAPIClient,
                    persistenceId
                )
            }
    }

    override fun retentionCriteria(): SnapshotCountRetentionCriteria = RetentionCriteria
        .snapshotEvery(props.numberOfEvents, props.keepSnapshots)
        .withDeleteEventsOnSnapshot()

    override fun emptyState() = PhoneBookingState(phoneUuid)
    override fun eventHandler(): EventHandler<PhoneBookingState, PhoneBookingEvent> =
        EventHandler { state, event -> event.applyTo(state) }

    override fun commandHandler(): CommandHandler<PhoneBookingCommand, PhoneBookingEvent, PhoneBookingState> =
        newCommandHandlerBuilder()
            .forAnyState()
            .onCommand(AddPhoneBookingCommand::class.java, this::bookPhone)
            .onCommand(ReturnPhoneBookingCommand::class.java, this::returnPhone)
            .onCommand(GetPhoneBookingCommand::class.java, this::getState)
            .build()

    private fun bookPhone(command: AddPhoneBookingCommand): Effect<PhoneBookingEvent, PhoneBookingState> =
        AddPhoneBookingEvent(command.phoneUuid, command.personName!!, Instant.now()).let { event ->
            Effect()
                .persist(event)
                .thenRun<PhoneBookingState> { newState ->
                    phoneInfoRepository.findByIdOrNull(command.phoneUuid)
                        ?.also {
                            it.status = BookingStatus.BOOKED
                            it.whoBooked = command.personName
                        }
                        ?.also { phoneInfoRepository.save(it) }
                        ?.also {
                            newState.brand = it.brand
                            newState.device = it.device
                            newState.band = it.band
                            newState.personName = it.whoBooked
                            newState.time = it.updatedAt
                        }
                    command.replyTo.tell(newState)
                }
        }

    private fun returnPhone(command: ReturnPhoneBookingCommand): Effect<PhoneBookingEvent, PhoneBookingState> =
        ReturnPhoneBookingEvent(command.phoneUuid, Instant.now()).let { event ->
            Effect()
                .persist(event)
                .thenRun<PhoneBookingState> { newState ->
                    phoneInfoRepository.findByIdOrNull(command.phoneUuid)
                        ?.also {
                            it.status = BookingStatus.AVAILABLE
                            it.whoBooked = null
                        }
                        ?.also { phoneInfoRepository.save(it) }
                        ?.also {
                            newState.brand = it.brand
                            newState.device = it.device
                            newState.band = it.band
                            newState.personName = it.whoBooked
                            newState.time = it.updatedAt
                        }
                    command.replyTo.tell(newState)
                }
        }

    private fun getState(command: GetPhoneBookingCommand): Effect<PhoneBookingEvent, PhoneBookingState> =
        Effect()
            .none()
            .thenRun<PhoneBookingState> { newState ->
                phoneInfoRepository.findByIdOrNull(UUID.fromString(phoneUuid))
                    ?.also {
                        newState.brand = it.brand
                        newState.device = it.device
                        newState.band = it.band
                        newState.personName = it.whoBooked
                        newState.time = it.updatedAt
                    }
                    ?.also {
                        fonoAPIClient.getPhoneInfo(it.brand, it.device)
                            .also { response -> newState.fonoInfo = response }
                    }
                command.replyTo.tell(newState)
            }
}
