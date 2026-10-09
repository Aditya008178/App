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
import androidx.compose.material.icons.filled.Alarm
import androidx.compose.material.icons.filled.Close
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
import androidx.compose.runtime.mutableIntStateOf
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
import com.example.ui.theme.TextPrimaryDark
import com.example.ui.theme.TextSecondaryDark

@Composable
fun AddReminderDialog(
    onDismiss: () -> Unit,
    onSaveReminder: (
        title: String,
        category: String,
        hour: Int,
        minute: Int,
        daysOfWeek: String,
        customMessage: String
    ) -> Unit
) {
    val categories = listOf("Meal", "Water", "Workout", "Sleep", "Stretch")
    val dayOptions = listOf("Everyday", "Weekdays", "Weekends")

    var selectedCategory by remember { mutableStateOf("Meal") }
    var selectedDays by remember { mutableStateOf("Everyday") }
    var title by remember { mutableStateOf("Post-Workout Protein Shake") }
    var hourStr by remember { mutableStateOf("15") }
    var minStr by remember { mutableStateOf("30") }
    var message by remember { mutableStateOf("Time for 30g protein replenishment & 500ml water!") }

    Dialog(onDismissRequest = onDismiss) {
        Surface(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(24.dp))
                .testTag("add_reminder_dialog"),
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
                        text = "Set Custom Reminder",
                        style = MaterialTheme.typography.titleLarge.copy(
                            fontWeight = FontWeight.Bold,
                            color = TextPrimaryDark
                        )
                    )
                    IconButton(onClick = onDismiss, modifier = Modifier.testTag("close_reminder_dialog_btn")) {
                        Icon(imageVector = Icons.Default.Close, contentDescription = "Close", tint = TextSecondaryDark)
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                Text(
                    text = "Category",
                    style = MaterialTheme.typography.labelMedium.copy(color = TextSecondaryDark)
                )
                Spacer(modifier = Modifier.height(6.dp))
                LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    items(categories) { cat ->
                        FilterChip(
                            selected = selectedCategory == cat,
                            onClick = {
                                selectedCategory = cat
                                when (cat) {
                                    "Meal" -> {
                                        title = "Afternoon Macro Fuel"
                                        message = "Log your clean snack to meet protein synthesis goals."
                                    }
                                    "Water" -> {
                                        title = "Hydration Interval"
                                        message = "Drink 300ml water to maintain energy balance."
                                    }
                                    "Workout" -> {
                                        title = "Daily Training Session"
                                        message = "Time to crush your active calories and close your rings!"
                                    }
                                    "Sleep" -> {
                                        title = "Sleep Wind-down"
                                        message = "Prepare for 8 hours of restorative deep sleep."
                                    }
                                }
                            },
                            label = { Text(cat) },
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = EmeraldPrimary,
                                selectedLabelColor = DarkNavySurface
                            )
                        )
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                OutlinedTextField(
                    value = title,
                    onValueChange = { title = it },
                    label = { Text("Reminder Title") },
                    modifier = Modifier.fillMaxWidth().testTag("reminder_title_input"),
                    colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = EmeraldPrimary),
                    singleLine = true
                )

                Spacer(modifier = Modifier.height(10.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    OutlinedTextField(
                        value = hourStr,
                        onValueChange = { hourStr = it },
                        label = { Text("Hour (0-23)") },
                        modifier = Modifier.weight(1f).testTag("reminder_hour_input"),
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = EmeraldPrimary),
                        singleLine = true
                    )
                    OutlinedTextField(
                        value = minStr,
                        onValueChange = { minStr = it },
                        label = { Text("Minute (0-59)") },
                        modifier = Modifier.weight(1f).testTag("reminder_minute_input"),
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = EmeraldPrimary),
                        singleLine = true
                    )
                }

                Spacer(modifier = Modifier.height(10.dp))

                Text(
                    text = "Repeat Schedule",
                    style = MaterialTheme.typography.labelMedium.copy(color = TextSecondaryDark)
                )
                Spacer(modifier = Modifier.height(6.dp))
                LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    items(dayOptions) { opt ->
                        FilterChip(
                            selected = selectedDays == opt,
                            onClick = { selectedDays = opt },
                            label = { Text(opt) },
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = EmeraldPrimary,
                                selectedLabelColor = DarkNavySurface
                            )
                        )
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                OutlinedTextField(
                    value = message,
                    onValueChange = { message = it },
                    label = { Text("Custom Notification Text") },
                    modifier = Modifier.fillMaxWidth().testTag("reminder_message_input"),
                    maxLines = 2
                )

                Spacer(modifier = Modifier.height(16.dp))

                Button(
                    onClick = {
                        val h = (hourStr.toIntOrNull() ?: 12).coerceIn(0, 23)
                        val m = (minStr.toIntOrNull() ?: 0).coerceIn(0, 59)
                        if (title.isNotBlank()) {
                            onSaveReminder(title.trim(), selectedCategory, h, m, selectedDays, message.trim())
                            onDismiss()
                        }
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(48.dp)
                        .testTag("save_reminder_btn"),
                    colors = ButtonDefaults.buttonColors(containerColor = EmeraldPrimary),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Icon(imageVector = Icons.Default.Alarm, contentDescription = null, tint = DarkNavySurface)
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("Save Reminder", color = DarkNavySurface, fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}
