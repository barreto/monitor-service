package com.example.monitor.components

import com.example.monitor.services.SystemMetricsService
import io.micrometer.core.instrument.Gauge
import io.micrometer.prometheusmetrics.PrometheusMeterRegistry
import jakarta.annotation.PostConstruct
import org.springframework.stereotype.Component

@Component
class MetricsCollector(
    private val systemMetricsService: SystemMetricsService,
    private val meterRegistry: PrometheusMeterRegistry
) {

    @PostConstruct
    fun initMetrics() {
        Gauge.builder("custom_system_cpu_usage") { systemMetricsService.getCpuUsage() }
            .description("CPU usage percentage")
            .register(meterRegistry)

        Gauge.builder("custom_system_memory_usage") { systemMetricsService.getMemoryUsage() }
            .description("Memory usage percentage")
            .register(meterRegistry)

        systemMetricsService.getDiskUsage().forEach { (name, _) ->
            Gauge.builder("custom_system_disk_usage") {
                systemMetricsService.getDiskUsage().find { it.first == name }?.second ?: 0.0
            }
                .description("Disk usage percentage for $name")
                .tag("disk", name)
                .register(meterRegistry)
        }
    }
}
