package com.example.model

enum class ActivityLevel(val displayName: String, val multiplier: Double) {
    SEDENTARY("Sedentary (Little or no exercise)", 1.2),
    LIGHT("Lightly Active (1-3 days/wk)", 1.375),
    MODERATE("Moderately Active (3-5 days/wk)", 1.55),
    VERY_ACTIVE("Very Active (6-7 days/wk)", 1.725),
    ATHLETE("Athletic / Heavy Training (2x per day)", 1.9)
}

enum class FitnessGoal(val displayName: String, val calorieAdjustment: Int) {
    FAT_LOSS("Lean Fat Loss", -450),
    MAINTENANCE("Healthy Maintenance", 0),
    MUSCLE_GAIN("Clean Muscle Gain", 350)
}

data class UserProfile(
    val name: String = "Alex Rivera",
    val age: Int = 28,
    val gender: String = "Male", // Male / Female
    val weightKg: Double = 74.0,
    val heightCm: Double = 178.0,
    val activityLevel: ActivityLevel = ActivityLevel.MODERATE,
    val goal: FitnessGoal = FitnessGoal.FAT_LOSS,
    val dailyStepGoal: Int = 10000,
    val dailyWaterGoalMl: Int = 2600,
    val dailySleepGoalHours: Double = 8.0
) {
    // Mifflin-St Jeor BMR Equation
    val bmr: Int
        get() {
            val base = (10 * weightKg) + (6.25 * heightCm) - (5 * age)
            return if (gender.equals("Female", ignoreCase = true)) {
                (base - 161).toInt()
            } else {
                (base + 5).toInt()
            }
        }

    // Total Daily Energy Expenditure (TDEE)
    val tdee: Int
        get() = (bmr * activityLevel.multiplier).toInt()

    val targetDailyCalories: Int
        get() = tdee + goal.calorieAdjustment

    // Macro targets based on goal
    val targetProteinG: Int
        get() = (weightKg * when (goal) {
            FitnessGoal.FAT_LOSS -> 2.0
            FitnessGoal.MUSCLE_GAIN -> 2.2
            FitnessGoal.MAINTENANCE -> 1.7
        }).toInt()

    val targetFatG: Int
        get() = ((targetDailyCalories * 0.25) / 9.0).toInt()

    val targetCarbsG: Int
        get() {
            val proteinCals = targetProteinG * 4
            val fatCals = targetFatG * 9
            val remainingCals = (targetDailyCalories - proteinCals - fatCals).coerceAtLeast(100)
            return (remainingCals / 4.0).toInt()
        }
}

data class NutritionSummary(
    val totalCalories: Int,
    val totalProtein: Double,
    val totalCarbs: Double,
    val totalFat: Double,
    val totalFiber: Double,
    val targetCalories: Int,
    val targetProtein: Int,
    val targetCarbs: Int,
    val targetFat: Int
) {
    val calorieDeficitOrSurplus: Int
        get() = totalCalories - targetCalories

    val proteinPercent: Float
        get() = if (targetProtein > 0) (totalProtein.toFloat() / targetProtein).coerceIn(0f, 1f) else 0f

    val carbsPercent: Float
        get() = if (targetCarbs > 0) (totalCarbs.toFloat() / targetCarbs).coerceIn(0f, 1f) else 0f

    val fatPercent: Float
        get() = if (targetFat > 0) (totalFat.toFloat() / targetFat).coerceIn(0f, 1f) else 0f

    val macroRatioProtein: Int
        get() {
            val totalGrams = (totalProtein + totalCarbs + totalFat).coerceAtLeast(1.0)
            return ((totalProtein / totalGrams) * 100).toInt()
        }

    val macroRatioCarbs: Int
        get() {
            val totalGrams = (totalProtein + totalCarbs + totalFat).coerceAtLeast(1.0)
            return ((totalCarbs / totalGrams) * 100).toInt()
        }

    val macroRatioFat: Int
        get() {
            val totalGrams = (totalProtein + totalCarbs + totalFat).coerceAtLeast(1.0)
            return ((totalFat / totalGrams) * 100).toInt()
        }
}

enum class InsightType {
    OPTIMAL, WARNING, RECOMMENDATION, RECOVERY
}

data class HealthInsight(
    val id: String,
    val title: String,
    val description: String,
    val type: InsightType,
    val tag: String,
    val actionSuggestion: String
)

data class WearableDevice(
    val id: String,
    val name: String,
    val brand: String, // Pixel Watch, Garmin, Apple Watch, Galaxy Watch, Fitbit, Whoop
    val isConnected: Boolean,
    val batteryPct: Int,
    val lastSyncFormatted: String,
    val currentHeartRateBpm: Int,
    val restingHeartRateBpm: Int,
    val stepsToday: Int,
    val activeBurnedKcal: Int,
    val hrvMs: Int,
    val signalStrengthDbm: Int = -58
)

data class FriendActivity(
    val id: String,
    val name: String,
    val initials: String,
    val avatarBgColorHex: Long,
    val activityText: String,
    val achievementBadge: String? = null,
    val stepsToday: Int,
    val timeAgo: String,
    var highFives: Int = 12,
    var isLikedByUser: Boolean = false
)

data class FoodPreset(
    val name: String,
    val portion: String,
    val mealType: String,
    val calories: Int,
    val protein: Double,
    val carbs: Double,
    val fat: Double,
    val fiber: Double = 0.0,
    val iconCategory: String = "meal"
)
