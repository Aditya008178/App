package com.example.ui.dialogs

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.FitnessCenter
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import com.example.ui.theme.DarkNavyElevated
import com.example.ui.theme.DarkNavySurface
import com.example.ui.theme.EmeraldPrimary
import com.example.ui.theme.OrangeFlame
import com.example.ui.theme.TextPrimaryDark
import com.example.ui.theme.TextSecondaryDark

@Composable
fun AddWorkoutDialog(
    onDismiss: () -> Unit,
    onSaveWorkout: (
        workoutType: String,
        durationMinutes: Int,
        caloriesBurned: Int,
        avgHeartRate: Int,
        distanceKm: Double,
        intensity: String
    ) -> Unit
) {
    val workoutTypes = listOf(
        "Strength Training",
        "Outdoor Run",
        "HIIT & Cardio",
        "Cycling",
        "Brisk Walking",
        "Yoga / Flow",
        "Swimming"
    )
    val intensities = listOf("Low", "Moderate", "High", "Extreme")

    var selectedType by remember { mutableStateOf(workoutTypes[0]) }
    var selectedIntensity by remember { mutableStateOf("Moderate") }
    var durationStr by remember { mutableStateOf("30") }
    var caloriesStr by remember { mutableStateOf("250") }
    var heartRateStr by remember { mutableStateOf("138") }
    var distanceStr by remember { mutableStateOf("0.0") }

    Dialog(onDismissRequest = onDismiss) {
        Surface(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(24.dp))
                .testTag("add_workout_dialog"),
            color = DarkNavySurface
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
                    Text(
                        text = "Log Workout Activity",
                        style = MaterialTheme.typography.titleLarge.copy(
                            fontWeight = FontWeight.Bold,
                            color = TextPrimaryDark
                        )
                    )
                    IconButton(onClick = onDismiss, modifier = Modifier.testTag("close_workout_dialog_btn")) {
                        Icon(imageVector = Icons.Default.Close, contentDescription = "Close", tint = TextSecondaryDark)
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                Text(
                    text = "Activity Type",
                    style = MaterialTheme.typography.labelMedium.copy(color = TextSecondaryDark)
                )
                Spacer(modifier = Modifier.height(6.dp))
                LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    items(workoutTypes) { type ->
                        FilterChip(
                            selected = selectedType == type,
                            onClick = {
                                selectedType = type
                                if (type == "Outdoor Run") {
                                    caloriesStr = "320"
                                    distanceStr = "4.5"
                                } else if (type == "Strength Training") {
                                    caloriesStr = "280"
                                    distanceStr = "0.0"
                                }
                            },
                            label = { Text(type) },
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = OrangeFlame,
                                selectedLabelColor = DarkNavySurface
                            )
                        )
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    OutlinedTextField(
                        value = durationStr,
                        onValueChange = { durationStr = it },
                        label = { Text("Duration (min)") },
                        modifier = Modifier.weight(1f).testTag("workout_duration_input"),
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = EmeraldPrimary),
                        singleLine = true
                    )
                    OutlinedTextField(
                        value = caloriesStr,
                        onValueChange = { caloriesStr = it },
                        label = { Text("Burned (kcal)") },
                        modifier = Modifier.weight(1f).testTag("workout_calories_input"),
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = OrangeFlame),
                        singleLine = true
                    )
                }

                Spacer(modifier = Modifier.height(10.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    OutlinedTextField(
                        value = heartRateStr,
                        onValueChange = { heartRateStr = it },
                        label = { Text("Avg Heart Rate") },
                        modifier = Modifier.weight(1f).testTag("workout_hr_input"),
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        singleLine = true
                    )
                    OutlinedTextField(
                        value = distanceStr,
                        onValueChange = { distanceStr = it },
                        label = { Text("Distance (km)") },
                        modifier = Modifier.weight(1f).testTag("workout_dist_input"),
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                        singleLine = true
                    )
                }

                Spacer(modifier = Modifier.height(12.dp))

                Text(
                    text = "Intensity Level",
                    style = MaterialTheme.typography.labelMedium.copy(color = TextSecondaryDark)
                )
                Spacer(modifier = Modifier.height(6.dp))
                LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    items(intensities) { level ->
                        FilterChip(
                            selected = selectedIntensity == level,
                            onClick = { selectedIntensity = level },
                            label = { Text(level) },
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = EmeraldPrimary,
                                selectedLabelColor = DarkNavySurface
                            )
                        )
                    }
                }

                Spacer(modifier = Modifier.height(20.dp))

                Button(
                    onClick = {
                        val duration = durationStr.toIntOrNull() ?: 30
                        val calories = caloriesStr.toIntOrNull() ?: 250
                        val hr = heartRateStr.toIntOrNull() ?: 135
                        val dist = distanceStr.toDoubleOrNull() ?: 0.0
                        onSaveWorkout(selectedType, duration, calories, hr, dist, selectedIntensity)
                        onDismiss()
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(48.dp)
                        .testTag("save_workout_btn"),
                    colors = ButtonDefaults.buttonColors(containerColor = OrangeFlame),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Icon(imageVector = Icons.Default.FitnessCenter, contentDescription = null, tint = DarkNavySurface)
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("Record Workout", color = DarkNavySurface, fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}
