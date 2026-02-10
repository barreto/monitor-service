package com.example.monitor.configs

import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.web.reactive.function.client.WebClient

@Configuration
class WebClientConfig(
    @Value($$"${prometheus.url}") private val prometheusUrl: String
) {

    @Bean
    fun webClientBuilder(): WebClient.Builder {
        return WebClient.builder()
    }

    @Bean
    fun prometheusWebClient(builder: WebClient.Builder): WebClient {
        return builder.baseUrl(prometheusUrl).build()
    }
}