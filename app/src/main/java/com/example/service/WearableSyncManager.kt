package com.example.service

import com.example.model.WearableDevice
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import kotlin.random.Random

class WearableSyncManager(private val externalScope: CoroutineScope) {

    private val timeFormat = SimpleDateFormat("h:mm a", Locale.getDefault())

    private val initialDevices = listOf(
        WearableDevice(
            id = "w1",
            name = "Pixel Watch 3 (45mm)",
            brand = "Google Pixel",
            isConnected = true,
            batteryPct = 84,
            lastSyncFormatted = "Just now",
            currentHeartRateBpm = 72,
            restingHeartRateBpm = 61,
            stepsToday = 8420,
            activeBurnedKcal = 512,
            hrvMs = 68,
            signalStrengthDbm = -54
        ),
        WearableDevice(
            id = "w2",
            name = "Garmin Forerunner 965",
            brand = "Garmin",
            isConnected = false,
            batteryPct = 92,
            lastSyncFormatted = "Yesterday 9:15 PM",
            currentHeartRateBpm = 0,
            restingHeartRateBpm = 58,
            stepsToday = 9100,
            activeBurnedKcal = 620,
            hrvMs = 74,
            signalStrengthDbm = -72
        ),
        WearableDevice(
            id = "w3",
            name = "Whoop 4.0 Strap",
            brand = "Whoop",
            isConnected = false,
            batteryPct = 67,
            lastSyncFormatted = "Today 6:30 AM",
            currentHeartRateBpm = 0,
            restingHeartRateBpm = 59,
            stepsToday = 7800,
            activeBurnedKcal = 490,
            hrvMs = 82,
            signalStrengthDbm = -65
        )
    )

    private val _devices = MutableStateFlow(initialDevices)
    val devices: StateFlow<List<WearableDevice>> = _devices.asStateFlow()

    private val _activeDevice = MutableStateFlow(initialDevices[0])
    val activeDevice: StateFlow<WearableDevice> = _activeDevice.asStateFlow()

    private val _isSyncing = MutableStateFlow(false)
    val isSyncing: StateFlow<Boolean> = _isSyncing.asStateFlow()

    init {
        startTelemetryLoop()
    }

    private fun startTelemetryLoop() {
        externalScope.launch(Dispatchers.Default) {
            while (true) {
                delay(3000)
                val current = _activeDevice.value
                if (current.isConnected) {
                    val deltaHr = Random.nextInt(-3, 4)
                    val newHr = (current.currentHeartRateBpm + deltaHr).coerceIn(60, 160)
                    val newSteps = current.stepsToday + Random.nextInt(0, 5)
                    val newKcal = current.activeBurnedKcal + (if (Random.nextBoolean()) 1 else 0)

                    val updated = current.copy(
                        currentHeartRateBpm = newHr,
                        stepsToday = newSteps,
                        activeBurnedKcal = newKcal
                    )
                    _activeDevice.value = updated
                    _devices.value = _devices.value.map { if (it.id == updated.id) updated else it }
                }
            }
        }
    }

    fun syncNow(onDataSynced: (steps: Int, activeKcal: Int, heartRate: Int) -> Unit) {
        externalScope.launch {
            _isSyncing.value = true
            delay(1200) // realistic BLE exchange latency
            val current = _activeDevice.value
            val simulatedSteps = (current.stepsToday + Random.nextInt(15, 60))
            val simulatedKcal = (current.activeBurnedKcal + Random.nextInt(2, 8))
            val currentHr = Random.nextInt(68, 85)

            val synced = current.copy(
                stepsToday = simulatedSteps,
                activeBurnedKcal = simulatedKcal,
                currentHeartRateBpm = currentHr,
                lastSyncFormatted = timeFormat.format(Date())
            )
            _activeDevice.value = synced
            _devices.value = _devices.value.map { if (it.id == synced.id) synced else it }
            _isSyncing.value = false
            onDataSynced(simulatedSteps, simulatedKcal, currentHr)
        }
    }

    fun toggleDeviceConnection(deviceId: String) {
        val list = _devices.value.map { dev ->
            if (dev.id == deviceId) {
                val newStatus = !dev.isConnected
                dev.copy(
                    isConnected = newStatus,
                    currentHeartRateBpm = if (newStatus) 72 else 0,
                    lastSyncFormatted = if (newStatus) "Just connected" else dev.lastSyncFormatted
                )
            } else {
                dev
            }
        }
        _devices.value = list
        val currentActive = list.find { it.id == _activeDevice.value.id } ?: list.first()
        _activeDevice.value = currentActive
    }

    fun selectActiveDevice(deviceId: String) {
        _devices.value.find { it.id == deviceId }?.let { dev ->
            _activeDevice.value = dev
        }
    }
}
