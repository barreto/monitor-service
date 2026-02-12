package com.example.monitor.dtos

data class MetricSample(
    val name: String,
    val labels: Map<String, String> = emptyMap(),
    val value: String,
)