package com.example.monitor.dtos

import com.fasterxml.jackson.annotation.JsonProperty

data class GetCurrentResponse(
    val status: String,
    val data: GetCurrentResponseData
)

data class GetCurrentResponseData(
    val resultType: String,
    val result: List<GetCurrentResponseDataResult>
)

data class GetCurrentResponseDataResult(
    val metric: Metric,
    val value: List<Any> // O primeiro elemento é Double (timestamp), o segundo é String (valor)
)

data class Metric(
    @JsonProperty("__name__")
    val name: String,
    val instance: String,
    val job: String
)

