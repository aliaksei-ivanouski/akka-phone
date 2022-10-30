package com.aivanouski.akkaphone.config

import akka.actor.typed.ActorSystem
import akka.actor.typed.javadsl.Behaviors
import akka.cluster.sharding.typed.javadsl.ClusterSharding
import akka.cluster.sharding.typed.javadsl.Entity
import akka.management.cluster.bootstrap.ClusterBootstrap
import akka.management.javadsl.AkkaManagement
import akka.persistence.jdbc.testkit.javadsl.SchemaUtils
import akka.persistence.typed.PersistenceId
import com.aivanouski.akkaphone.actor.PhoneBookingActor
import com.aivanouski.akkaphone.fonoapi.FonoAPIClient
import com.aivanouski.akkaphone.phone.PhoneInfoRepository
import com.aivanouski.akkaphone.props.EventSourcingProperties
import com.typesafe.config.Config
import com.typesafe.config.ConfigFactory
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.core.env.Environment

@Configuration
class AkkaConfig {

    @Bean
    fun akkaConfiguration(env: Environment): Config {
        val filePostfix = if (env.activeProfiles.contains("local").not()) "local" else env.activeProfiles.first()
        return ConfigFactory
            .parseResources("akka_$filePostfix.cfg")
            .resolve();
    }

    @Bean
    fun actorSystem(
        config: Config,
        @Value("\${akka.cluster.system.name}") systemName: String
    ): ActorSystem<Any> = ActorSystem.create(Behaviors.empty(), systemName, config)

    @Bean
    fun clusterSharding(
        actorSystem: ActorSystem<Any>,
        env: Environment,
        props: EventSourcingProperties,
        phoneInfoRepository: PhoneInfoRepository,
        fonoAPIClient: FonoAPIClient
    ): ClusterSharding {
        val sharding = ClusterSharding.get(actorSystem)
        if (env.activeProfiles.contains("local").not()) {
            AkkaManagement.get(actorSystem).start()
            ClusterBootstrap.get(actorSystem).start()
        }

        SchemaUtils.createIfNotExists(actorSystem)
            .toCompletableFuture()
            .get() //Wait for DB schema creation

        sharding.init(Entity.of(PhoneBookingActor.ENTITY_TYPE_KEY) { context ->
            PhoneBookingActor.createBehaviour(
                context.entityId,
                PersistenceId.of(context.entityTypeKey.name(), context.entityId),
                phoneInfoRepository,
                fonoAPIClient,
                props
            )
        })

        return sharding
    }
}
