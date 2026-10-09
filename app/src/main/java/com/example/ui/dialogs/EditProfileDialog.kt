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
import androidx.compose.material.icons.filled.Person
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
import com.example.model.ActivityLevel
import com.example.model.FitnessGoal
import com.example.model.UserProfile
import com.example.ui.theme.DarkNavyElevated
import com.example.ui.theme.DarkNavySurface
import com.example.ui.theme.EmeraldPrimary
import com.example.ui.theme.TextPrimaryDark
import com.example.ui.theme.TextSecondaryDark

@Composable
fun EditProfileDialog(
    currentProfile: UserProfile,
    onDismiss: () -> Unit,
    onSaveProfile: (
        weightKg: Double,
        heightCm: Double,
        activityLevel: ActivityLevel,
        goal: FitnessGoal,
        dailyStepGoal: Int,
        dailyWaterGoalMl: Int
    ) -> Unit
) {
    var weightStr by remember { mutableStateOf(currentProfile.weightKg.toString()) }
    var heightStr by remember { mutableStateOf(currentProfile.heightCm.toString()) }
    var stepGoalStr by remember { mutableStateOf(currentProfile.dailyStepGoal.toString()) }
    var waterGoalStr by remember { mutableStateOf(currentProfile.dailyWaterGoalMl.toString()) }

    var selectedActivityLevel by remember { mutableStateOf(currentProfile.activityLevel) }
    var selectedGoal by remember { mutableStateOf(currentProfile.goal) }

    Dialog(onDismissRequest = onDismiss) {
        Surface(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(24.dp))
                .testTag("edit_profile_dialog"),
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
                        text = "Fitness Goals & Profile",
                        style = MaterialTheme.typography.titleLarge.copy(
                            fontWeight = FontWeight.Bold,
                            color = TextPrimaryDark
                        )
                    )
                    IconButton(onClick = onDismiss, modifier = Modifier.testTag("close_profile_dialog_btn")) {
                        Icon(imageVector = Icons.Default.Close, contentDescription = "Close", tint = TextSecondaryDark)
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                Text(
                    text = "Primary Fitness Target",
                    style = MaterialTheme.typography.labelMedium.copy(color = TextSecondaryDark)
                )
                Spacer(modifier = Modifier.height(6.dp))
                LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    items(FitnessGoal.values()) { g ->
                        FilterChip(
                            selected = selectedGoal == g,
                            onClick = { selectedGoal = g },
                            label = { Text(g.displayName) },
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = EmeraldPrimary,
                                selectedLabelColor = DarkNavySurface
                            )
                        )
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                Text(
                    text = "Weekly Activity Level",
                    style = MaterialTheme.typography.labelMedium.copy(color = TextSecondaryDark)
                )
                Spacer(modifier = Modifier.height(6.dp))
                LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    items(ActivityLevel.values()) { level ->
                        FilterChip(
                            selected = selectedActivityLevel == level,
                            onClick = { selectedActivityLevel = level },
                            label = { Text(level.displayName.substringBefore(" (")) },
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = EmeraldPrimary,
                                selectedLabelColor = DarkNavySurface
                            )
                        )
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    OutlinedTextField(
                        value = weightStr,
                        onValueChange = { weightStr = it },
                        label = { Text("Weight (kg)") },
                        modifier = Modifier.weight(1f).testTag("profile_weight_input"),
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                        singleLine = true
                    )
                    OutlinedTextField(
                        value = heightStr,
                        onValueChange = { heightStr = it },
                        label = { Text("Height (cm)") },
                        modifier = Modifier.weight(1f).testTag("profile_height_input"),
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                        singleLine = true
                    )
                }

                Spacer(modifier = Modifier.height(8.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    OutlinedTextField(
                        value = stepGoalStr,
                        onValueChange = { stepGoalStr = it },
                        label = { Text("Daily Steps") },
                        modifier = Modifier.weight(1f).testTag("profile_steps_input"),
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        singleLine = true
                    )
                    OutlinedTextField(
                        value = waterGoalStr,
                        onValueChange = { waterGoalStr = it },
                        label = { Text("Water (ml)") },
                        modifier = Modifier.weight(1f).testTag("profile_water_input"),
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        singleLine = true
                    )
                }

                Spacer(modifier = Modifier.height(18.dp))

                Button(
                    onClick = {
                        val w = weightStr.toDoubleOrNull() ?: currentProfile.weightKg
                        val h = heightStr.toDoubleOrNull() ?: currentProfile.heightCm
                        val steps = stepGoalStr.toIntOrNull() ?: currentProfile.dailyStepGoal
                        val water = waterGoalStr.toIntOrNull() ?: currentProfile.dailyWaterGoalMl
                        onSaveProfile(w, h, selectedActivityLevel, selectedGoal, steps, water)
                        onDismiss()
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(48.dp)
                        .testTag("save_profile_btn"),
                    colors = ButtonDefaults.buttonColors(containerColor = EmeraldPrimary),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Icon(imageVector = Icons.Default.Person, contentDescription = null, tint = DarkNavySurface)
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("Update Goals & Recalculate TDEE", color = DarkNavySurface, fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}
