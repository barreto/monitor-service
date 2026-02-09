package com.example.monitor

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

@SpringBootApplication
class MonitorServiceApplication

fun main(args: Array<String>) {
	runApplication<MonitorServiceApplication>(*args)
}
