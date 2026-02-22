package com.example.monitor.controllers

import com.example.monitor.dtos.GetCurrentResponse
import com.example.monitor.dtos.MetricFamily
import com.example.monitor.services.PrometheusService
import org.springframework.http.MediaType
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController
import reactor.core.publisher.Mono
import java.time.Duration

@RestController
@RequestMapping("/api/metrics")
final class MetricsController(private val prometheusService: PrometheusService) {

    @GetMapping("/prometheus", produces = [MediaType.TEXT_PLAIN_VALUE])
    final fun prometheus(): String {
        return prometheusService.scrape()
    }

    @GetMapping("/prometheus/json", produces = [MediaType.APPLICATION_JSON_VALUE])
    final fun prometheusJson(): ResponseEntity<List<MetricFamily>> {
        return ResponseEntity.ok(prometheusService.scrapeAsJson())
    }

    @GetMapping("/current", produces = [MediaType.TEXT_PLAIN_VALUE])
    final fun current(
        @RequestParam(required = false, defaultValue = "up") type: String
    ): Mono<ResponseEntity<GetCurrentResponse>> {
        return prometheusService.queryInstant(type).map { ResponseEntity.ok(it) }
    }

    @GetMapping("/history", produces = [MediaType.APPLICATION_JSON_VALUE])
    final fun history(
        @RequestParam(required = false, defaultValue = "up") type: String,
        @RequestParam(required = false, defaultValue = "15") stepInSec: Long
    ): Mono<ResponseEntity<GetCurrentResponse>> {
        return prometheusService.queryRange(type, Duration.ofSeconds(stepInSec))
            .map { ResponseEntity.ok(it) }
    }
}
