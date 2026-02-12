package com.example.monitor.configs

import io.micrometer.core.instrument.MeterRegistry
import io.micrometer.core.instrument.config.MeterFilter
import org.springframework.boot.micrometer.metrics.autoconfigure.MeterRegistryCustomizer
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
class PrometheusConfigCustom {

    @Bean
    fun metricsRegistryConfig(): MeterRegistryCustomizer<MeterRegistry> {
        return MeterRegistryCustomizer { meterRegistry ->
            meterRegistry.config()
                .meterFilter(MeterFilter.denyUnless { it.name.startsWith("custom") })
        }
    }
}
