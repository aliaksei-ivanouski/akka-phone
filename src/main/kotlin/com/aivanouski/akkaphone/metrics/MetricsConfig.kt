package com.aivanouski.akkaphone.metrics

import io.micrometer.core.aop.TimedAspect
import io.micrometer.core.instrument.MeterRegistry
import org.springframework.boot.actuate.autoconfigure.metrics.MeterRegistryCustomizer
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration


@Configuration
class MetricConfig {
    @Bean
    fun metricsCommonTags() = MeterRegistryCustomizer { registry: MeterRegistry ->
        registry.config().commonTags("application", "Akka Phone App")
    }

    @Bean
    fun timedAspect(registry: MeterRegistry?) = TimedAspect(registry!!)
}