package com.example.ui.dialogs

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
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
import com.example.model.FoodPresets
import com.example.ui.theme.CyanSecondary
import com.example.ui.theme.DarkNavyElevated
import com.example.ui.theme.DarkNavySurface
import com.example.ui.theme.EmeraldPrimary
import com.example.ui.theme.OrangeFlame
import com.example.ui.theme.TextPrimaryDark
import com.example.ui.theme.TextSecondaryDark

@Composable
fun AddMealDialog(
    initialMealType: String = "Lunch",
    onDismiss: () -> Unit,
    onSaveMeal: (
        mealType: String,
        foodName: String,
        portion: String,
        calories: Int,
        protein: Double,
        carbs: Double,
        fat: Double,
        fiber: Double
    ) -> Unit
) {
    val mealTypes = listOf("Breakfast", "Lunch", "Dinner", "Snack")
    var selectedMealType by remember { mutableStateOf(initialMealType) }
    var selectedTabMode by remember { mutableIntStateOf(0) } // 0: Quick Presets, 1: Custom Entry

    var foodName by remember { mutableStateOf("") }
    var portion by remember { mutableStateOf("1 serving") }
    var caloriesStr by remember { mutableStateOf("") }
    var proteinStr by remember { mutableStateOf("") }
    var carbsStr by remember { mutableStateOf("") }
    var fatStr by remember { mutableStateOf("") }
    var fiberStr by remember { mutableStateOf("") }

    Dialog(onDismissRequest = onDismiss) {
        Surface(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(24.dp))
                .testTag("add_meal_dialog"),
            color = DarkNavySurface
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(20.dp)
            ) {
                // Header
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Log Meal & Nutrition",
                        style = MaterialTheme.typography.titleLarge.copy(
                            fontWeight = FontWeight.Bold,
                            color = TextPrimaryDark
                        )
                    )
                    IconButton(onClick = onDismiss, modifier = Modifier.testTag("close_meal_dialog_btn")) {
                        Icon(imageVector = Icons.Default.Close, contentDescription = "Close", tint = TextSecondaryDark)
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                // Meal Type selector chips
                LazyRow(
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(mealTypes) { type ->
                        FilterChip(
                            selected = selectedMealType == type,
                            onClick = { selectedMealType = type },
                            label = { Text(type) },
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = EmeraldPrimary,
                                selectedLabelColor = DarkNavySurface
                            )
                        )
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                // Tab Switch: Presets vs Custom
                TabRow(
                    selectedTabIndex = selectedTabMode,
                    containerColor = DarkNavyElevated,
                    contentColor = EmeraldPrimary,
                    modifier = Modifier.clip(RoundedCornerShape(12.dp))
                ) {
                    Tab(
                        selected = selectedTabMode == 0,
                        onClick = { selectedTabMode = 0 },
                        text = { Text("Quick Database", fontWeight = FontWeight.SemiBold) }
                    )
                    Tab(
                        selected = selectedTabMode == 1,
                        onClick = { selectedTabMode = 1 },
                        text = { Text("Custom Entry", fontWeight = FontWeight.SemiBold) }
                    )
                }

                Spacer(modifier = Modifier.height(14.dp))

                if (selectedTabMode == 0) {
                    // Quick Food Presets list
                    Text(
                        text = "Tap a food item to log instantly:",
                        style = MaterialTheme.typography.bodySmall.copy(color = TextSecondaryDark)
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    LazyColumn(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(260.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        items(FoodPresets.items) { item ->
                            Card(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clickable {
                                        onSaveMeal(
                                            selectedMealType,
                                            item.name,
                                            item.portion,
                                            item.calories,
                                            item.protein,
                                            item.carbs,
                                            item.fat,
                                            item.fiber
                                        )
                                        onDismiss()
                                    },
                                colors = CardDefaults.cardColors(containerColor = DarkNavyElevated),
                                shape = RoundedCornerShape(12.dp)
                            ) {
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(12.dp),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Column(modifier = Modifier.weight(1f)) {
                                        Text(
                                            text = item.name,
                                            style = MaterialTheme.typography.bodyMedium.copy(
                                                fontWeight = FontWeight.Bold,
                                                color = TextPrimaryDark
                                            )
                                        )
                                        Text(
                                            text = "${item.portion} • P: ${item.protein}g • C: ${item.carbs}g • F: ${item.fat}g",
                                            style = MaterialTheme.typography.labelSmall.copy(
                                                color = TextSecondaryDark
                                            )
                                        )
                                    }
                                    Surface(
                                        color = OrangeFlame.copy(alpha = 0.2f),
                                        shape = RoundedCornerShape(8.dp)
                                    ) {
                                        Text(
                                            text = "${item.calories} kcal",
                                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                                            style = MaterialTheme.typography.labelSmall.copy(
                                                color = OrangeFlame,
                                                fontWeight = FontWeight.Bold
                                            )
                                        )
                                    }
                                }
                            }
                        }
                    }
                } else {
                    // Custom Entry form
                    OutlinedTextField(
                        value = foodName,
                        onValueChange = { foodName = it },
                        label = { Text("Food / Recipe Name") },
                        modifier = Modifier.fillMaxWidth().testTag("food_name_input"),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = EmeraldPrimary,
                            unfocusedBorderColor = DarkNavyElevated
                        ),
                        singleLine = true
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    OutlinedTextField(
                        value = portion,
                        onValueChange = { portion = it },
                        label = { Text("Portion Size (e.g. 200g, 1 bowl)") },
                        modifier = Modifier.fillMaxWidth().testTag("portion_input"),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = EmeraldPrimary,
                            unfocusedBorderColor = DarkNavyElevated
                        ),
                        singleLine = true
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        OutlinedTextField(
                            value = caloriesStr,
                            onValueChange = { caloriesStr = it },
                            label = { Text("Calories") },
                            modifier = Modifier.weight(1f).testTag("calories_input"),
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                            colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = OrangeFlame),
                            singleLine = true
                        )
                        OutlinedTextField(
                            value = proteinStr,
                            onValueChange = { proteinStr = it },
                            label = { Text("Protein (g)") },
                            modifier = Modifier.weight(1f).testTag("protein_input"),
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                            colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = CyanSecondary),
                            singleLine = true
                        )
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        OutlinedTextField(
                            value = carbsStr,
                            onValueChange = { carbsStr = it },
                            label = { Text("Carbs (g)") },
                            modifier = Modifier.weight(1f).testTag("carbs_input"),
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                            colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = EmeraldPrimary),
                            singleLine = true
                        )
                        OutlinedTextField(
                            value = fatStr,
                            onValueChange = { fatStr = it },
                            label = { Text("Fats (g)") },
                            modifier = Modifier.weight(1f).testTag("fats_input"),
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                            colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = OrangeFlame),
                            singleLine = true
                        )
                        OutlinedTextField(
                            value = fiberStr,
                            onValueChange = { fiberStr = it },
                            label = { Text("Fiber (g)") },
                            modifier = Modifier.weight(1f).testTag("fiber_input"),
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                            singleLine = true
                        )
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    Button(
                        onClick = {
                            if (foodName.isNotBlank()) {
                                onSaveMeal(
                                    selectedMealType,
                                    foodName.trim(),
                                    portion.ifBlank { "1 serving" },
                                    caloriesStr.toIntOrNull() ?: 200,
                                    proteinStr.toDoubleOrNull() ?: 15.0,
                                    carbsStr.toDoubleOrNull() ?: 20.0,
                                    fatStr.toDoubleOrNull() ?: 5.0,
                                    fiberStr.toDoubleOrNull() ?: 2.0
                                )
                                onDismiss()
                            }
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(48.dp)
                            .testTag("save_custom_meal_btn"),
                        colors = ButtonDefaults.buttonColors(containerColor = EmeraldPrimary),
                        shape = RoundedCornerShape(12.dp),
                        enabled = foodName.isNotBlank()
                    ) {
                        Icon(imageVector = Icons.Default.Add, contentDescription = null, tint = DarkNavySurface)
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("Log Meal Item", color = DarkNavySurface, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    }
}
