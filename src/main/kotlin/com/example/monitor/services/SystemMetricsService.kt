package com.example.monitor.services

import org.springframework.stereotype.Service
import oshi.SystemInfo

@Service
class SystemMetricsService {

    private val systemInfo = SystemInfo()
    private val hardware = systemInfo.hardware

    var prevTicks: LongArray = hardware.processor.systemCpuLoadTicks

    fun getCpuUsage(): Double {
        val processor = hardware.processor
        val load = processor.getSystemCpuLoadBetweenTicks(prevTicks)
        prevTicks = processor.systemCpuLoadTicks

        return load * 100
    }

    fun getMemoryUsage(): Double {
        val memory = hardware.memory
        val used = memory.total - memory.available

        return used.toDouble() / memory.total * 100
    }

    fun getDiskUsage(): List<Pair<String, Double>> {
        val fileSystem = systemInfo.operatingSystem.fileSystem

        return fileSystem.fileStores.map {
            val usage = it.usableSpace.toDouble() / it.totalSpace * 100
            it.name to (100 - usage)
        }
    }
}
