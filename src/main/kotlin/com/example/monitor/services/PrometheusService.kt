package com.example.monitor.services

import io.micrometer.prometheusmetrics.PrometheusMeterRegistry
import org.springframework.stereotype.Service
import org.springframework.web.reactive.function.client.WebClient
import reactor.core.publisher.Mono
import java.time.Duration
import java.time.Instant

@Service
final class PrometheusService(
    private val webClient: WebClient,
    private val prometheusRegistry: PrometheusMeterRegistry
) {

    final fun scrape(): String {
        return prometheusRegistry.scrape()
    }

    final fun queryInstant(promql: String): Mono<String> {
        return webClient.get()
            .uri { uriBuilder ->
                uriBuilder.path("/api/v1/query")
                    .queryParam("query", promql)
                    .build()
            }
            .retrieve()
            .bodyToMono(String::class.java)
    }

    final fun queryRange(promql: String, step: Duration): Mono<String> {
        val end = Instant.now()
        val start = end.minusSeconds(300)

        return webClient.get()
            .uri { uriBuilder ->
                uriBuilder.path("/api/v1/query_range")
                    .queryParam("query", promql)
                    .queryParam("start", start.epochSecond)
                    .queryParam("end", end.epochSecond)
                    .queryParam("step", step.seconds)
                    .build()
            }
            .retrieve()
            .bodyToMono(String::class.java)
    }
}