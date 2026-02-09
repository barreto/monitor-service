package com.example.monitor

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

@SpringBootApplication
final class MonitorServiceApplication

fun main(args: Array<String>) {
    runApplication<MonitorServiceApplication>(*args)
}
