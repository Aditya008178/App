package com.example.ui.screens

import androidx.compose.foundation.background
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
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Restaurant
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.data.entities.MealLogEntity
import com.example.ui.components.MacroProgressBar
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
fun MealsScreen(
    viewModel: FitPulseViewModel,
    onOpenAddMealForType: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    val meals by viewModel.todayMeals.collectAsStateWithLifecycle()
    val nutrition by viewModel.nutritionSummary.collectAsStateWithLifecycle()

    val mealTypes = listOf("Breakfast", "Lunch", "Dinner", "Snack")

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .testTag("meals_screen"),
        contentPadding = PaddingValues(bottom = 24.dp)
    ) {
        // 1. Header
        item {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp, vertical = 14.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = "Nutrition & Meal Intake",
                            style = MaterialTheme.typography.titleLarge.copy(
                                fontWeight = FontWeight.ExtraBold,
                                color = TextPrimaryDark
                            )
                        )
                        Text(
                            text = "Calorie pacing and macronutrient breakdown",
                            style = MaterialTheme.typography.bodySmall.copy(color = TextSecondaryDark)
                        )
                    }

                    Button(
                        onClick = { onOpenAddMealForType("Lunch") },
                        modifier = Modifier.testTag("add_meal_header_btn"),
                        colors = ButtonDefaults.buttonColors(containerColor = EmeraldPrimary),
                        shape = RoundedCornerShape(10.dp),
                        contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp)
                    ) {
                        Icon(imageVector = Icons.Default.Add, contentDescription = null, tint = DarkNavySurface)
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Add Food", color = DarkNavySurface, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }

        // 2. Nutrition Analysis Overview Card
        item {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp, vertical = 6.dp)
                    .testTag("nutrition_summary_card"),
                colors = CardDefaults.cardColors(containerColor = DarkNavySurface),
                shape = RoundedCornerShape(20.dp)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(20.dp)
                ) {
                    // Calorie Budget Row
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text(
                                text = "CALORIC ENERGY BALANCE",
                                style = MaterialTheme.typography.labelSmall.copy(
                                    color = TextSecondaryDark,
                                    letterSpacing = 1.sp
                                )
                            )
                            Row(verticalAlignment = Alignment.Bottom) {
                                Text(
                                    text = "${nutrition.totalCalories}",
                                    style = MaterialTheme.typography.headlineMedium.copy(
                                        fontWeight = FontWeight.Bold,
                                        color = TextPrimaryDark
                                    )
                                )
                                Text(
                                    text = " / ${nutrition.targetCalories} kcal",
                                    modifier = Modifier.padding(bottom = 4.dp, start = 4.dp),
                                    style = MaterialTheme.typography.bodyMedium.copy(color = TextSecondaryDark)
                                )
                            }
                        }

                        // Deficit / Surplus Pill
                        val remaining = nutrition.targetCalories - nutrition.totalCalories
                        Surface(
                            color = if (remaining >= 0) EmeraldPrimary.copy(alpha = 0.2f) else RosePulse.copy(alpha = 0.2f),
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Text(
                                text = if (remaining >= 0) "$remaining kcal left" else "${-remaining} kcal surplus",
                                modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                                style = MaterialTheme.typography.labelMedium.copy(
                                    color = if (remaining >= 0) EmeraldPrimary else RosePulse,
                                    fontWeight = FontWeight.Bold
                                )
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    // Macronutrient Progress Bars
                    MacroProgressBar(
                        label = "Protein",
                        currentValue = nutrition.totalProtein,
                        targetValue = nutrition.targetProtein,
                        barColor = CyanSecondary,
                        modifier = Modifier.padding(bottom = 10.dp)
                    )

                    MacroProgressBar(
                        label = "Carbohydrates",
                        currentValue = nutrition.totalCarbs,
                        targetValue = nutrition.targetCarbs,
                        barColor = EmeraldPrimary,
                        modifier = Modifier.padding(bottom = 10.dp)
                    )

                    MacroProgressBar(
                        label = "Fats",
                        currentValue = nutrition.totalFat,
                        targetValue = nutrition.targetFat,
                        barColor = OrangeFlame,
                        modifier = Modifier.padding(bottom = 10.dp)
                    )

                    Spacer(modifier = Modifier.height(6.dp))

                    // Macro Ratio Breakdown Strip
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            text = "Ratio: ${nutrition.macroRatioProtein}% P • ${nutrition.macroRatioCarbs}% C • ${nutrition.macroRatioFat}% F",
                            style = MaterialTheme.typography.labelSmall.copy(
                                color = TextSecondaryDark,
                                fontWeight = FontWeight.Medium
                            )
                        )
                        Text(
                            text = "Dietary Fiber: ${nutrition.totalFiber.toInt()}g",
                            style = MaterialTheme.typography.labelSmall.copy(
                                color = EmeraldPrimary,
                                fontWeight = FontWeight.Bold
                            )
                        )
                    }
                }
            }
        }

        // 3. Meal Category Sections
        items(mealTypes) { mealType ->
            val mealsInType = meals.filter { it.mealType.equals(mealType, ignoreCase = true) }
            val calsInType = mealsInType.sumOf { it.calories }

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp, vertical = 8.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = mealType,
                            style = MaterialTheme.typography.titleMedium.copy(
                                fontWeight = FontWeight.Bold,
                                color = TextPrimaryDark
                            )
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "($calsInType kcal)",
                            style = MaterialTheme.typography.bodySmall.copy(
                                color = OrangeFlame,
                                fontWeight = FontWeight.SemiBold
                            )
                        )
                    }

                    IconButton(
                        onClick = { onOpenAddMealForType(mealType) },
                        modifier = Modifier
                            .size(32.dp)
                            .testTag("add_meal_to_$mealType")
                    ) {
                        Icon(
                            imageVector = Icons.Default.Add,
                            contentDescription = "Add to $mealType",
                            tint = EmeraldPrimary,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(6.dp))

                if (mealsInType.isEmpty()) {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(containerColor = DarkNavyElevated.copy(alpha = 0.5f)),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Text(
                            text = "No $mealType logged yet. Tap + to add items.",
                            modifier = Modifier.padding(14.dp),
                            style = MaterialTheme.typography.bodySmall.copy(color = TextSecondaryDark)
                        )
                    }
                } else {
                    mealsInType.forEach { item ->
                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(bottom = 6.dp)
                                .testTag("meal_item_${item.id}"),
                            colors = CardDefaults.cardColors(containerColor = DarkNavyElevated),
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(horizontal = 14.dp, vertical = 10.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Column(modifier = Modifier.weight(1f)) {
                                    Text(
                                        text = item.foodName,
                                        style = MaterialTheme.typography.bodyMedium.copy(
                                            fontWeight = FontWeight.Bold,
                                            color = TextPrimaryDark
                                        )
                                    )
                                    Text(
                                        text = "${item.portion} • P: ${item.proteinG}g • C: ${item.carbsG}g • F: ${item.fatG}g",
                                        style = MaterialTheme.typography.labelSmall.copy(color = TextSecondaryDark)
                                    )
                                }

                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Text(
                                        text = "${item.calories} kcal",
                                        style = MaterialTheme.typography.bodyMedium.copy(
                                            fontWeight = FontWeight.Bold,
                                            color = OrangeFlame
                                        )
                                    )
                                    IconButton(
                                        onClick = { viewModel.deleteMeal(item) },
                                        modifier = Modifier.size(28.dp).testTag("delete_meal_${item.id}")
                                    ) {
                                        Icon(
                                            imageVector = Icons.Default.Delete,
                                            contentDescription = "Delete",
                                            tint = TextSecondaryDark,
                                            modifier = Modifier.size(16.dp)
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
