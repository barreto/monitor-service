package com.example.monitor.controller

import io.micrometer.prometheusmetrics.PrometheusMeterRegistry
import org.springframework.http.MediaType
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api/metrics")
final class MetricsController(private val prometheusRegistry: PrometheusMeterRegistry) {

    @GetMapping("/prometheus", produces = [MediaType.TEXT_PLAIN_VALUE])
    final fun prometheus(): String {
        return prometheusRegistry.scrape()
    }
}
