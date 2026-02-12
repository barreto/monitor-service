package com.example.monitor.dtos

data class MetricFamily(
    val name: String,
    var help: String? = null,
    var type: String? = null,
    val samples: MutableList<MetricSample> = mutableListOf()
)
