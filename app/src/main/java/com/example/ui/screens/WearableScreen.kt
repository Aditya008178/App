package com.example.ui.screens

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.BatteryChargingFull
import androidx.compose.material.icons.filled.Bluetooth
import androidx.compose.material.icons.filled.BluetoothConnected
import androidx.compose.material.icons.filled.DirectionsWalk
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.LocalFireDepartment
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Watch
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.ui.theme.CyanSecondary
import com.example.ui.theme.DarkNavyElevated
import com.example.ui.theme.DarkNavySurface
import com.example.ui.theme.EmeraldPrimary
import com.example.ui.theme.OrangeFlame
import com.example.ui.theme.RosePulse
import com.example.ui.theme.TextPrimaryDark
import com.example.ui.theme.TextSecondaryDark
import com.example.ui.viewmodel.FitPulseViewModel

@Composable
fun WearableScreen(
    viewModel: FitPulseViewModel,
    modifier: Modifier = Modifier
) {
    val devices by viewModel.wearableSyncManager.devices.collectAsStateWithLifecycle()
    val activeDevice by viewModel.wearableSyncManager.activeDevice.collectAsStateWithLifecycle()
    val isSyncing by viewModel.wearableSyncManager.isSyncing.collectAsStateWithLifecycle()

    var autoSyncEnabled by remember { mutableStateOf(true) }
    var highHrAlertEnabled by remember { mutableStateOf(true) }

    // Heartbeat pulse animation
    val infiniteTransition = rememberInfiniteTransition(label = "pulse")
    val pulseScale by infiniteTransition.animateFloat(
        initialValue = 1f,
        targetValue = 1.15f,
        animationSpec = infiniteRepeatable(
            animation = tween(800, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "pulseScale"
    )

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .testTag("wearables_screen"),
        contentPadding = PaddingValues(bottom = 24.dp)
    ) {
        // 1. Header
        item {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp, vertical = 14.dp)
            ) {
                Text(
                    text = "Wearable Sync & Integration",
                    style = MaterialTheme.typography.titleLarge.copy(
                        fontWeight = FontWeight.ExtraBold,
                        color = TextPrimaryDark
                    )
                )
                Text(
                    text = "Continuous smartwatch biometric streams & BLE data sync",
                    style = MaterialTheme.typography.bodySmall.copy(color = TextSecondaryDark)
                )
            }
        }

        // 2. Active Connected Device Hero Card
        item {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp, vertical = 6.dp)
                    .testTag("active_wearable_card"),
                colors = CardDefaults.cardColors(containerColor = DarkNavySurface),
                shape = RoundedCornerShape(24.dp)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(20.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(
                                modifier = Modifier
                                    .size(46.dp)
                                    .clip(CircleShape)
                                    .background(if (activeDevice.isConnected) EmeraldPrimary.copy(alpha = 0.2f) else Color.Gray.copy(alpha = 0.2f)),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Watch,
                                    contentDescription = null,
                                    tint = if (activeDevice.isConnected) EmeraldPrimary else Color.Gray,
                                    modifier = Modifier.size(26.dp)
                                )
                            }
                            Spacer(modifier = Modifier.width(12.dp))
                            Column {
                                Text(
                                    text = activeDevice.name,
                                    style = MaterialTheme.typography.titleMedium.copy(
                                        fontWeight = FontWeight.Bold,
                                        color = TextPrimaryDark
                                    )
                                )
                                Text(
                                    text = "${activeDevice.brand} • ${if (activeDevice.isConnected) "Connected via BLE" else "Disconnected"}",
                                    style = MaterialTheme.typography.labelSmall.copy(color = TextSecondaryDark)
                                )
                            }
                        }

                        Surface(
                            color = if (activeDevice.isConnected) EmeraldPrimary.copy(alpha = 0.15f) else Color.Red.copy(alpha = 0.15f),
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(6.dp)
                                        .clip(CircleShape)
                                        .background(if (activeDevice.isConnected) EmeraldPrimary else Color.Red)
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = if (activeDevice.isConnected) "ACTIVE" else "OFFLINE",
                                    style = MaterialTheme.typography.labelSmall.copy(
                                        color = if (activeDevice.isConnected) EmeraldPrimary else Color.Red,
                                        fontWeight = FontWeight.Bold
                                    )
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(20.dp))

                    // Live Biometric Telemetry Grid
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        // Live Heart Rate Card
                        Card(
                            modifier = Modifier.weight(1f),
                            colors = CardDefaults.cardColors(containerColor = DarkNavyElevated),
                            shape = RoundedCornerShape(16.dp)
                        ) {
                            Column(modifier = Modifier.padding(14.dp)) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text("HEART RATE", style = MaterialTheme.typography.labelSmall.copy(color = TextSecondaryDark, fontSize = 10.sp))
                                    Icon(
                                        imageVector = Icons.Default.Favorite,
                                        contentDescription = null,
                                        tint = RosePulse,
                                        modifier = Modifier
                                            .size(16.dp)
                                            .scale(if (activeDevice.isConnected) pulseScale else 1f)
                                    )
                                }
                                Spacer(modifier = Modifier.height(6.dp))
                                Row(verticalAlignment = Alignment.Bottom) {
                                    Text(
                                        text = if (activeDevice.isConnected) "${activeDevice.currentHeartRateBpm}" else "--",
                                        style = MaterialTheme.typography.headlineMedium.copy(
                                            fontWeight = FontWeight.Bold,
                                            color = RosePulse
                                        )
                                    )
                                    Text(
                                        text = " bpm",
                                        modifier = Modifier.padding(bottom = 4.dp),
                                        style = MaterialTheme.typography.bodySmall.copy(color = TextSecondaryDark)
                                    )
                                }
                                Text("Resting: ${activeDevice.restingHeartRateBpm} bpm", style = MaterialTheme.typography.labelSmall.copy(color = TextSecondaryDark, fontSize = 10.sp))
                            }
                        }

                        // Steps Card
                        Card(
                            modifier = Modifier.weight(1f),
                            colors = CardDefaults.cardColors(containerColor = DarkNavyElevated),
                            shape = RoundedCornerShape(16.dp)
                        ) {
                            Column(modifier = Modifier.padding(14.dp)) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text("STEPS SYNCED", style = MaterialTheme.typography.labelSmall.copy(color = TextSecondaryDark, fontSize = 10.sp))
                                    Icon(
                                        imageVector = Icons.Default.DirectionsWalk,
                                        contentDescription = null,
                                        tint = EmeraldPrimary,
                                        modifier = Modifier.size(16.dp)
                                    )
                                }
                                Spacer(modifier = Modifier.height(6.dp))
                                Text(
                                    text = "${activeDevice.stepsToday}",
                                    style = MaterialTheme.typography.headlineMedium.copy(
                                        fontWeight = FontWeight.Bold,
                                        color = TextPrimaryDark
                                    )
                                )
                                Text("Burned: ${activeDevice.activeBurnedKcal} kcal", style = MaterialTheme.typography.labelSmall.copy(color = OrangeFlame, fontSize = 10.sp))
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    // Secondary metrics (HRV & Battery)
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Surface(
                            modifier = Modifier.weight(1f),
                            color = DarkNavyElevated,
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(imageVector = Icons.Default.BatteryChargingFull, contentDescription = null, tint = EmeraldPrimary, modifier = Modifier.size(16.dp))
                                Spacer(modifier = Modifier.width(6.dp))
                                Text("Battery: ${activeDevice.batteryPct}%", style = MaterialTheme.typography.bodySmall.copy(color = TextPrimaryDark))
                            }
                        }

                        Surface(
                            modifier = Modifier.weight(1f),
                            color = DarkNavyElevated,
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(imageVector = Icons.Default.BluetoothConnected, contentDescription = null, tint = CyanSecondary, modifier = Modifier.size(16.dp))
                                Spacer(modifier = Modifier.width(6.dp))
                                Text("HRV: ${activeDevice.hrvMs} ms", style = MaterialTheme.typography.bodySmall.copy(color = TextPrimaryDark))
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(18.dp))

                    // Trigger Sync Button
                    Button(
                        onClick = { viewModel.triggerWearableSync() },
                        enabled = !isSyncing && activeDevice.isConnected,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(50.dp)
                            .testTag("wearables_sync_now_btn"),
                        colors = ButtonDefaults.buttonColors(containerColor = EmeraldPrimary),
                        shape = RoundedCornerShape(14.dp)
                    ) {
                        if (isSyncing) {
                            CircularProgressIndicator(
                                modifier = Modifier.size(20.dp),
                                color = DarkNavySurface,
                                strokeWidth = 2.5.dp
                            )
                            Spacer(modifier = Modifier.width(10.dp))
                            Text("Synchronizing BLE Telemetry...", color = DarkNavySurface, fontWeight = FontWeight.Bold)
                        } else {
                            Icon(imageVector = Icons.Default.Refresh, contentDescription = null, tint = DarkNavySurface)
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("Sync Wearable Data Now", color = DarkNavySurface, style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold))
                        }
                    }

                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        text = "Last synced: ${activeDevice.lastSyncFormatted}",
                        modifier = Modifier.align(Alignment.CenterHorizontally),
                        style = MaterialTheme.typography.labelSmall.copy(color = TextSecondaryDark)
                    )
                }
            }
        }

        // 3. Paired Devices List
        item {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp, vertical = 10.dp)
            ) {
                Text(
                    text = "Paired Devices (${devices.size})",
                    style = MaterialTheme.typography.titleMedium.copy(
                        fontWeight = FontWeight.Bold,
                        color = TextPrimaryDark
                    )
                )
                Spacer(modifier = Modifier.height(8.dp))

                devices.forEach { dev ->
                    val isSelected = dev.id == activeDevice.id
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = 8.dp)
                            .border(
                                width = if (isSelected) 1.5.dp else 0.dp,
                                color = if (isSelected) EmeraldPrimary else Color.Transparent,
                                shape = RoundedCornerShape(14.dp)
                            )
                            .clickable { viewModel.wearableSyncManager.selectActiveDevice(dev.id) },
                        colors = CardDefaults.cardColors(containerColor = DarkNavyElevated),
                        shape = RoundedCornerShape(14.dp)
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(14.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Box(
                                    modifier = Modifier
                                        .size(38.dp)
                                        .clip(CircleShape)
                                        .background(if (dev.isConnected) EmeraldPrimary.copy(alpha = 0.2f) else Color.Gray.copy(alpha = 0.2f)),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Watch,
                                        contentDescription = null,
                                        tint = if (dev.isConnected) EmeraldPrimary else Color.Gray,
                                        modifier = Modifier.size(20.dp)
                                    )
                                }
                                Spacer(modifier = Modifier.width(12.dp))
                                Column {
                                    Text(
                                        text = dev.name,
                                        style = MaterialTheme.typography.bodyMedium.copy(
                                            fontWeight = FontWeight.Bold,
                                            color = TextPrimaryDark
                                        )
                                    )
                                    Text(
                                        text = "${dev.brand} • Battery ${dev.batteryPct}%",
                                        style = MaterialTheme.typography.labelSmall.copy(color = TextSecondaryDark)
                                    )
                                }
                            }

                            OutlinedButton(
                                onClick = { viewModel.wearableSyncManager.toggleDeviceConnection(dev.id) },
                                shape = RoundedCornerShape(8.dp),
                                contentPadding = PaddingValues(horizontal = 10.dp, vertical = 4.dp),
                                modifier = Modifier.height(34.dp)
                            ) {
                                Text(
                                    text = if (dev.isConnected) "Disconnect" else "Connect",
                                    style = MaterialTheme.typography.labelSmall.copy(
                                        color = if (dev.isConnected) RosePulse else EmeraldPrimary,
                                        fontWeight = FontWeight.Bold
                                    )
                                )
                            }
                        }
                    }
                }
            }
        }

        // 4. Background Sync Preferences
        item {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp, vertical = 6.dp),
                colors = CardDefaults.cardColors(containerColor = DarkNavyElevated),
                shape = RoundedCornerShape(16.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = "Seamless Sync Preferences",
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontWeight = FontWeight.Bold,
                            color = TextPrimaryDark
                        )
                    )
                    Spacer(modifier = Modifier.height(12.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text("Automatic Periodic BLE Sync", style = MaterialTheme.typography.bodyMedium.copy(color = TextPrimaryDark, fontWeight = FontWeight.SemiBold))
                            Text("Syncs steps and active calories every 15 minutes in background", style = MaterialTheme.typography.labelSmall.copy(color = TextSecondaryDark))
                        }
                        Switch(
                            checked = autoSyncEnabled,
                            onCheckedChange = { autoSyncEnabled = it },
                            colors = SwitchDefaults.colors(checkedThumbColor = EmeraldPrimary)
                        )
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text("Elevated Heart Rate Alerts", style = MaterialTheme.typography.bodyMedium.copy(color = TextPrimaryDark, fontWeight = FontWeight.SemiBold))
                            Text("Vibrate watch if heart rate exceeds 160 bpm at rest", style = MaterialTheme.typography.labelSmall.copy(color = TextSecondaryDark))
                        }
                        Switch(
                            checked = highHrAlertEnabled,
                            onCheckedChange = { highHrAlertEnabled = it },
                            colors = SwitchDefaults.colors(checkedThumbColor = EmeraldPrimary)
                        )
                    }
                }
            }
        }
    }
}
