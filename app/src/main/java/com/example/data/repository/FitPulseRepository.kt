package com.example.data.repository

import com.example.data.dao.FitPulseDao
import com.example.data.entities.AchievementEntity
import com.example.data.entities.DailyMetricEntity
import com.example.data.entities.MealLogEntity
import com.example.data.entities.ReminderEntity
import com.example.data.entities.WorkoutEntity
import kotlinx.coroutines.flow.Flow
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

class FitPulseRepository(private val dao: FitPulseDao) {

    private val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())

    fun getTodayDate(): String = dateFormat.format(Date())

    fun getMealsForDate(date: String): Flow<List<MealLogEntity>> = dao.getMealsForDate(date)

    suspend fun insertMeal(meal: MealLogEntity): Long = dao.insertMeal(meal)

    suspend fun deleteMeal(meal: MealLogEntity) = dao.deleteMeal(meal)

    fun getWorkoutsForDate(date: String): Flow<List<WorkoutEntity>> = dao.getWorkoutsForDate(date)

    fun getAllWorkouts(): Flow<List<WorkoutEntity>> = dao.getAllWorkouts()

    suspend fun insertWorkout(workout: WorkoutEntity): Long = dao.insertWorkout(workout)

    suspend fun deleteWorkout(workout: WorkoutEntity) = dao.deleteWorkout(workout)

    fun getDailyMetric(date: String): Flow<DailyMetricEntity?> = dao.getDailyMetric(date)

    fun getRecentDailyMetrics(): Flow<List<DailyMetricEntity>> = dao.getRecentDailyMetrics()

    suspend fun upsertDailyMetric(metric: DailyMetricEntity) = dao.upsertDailyMetric(metric)

    fun getAllReminders(): Flow<List<ReminderEntity>> = dao.getAllReminders()

    suspend fun insertReminder(reminder: ReminderEntity): Long = dao.insertReminder(reminder)

    suspend fun updateReminder(reminder: ReminderEntity) = dao.updateReminder(reminder)

    suspend fun deleteReminder(reminder: ReminderEntity) = dao.deleteReminder(reminder)

    fun getAllAchievements(): Flow<List<AchievementEntity>> = dao.getAllAchievements()

    suspend fun updateAchievement(achievement: AchievementEntity) = dao.updateAchievement(achievement)

    suspend fun seedInitialDataIfEmpty() {
        if (dao.getAchievementCount() > 0) return

        val today = getTodayDate()

        // 1. Seed Achievements
        val achievements = listOf(
            AchievementEntity(
                id = "step_10k",
                title = "Century Walker",
                description = "Surpass 10,000 steps in a single day",
                badgeIcon = "directions_walk",
                category = "Steps",
                targetValue = 10000,
                currentProgress = 8420,
                isUnlocked = false,
                xpReward = 150
            ),
            AchievementEntity(
                id = "protein_crusher",
                title = "Protein Titan",
                description = "Hit 120g+ protein target for muscle repair",
                badgeIcon = "fitness_center",
                category = "Nutrition",
                targetValue = 120,
                currentProgress = 135,
                isUnlocked = true,
                unlockedDate = today,
                xpReward = 200
            ),
            AchievementEntity(
                id = "streak_7",
                title = "Iron Consistency",
                description = "Log meals and workouts 7 days consecutively",
                badgeIcon = "local_fire_department",
                category = "Consistency",
                targetValue = 7,
                currentProgress = 6,
                isUnlocked = false,
                xpReward = 300
            ),
            AchievementEntity(
                id = "cardio_fiend",
                title = "Calorie Inferno",
                description = "Burn over 500 active calories in single day",
                badgeIcon = "bolt",
                category = "Workout",
                targetValue = 500,
                currentProgress = 512,
                isUnlocked = true,
                unlockedDate = today,
                xpReward = 175
            ),
            AchievementEntity(
                id = "hydration_hero",
                title = "Hydration Hero",
                description = "Hit your full daily water goal of 2500ml",
                badgeIcon = "water_drop",
                category = "Nutrition",
                targetValue = 2500,
                currentProgress = 2000,
                isUnlocked = false,
                xpReward = 120
            ),
            AchievementEntity(
                id = "wearable_sync_master",
                title = "Synced & Connected",
                description = "Sync health metrics via wearable smartwatch",
                badgeIcon = "watch",
                category = "Wearable",
                targetValue = 1,
                currentProgress = 1,
                isUnlocked = true,
                unlockedDate = today,
                xpReward = 100
            )
        )
        dao.insertAchievements(achievements)

        // 2. Seed Default Reminders
        val defaultReminders = listOf(
            ReminderEntity(
                title = "Breakfast & Fuel Log",
                category = "Meal",
                hour = 8,
                minute = 30,
                isEnabled = true,
                daysOfWeek = "Everyday",
                customMessage = "Time to fuel up for peak energy and hit your morning protein goal!"
            ),
            ReminderEntity(
                title = "Lunch & Midday Macros",
                category = "Meal",
                hour = 12,
                minute = 45,
                isEnabled = true,
                daysOfWeek = "Weekdays",
                customMessage = "Log your lunch to keep your daily calorie deficit on track."
            ),
            ReminderEntity(
                title = "Hydration Power Check",
                category = "Water",
                hour = 15,
                minute = 0,
                isEnabled = true,
                daysOfWeek = "Everyday",
                customMessage = "Drink 350ml cold water to optimize metabolic recovery and focus."
            ),
            ReminderEntity(
                title = "Evening Cardio / Lift",
                category = "Workout",
                hour = 18,
                minute = 15,
                isEnabled = true,
                daysOfWeek = "Everyday",
                customMessage = "Get your workout in to close your active rings before dinner!"
            ),
            ReminderEntity(
                title = "Dinner & Macro Review",
                category = "Meal",
                hour = 20,
                minute = 0,
                isEnabled = true,
                daysOfWeek = "Everyday",
                customMessage = "Review remaining macros and log your final nutritious meal."
            ),
            ReminderEntity(
                title = "Sleep & Recovery Mode",
                category = "Sleep",
                hour = 22,
                minute = 30,
                isEnabled = true,
                daysOfWeek = "Everyday",
                customMessage = "Dim lights and prep for 8 hours of restorative deep sleep."
            )
        )
        defaultReminders.forEach { dao.insertReminder(it) }

        // 3. Seed Today's Meals
        val initialMeals = listOf(
            MealLogEntity(
                date = today,
                mealType = "Breakfast",
                foodName = "Rolled Oats with Blueberries & Whey",
                portion = "1 large bowl",
                calories = 420,
                proteinG = 34.0,
                carbsG = 58.0,
                fatG = 6.5,
                fiberG = 7.0
            ),
            MealLogEntity(
                date = today,
                mealType = "Lunch",
                foodName = "Grilled Chicken Breast with Quinoa & Greens",
                portion = "1 bowl (350g)",
                calories = 540,
                proteinG = 48.0,
                carbsG = 46.0,
                fatG = 12.0,
                fiberG = 6.2
            ),
            MealLogEntity(
                date = today,
                mealType = "Snack",
                foodName = "Greek Yogurt & Raw Almonds",
                portion = "200g yogurt, 20g almonds",
                calories = 260,
                proteinG = 22.0,
                carbsG = 14.0,
                fatG = 11.5,
                fiberG = 2.4
            ),
            MealLogEntity(
                date = today,
                mealType = "Dinner",
                foodName = "Pan-seared Atlantic Salmon with Asparagus",
                portion = "220g salmon fillet",
                calories = 510,
                proteinG = 44.0,
                carbsG = 12.0,
                fatG = 28.0,
                fiberG = 4.0
            )
        )
        initialMeals.forEach { dao.insertMeal(it) }

        // 4. Seed Today's Workout
        val initialWorkouts = listOf(
            WorkoutEntity(
                date = today,
                workoutType = "HIIT & Core Conditioning",
                durationMinutes = 35,
                caloriesBurned = 340,
                avgHeartRateBpm = 148,
                distanceKm = 0.0,
                intensity = "High"
            ),
            WorkoutEntity(
                date = today,
                workoutType = "Outdoor Morning Run",
                durationMinutes = 24,
                caloriesBurned = 260,
                avgHeartRateBpm = 142,
                distanceKm = 4.2,
                intensity = "Moderate"
            )
        )
        initialWorkouts.forEach { dao.insertWorkout(it) }

        // 5. Seed Historical 14-day Daily Metrics for Trends Dashboard
        val cal = Calendar.getInstance()
        for (i in 13 downTo 0) {
            val historyCal = Calendar.getInstance().apply { add(Calendar.DAY_OF_YEAR, -i) }
            val histDate = dateFormat.format(historyCal.time)
            val isCurrentDay = (i == 0)

            val steps = if (isCurrentDay) 8420 else (7500 + (i * 370) % 4500)
            val activeCals = if (isCurrentDay) 512 else (420 + (i * 45) % 300)
            val water = if (isCurrentDay) 2000 else (2100 + (i * 80) % 800)
            val weight = 74.8 - (0.07 * (14 - i)) // gradual healthy fat loss progression
            val sleep = 7.1 + ((i % 3) * 0.4)
            val restingHr = 60 + ((i % 4))
            val activeMin = 35 + ((i * 5) % 40)

            dao.upsertDailyMetric(
                DailyMetricEntity(
                    date = histDate,
                    steps = steps,
                    stepGoal = 10000,
                    activeCalories = activeCals,
                    calorieGoal = 2200,
                    waterMl = water,
                    waterGoalMl = 2500,
                    weightKg = Math.round(weight * 10.0) / 10.0,
                    sleepHours = Math.round(sleep * 10.0) / 10.0,
                    restingHeartRateBpm = restingHr,
                    activeMinutes = activeMin
                )
            )
        }
    }
}
