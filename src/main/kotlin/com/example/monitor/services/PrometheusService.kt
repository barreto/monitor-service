package com.example.monitor.services

import com.example.monitor.dtos.GetCurrentResponse
import com.example.monitor.dtos.MetricFamily
import com.example.monitor.dtos.MetricSample
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

    final fun scrapeAsJson(): List<MetricFamily> {
        val text = scrape()

        val families = linkedMapOf<String, MetricFamily>()

        val sampleLineRegex = Regex("^([^\\{\\s]+)(\\{[^}]*\\})?\\s+([^\\s]+)(?:\\s+(\\d+))?$")
        val labelRegex = Regex("([a-zA-Z_][a-zA-Z0-9_]*)\\s*=\\s*\"((?:\\\\.|[^\\\\\"])*)\"")

        text.lines().forEach { rawLine ->
            val line = rawLine.trim()
            if (line.isEmpty()) return@forEach

            when {
                line.startsWith("# HELP") -> {
                    // Format: # HELP <metric_name> <help text>
                    val parts = line.split(Regex("\\s+"), limit = 4)
                    if (parts.size >= 3) {
                        val name = parts[2]
                        val help = parts.drop(3).joinToString(" ")
                        val fam = families.getOrPut(name) { MetricFamily(name) }
                        fam.help = help
                    }
                }

                line.startsWith("# TYPE") -> {
                    // Format: # TYPE <metric_name> <type>
                    val parts = line.split(Regex("\\s+"), limit = 4)
                    if (parts.size >= 4) {
                        val name = parts[2]
                        val type = parts[3]
                        val fam = families.getOrPut(name) { MetricFamily(name) }
                        fam.type = type
                    }
                }

                line.startsWith("#") -> {
                    // other comments - ignore
                }

                else -> {
                    // sample line
                    val m = sampleLineRegex.matchEntire(line)
                    if (m != null) {
                        val name = m.groupValues[1]
                        val labelsPart = m.groupValues[2]
                        val value = m.groupValues[3]
                        val tsPart = m.groupValues[4]

                        val labels = if (labelsPart.isNotEmpty()) {
                            // strip surrounding { }
                            val inner = labelsPart.substring(1, labelsPart.length - 1)
                            val map = mutableMapOf<String, String>()
                            labelRegex.findAll(inner).forEach { lm ->
                                val k = lm.groupValues[1]
                                val v = lm.groupValues[2].replace("\\\\", "\\").replace("\"", "\"")
                                map[k] = v
                            }
                            map
                        } else emptyMap()

                        val fam = families.getOrPut(name) { MetricFamily(name) }
                        fam.samples.add(MetricSample(name, labels, value))
                    }
                }
            }
        }

        return families.values.toList()
    }

    final fun queryInstant(promql: String): Mono<GetCurrentResponse> {
        return webClient.get()
            .uri { uriBuilder ->
                uriBuilder.path("/api/v1/query")
                    .queryParam("query", promql)
                    .build()
            }
            .retrieve()
            .bodyToMono(GetCurrentResponse::class.java)
    }

    final fun queryRange(promql: String, step: Duration): Mono<GetCurrentResponse> {
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
            .bodyToMono(GetCurrentResponse::class.java)
    }
}