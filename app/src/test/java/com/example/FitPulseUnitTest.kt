package com.example

import com.example.model.ActivityLevel
import com.example.model.FitnessGoal
import com.example.model.NutritionSummary
import com.example.model.UserProfile
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class FitPulseUnitTest {

    @Test
    fun testBmrAndTdeeCalculation() {
        val profile = UserProfile(
            name = "Alex",
            age = 28,
            gender = "Male",
            weightKg = 74.0,
            heightCm = 178.0,
            activityLevel = ActivityLevel.MODERATE,
            goal = FitnessGoal.FAT_LOSS
        )

        // BMR: (10 * 74) + (6.25 * 178) - (5 * 28) + 5 = 740 + 1112.5 - 140 + 5 = 1717
        assertEquals(1717, profile.bmr)

        // TDEE: 1717 * 1.55 = 2661
        assertEquals(2661, profile.tdee)

        // Target daily calories for FAT_LOSS (-450 kcal): 2661 - 450 = 2211
        assertEquals(2211, profile.targetDailyCalories)

        // Target protein (2.0g per kg for fat loss): 74 * 2.0 = 148g
        assertEquals(148, profile.targetProteinG)
    }

    @Test
    fun testNutritionSummaryMacroPercentages() {
        val summary = NutritionSummary(
            totalCalories = 2000,
            totalProtein = 150.0,
            totalCarbs = 200.0,
            totalFat = 65.0,
            totalFiber = 28.0,
            targetCalories = 2200,
            targetProtein = 150,
            targetCarbs = 250,
            targetFat = 70
        )

        assertEquals(1.0f, summary.proteinPercent, 0.01f)
        assertEquals(0.8f, summary.carbsPercent, 0.01f)
        assertTrue(summary.macroRatioProtein > 0)
        assertTrue(summary.macroRatioCarbs > 0)
        assertTrue(summary.macroRatioFat > 0)
    }
}
