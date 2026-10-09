package com.example.ui.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.AppDatabase
import com.example.data.entities.AchievementEntity
import com.example.data.entities.DailyMetricEntity
import com.example.data.entities.MealLogEntity
import com.example.data.entities.ReminderEntity
import com.example.data.entities.WorkoutEntity
import com.example.data.repository.FitPulseRepository
import com.example.model.ActivityLevel
import com.example.model.FitnessGoal
import com.example.model.FriendActivity
import com.example.model.HealthInsight
import com.example.model.InsightType
import com.example.model.NutritionSummary
import com.example.model.UserProfile
import com.example.model.WearableDevice
import com.example.service.NotificationHelper
import com.example.service.WearableSyncManager
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

enum class AppNavTab(val title: String, val icon: String) {
    HOME("Overview", "home"),
    MEALS("Nutrition", "restaurant"),
    DASHBOARD("Trends", "insights"),
    WEARABLE("Wearables", "watch"),
    REMINDERS("Reminders", "alarm"),
    SOCIAL("Community", "group")
}

class FitPulseViewModel(application: Application) : AndroidViewModel(application) {

    private val repository: FitPulseRepository
    val wearableSyncManager: WearableSyncManager

    private val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
    val todayDate: String = dateFormat.format(Date())

    // Selected navigation tab
    private val _currentTab = MutableStateFlow(AppNavTab.HOME)
    val currentTab: StateFlow<AppNavTab> = _currentTab.asStateFlow()

    // User Profile
    private val _userProfile = MutableStateFlow(UserProfile())
    val userProfile: StateFlow<UserProfile> = _userProfile.asStateFlow()

    // UI Feedback Banner
    private val _userMessage = MutableStateFlow<String?>(null)
    val userMessage: StateFlow<String?> = _userMessage.asStateFlow()

    // Selected Achievement to Share
    private val _selectedShareAchievement = MutableStateFlow<AchievementEntity?>(null)
    val selectedShareAchievement: StateFlow<AchievementEntity?> = _selectedShareAchievement.asStateFlow()

    // Friends Activity Feed
    private val _friendsFeed = MutableStateFlow(
        listOf(
            FriendActivity(
                id = "f1",
                name = "Elena Rostova",
                initials = "ER",
                avatarBgColorHex = 0xFFEC4899,
                activityText = "Crushed an intense 5km Trail Run in 24:18 min!",
                achievementBadge = "Century Walker",
                stepsToday = 12450,
                timeAgo = "18m ago",
                highFives = 16,
                isLikedByUser = false
            ),
            FriendActivity(
                id = "f2",
                name = "Marcus Vance",
                initials = "MV",
                avatarBgColorHex = 0xFF3B82F6,
                activityText = "Hit 145g Protein Goal & finished Upper Body Strength!",
                achievementBadge = "Protein Titan",
                stepsToday = 10180,
                timeAgo = "1h ago",
                highFives = 24,
                isLikedByUser = true
            ),
            FriendActivity(
                id = "f3",
                name = "Chloe Chen",
                initials = "CC",
                avatarBgColorHex = 0xFF10B981,
                activityText = "Synced Garmin Watch — 14-day consistency streak unbroken!",
                achievementBadge = "Iron Consistency",
                stepsToday = 8940,
                timeAgo = "3h ago",
                highFives = 31,
                isLikedByUser = false
            ),
            FriendActivity(
                id = "f4",
                name = "Devon Miller",
                initials = "DM",
                avatarBgColorHex = 0xFFF59E0B,
                activityText = "Logged 2.8L Water hydration target before sunset",
                achievementBadge = "Hydration Hero",
                stepsToday = 7210,
                timeAgo = "5h ago",
                highFives = 9,
                isLikedByUser = false
            )
        )
    )
    val friendsFeed: StateFlow<List<FriendActivity>> = _friendsFeed.asStateFlow()

    init {
        val db = AppDatabase.getInstance(application)
        repository = FitPulseRepository(db.fitPulseDao())
        wearableSyncManager = WearableSyncManager(viewModelScope)

        viewModelScope.launch {
            repository.seedInitialDataIfEmpty()
        }
    }

    // Today's Meals
    val todayMeals: StateFlow<List<MealLogEntity>> = repository.getMealsForDate(todayDate)
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // Today's Workouts
    val todayWorkouts: StateFlow<List<WorkoutEntity>> = repository.getWorkoutsForDate(todayDate)
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // Today's Daily Metrics
    val todayMetric: StateFlow<DailyMetricEntity?> = repository.getDailyMetric(todayDate)
        .stateIn(
            viewModelScope,
            SharingStarted.WhileSubscribed(5000),
            DailyMetricEntity(date = todayDate, steps = 8420, activeCalories = 512, waterMl = 2000)
        )

    // Historical Recent Metrics for Dashboard
    val recentMetrics: StateFlow<List<DailyMetricEntity>> = repository.getRecentDailyMetrics()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // Custom Reminders
    val reminders: StateFlow<List<ReminderEntity>> = repository.getAllReminders()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // Achievements
    val achievements: StateFlow<List<AchievementEntity>> = repository.getAllAchievements()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // Live Nutrition Summary
    val nutritionSummary: StateFlow<NutritionSummary> = combine(
        todayMeals,
        _userProfile
    ) { meals, profile ->
        val totalCals = meals.sumOf { it.calories }
        val totalProt = meals.sumOf { it.proteinG }
        val totalCarb = meals.sumOf { it.carbsG }
        val totalFat = meals.sumOf { it.fatG }
        val totalFib = meals.sumOf { it.fiberG }

        NutritionSummary(
            totalCalories = totalCals,
            totalProtein = totalProt,
            totalCarbs = totalCarb,
            totalFat = totalFat,
            totalFiber = totalFib,
            targetCalories = profile.targetDailyCalories,
            targetProtein = profile.targetProteinG,
            targetCarbs = profile.targetCarbsG,
            targetFat = profile.targetFatG
        )
    }.stateIn(
        viewModelScope,
        SharingStarted.WhileSubscribed(5000),
        NutritionSummary(
            totalCalories = 1730,
            totalProtein = 148.0,
            totalCarbs = 130.0,
            totalFat = 58.0,
            totalFiber = 19.6,
            targetCalories = 2150,
            targetProtein = 148,
            targetCarbs = 195,
            targetFat = 60
        )
    )

    // Personalized Health Insights
    val healthInsights: StateFlow<List<HealthInsight>> = combine(
        nutritionSummary,
        todayMetric,
        _userProfile,
        todayWorkouts
    ) { nutrition, metric, profile, workouts ->
        val insights = mutableListOf<HealthInsight>()
        val steps = metric?.steps ?: 0
        val water = metric?.waterMl ?: 0
        val activeMin = workouts.sumOf { it.durationMinutes }

        // 1. Calorie Deficit / Surplus analysis
        val remainingCals = nutrition.targetCalories - nutrition.totalCalories
        if (remainingCals in 200..600) {
            insights.add(
                HealthInsight(
                    id = "cal_on_track",
                    title = "Optimal Fat Loss Deficit",
                    description = "You're at ${nutrition.totalCalories} kcal consumed (${remainingCals} kcal remaining). Ideal pacing for steady fat loss.",
                    type = InsightType.OPTIMAL,
                    tag = "Metabolism",
                    actionSuggestion = "Have a high-protein snack like Greek yogurt to seal your goal."
                )
            )
        } else if (remainingCals < 0) {
            insights.add(
                HealthInsight(
                    id = "cal_surplus",
                    title = "Caloric Budget Exceeded",
                    description = "You've exceeded target calories by ${-remainingCals} kcal.",
                    type = InsightType.WARNING,
                    tag = "Energy Balance",
                    actionSuggestion = "Consider a 20-min evening brisk walk to offset excess surplus."
                )
            )
        }

        // 2. Protein Intake & Muscle Recovery
        if (nutrition.proteinPercent >= 0.9f) {
            insights.add(
                HealthInsight(
                    id = "protein_crushed",
                    title = "Peak Protein Synthesis Achieved",
                    description = "Delivered ${nutrition.totalProtein.toInt()}g protein today (${(nutrition.proteinPercent * 100).toInt()}% of goal). Excellent for lean tissue preservation.",
                    type = InsightType.OPTIMAL,
                    tag = "Hypertrophy",
                    actionSuggestion = "Your muscles have maximum amino acid supply for overnight repair."
                )
            )
        } else {
            val neededProtein = (nutrition.targetProtein - nutrition.totalProtein).toInt()
            insights.add(
                HealthInsight(
                    id = "protein_boost",
                    title = "Protein Intake Below Target",
                    description = "You need ${neededProtein}g more protein to hit optimal muscle synthesis.",
                    type = InsightType.RECOMMENDATION,
                    tag = "Nutrition",
                    actionSuggestion = "Add a whey protein shake or cottage cheese before bedtime."
                )
            )
        }

        // 3. Activity Level & Steps insight
        if (steps >= profile.dailyStepGoal) {
            insights.add(
                HealthInsight(
                    id = "step_goal_hit",
                    title = "Daily Movement Milestone",
                    description = "Hit $steps steps! Great non-exercise activity thermogenesis (NEAT).",
                    type = InsightType.OPTIMAL,
                    tag = "Movement",
                    actionSuggestion = "Keep up this high volume for cardiovascular longevity."
                )
            )
        } else {
            val remainingSteps = profile.dailyStepGoal - steps
            insights.add(
                HealthInsight(
                    id = "steps_nudge",
                    title = "Step Target Within Reach",
                    description = "You are $remainingSteps steps away from your 10,000 daily movement milestone.",
                    type = InsightType.RECOMMENDATION,
                    tag = "Cardio",
                    actionSuggestion = "A 15-minute brisk walk will close this ring completely."
                )
            )
        }

        // 4. Hydration Check
        if (water < (profile.dailyWaterGoalMl * 0.75)) {
            insights.add(
                HealthInsight(
                    id = "hydration_alert",
                    title = "Hydration Deficit Detected",
                    description = "Logged only ${water}ml of ${profile.dailyWaterGoalMl}ml. Dehydration reduces metabolic rate by up to 3%.",
                    type = InsightType.WARNING,
                    tag = "Hydration",
                    actionSuggestion = "Drink 500ml water now to boost kidney filtration and recovery."
                )
            )
        }

        // 5. Recovery & Strain
        if (activeMin >= 45) {
            insights.add(
                HealthInsight(
                    id = "recovery_alert",
                    title = "High Physical Strain Day",
                    description = "Total training time reached $activeMin mins. Magnesium and 8+ hours sleep recommended.",
                    type = InsightType.RECOVERY,
                    tag = "Recovery",
                    actionSuggestion = "Prioritize light stretching and reduce screen time before bed."
                )
            )
        }

        insights
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    fun selectTab(tab: AppNavTab) {
        _currentTab.value = tab
    }

    fun showMessage(msg: String) {
        _userMessage.value = msg
        viewModelScope.launch {
            kotlinx.coroutines.delay(3500)
            if (_userMessage.value == msg) {
                _userMessage.value = null
            }
        }
    }

    // --- Meal Actions ---
    fun addMeal(
        mealType: String,
        foodName: String,
        portion: String,
        calories: Int,
        protein: Double,
        carbs: Double,
        fat: Double,
        fiber: Double = 0.0
    ) {
        viewModelScope.launch {
            repository.insertMeal(
                MealLogEntity(
                    date = todayDate,
                    mealType = mealType,
                    foodName = foodName,
                    portion = portion,
                    calories = calories,
                    proteinG = protein,
                    carbsG = carbs,
                    fatG = fat,
                    fiberG = fiber
                )
            )
            showMessage("Added $foodName ($calories kcal)")
        }
    }

    fun deleteMeal(meal: MealLogEntity) {
        viewModelScope.launch {
            repository.deleteMeal(meal)
            showMessage("Removed ${meal.foodName}")
        }
    }

    // --- Workout Actions ---
    fun addWorkout(
        workoutType: String,
        durationMinutes: Int,
        caloriesBurned: Int,
        avgHeartRate: Int,
        distanceKm: Double,
        intensity: String
    ) {
        viewModelScope.launch {
            repository.insertWorkout(
                WorkoutEntity(
                    date = todayDate,
                    workoutType = workoutType,
                    durationMinutes = durationMinutes,
                    caloriesBurned = caloriesBurned,
                    avgHeartRateBpm = avgHeartRate,
                    distanceKm = distanceKm,
                    intensity = intensity
                )
            )
            // Also bump active calories in daily metric
            val current = todayMetric.value
            if (current != null) {
                val updated = current.copy(
                    activeCalories = current.activeCalories + caloriesBurned,
                    activeMinutes = current.activeMinutes + durationMinutes
                )
                repository.upsertDailyMetric(updated)
            }
            showMessage("Logged $workoutType (+$caloriesBurned kcal)")
        }
    }

    fun deleteWorkout(workout: WorkoutEntity) {
        viewModelScope.launch {
            repository.deleteWorkout(workout)
            val current = todayMetric.value
            if (current != null) {
                val updated = current.copy(
                    activeCalories = (current.activeCalories - workout.caloriesBurned).coerceAtLeast(0),
                    activeMinutes = (current.activeMinutes - workout.durationMinutes).coerceAtLeast(0)
                )
                repository.upsertDailyMetric(updated)
            }
            showMessage("Deleted ${workout.workoutType}")
        }
    }

    // --- Water Tracker Actions ---
    fun addWater(amountMl: Int) {
        viewModelScope.launch {
            val current = todayMetric.value ?: DailyMetricEntity(date = todayDate)
            val updated = current.copy(waterMl = (current.waterMl + amountMl).coerceAtLeast(0))
            repository.upsertDailyMetric(updated)
            showMessage("Added +${amountMl}ml water")
        }
    }

    fun resetWater() {
        viewModelScope.launch {
            val current = todayMetric.value ?: DailyMetricEntity(date = todayDate)
            repository.upsertDailyMetric(current.copy(waterMl = 0))
            showMessage("Reset water intake")
        }
    }

    // --- Wearable Sync Action ---
    fun triggerWearableSync() {
        wearableSyncManager.syncNow { syncedSteps, syncedKcal, heartRate ->
            viewModelScope.launch {
                val current = todayMetric.value ?: DailyMetricEntity(date = todayDate)
                val updated = current.copy(
                    steps = syncedSteps,
                    activeCalories = syncedKcal,
                    restingHeartRateBpm = heartRate,
                    lastSyncedTime = System.currentTimeMillis()
                )
                repository.upsertDailyMetric(updated)
                showMessage("Wearable sync complete: $syncedSteps steps, $syncedKcal kcal burned")
            }
        }
    }

    // --- Reminder Actions ---
    fun toggleReminder(reminder: ReminderEntity) {
        viewModelScope.launch {
            val updated = reminder.copy(isEnabled = !reminder.isEnabled)
            repository.updateReminder(updated)
            showMessage("${if (updated.isEnabled) "Enabled" else "Disabled"} '${reminder.title}'")
        }
    }

    fun addCustomReminder(
        title: String,
        category: String,
        hour: Int,
        minute: Int,
        daysOfWeek: String,
        customMessage: String
    ) {
        viewModelScope.launch {
            repository.insertReminder(
                ReminderEntity(
                    title = title,
                    category = category,
                    hour = hour,
                    minute = minute,
                    daysOfWeek = daysOfWeek,
                    customMessage = customMessage
                )
            )
            showMessage("Created reminder '$title'")
        }
    }

    fun deleteReminder(reminder: ReminderEntity) {
        viewModelScope.launch {
            repository.deleteReminder(reminder)
            showMessage("Deleted reminder '${reminder.title}'")
        }
    }

    fun testReminderNotification(reminder: ReminderEntity) {
        val success = NotificationHelper.sendNotification(
            getApplication(),
            title = "FitPulse: ${reminder.title}",
            content = reminder.customMessage.ifBlank { "Don't forget your scheduled ${reminder.category} check-in!" }
        )
        if (success) {
            showMessage("Notification sent for '${reminder.title}'")
        } else {
            showMessage("Triggered alert: ${reminder.title} - ${reminder.customMessage}")
        }
    }

    // --- Social & Achievements ---
    fun setShareAchievement(achievement: AchievementEntity?) {
        _selectedShareAchievement.value = achievement
    }

    fun cheerFriend(friendId: String) {
        _friendsFeed.value = _friendsFeed.value.map { friend ->
            if (friend.id == friendId) {
                val newLiked = !friend.isLikedByUser
                friend.copy(
                    isLikedByUser = newLiked,
                    highFives = if (newLiked) friend.highFives + 1 else friend.highFives - 1
                )
            } else friend
        }
    }

    // --- User Profile Update ---
    fun updateProfile(
        weightKg: Double,
        heightCm: Double,
        activityLevel: ActivityLevel,
        goal: FitnessGoal,
        dailyStepGoal: Int,
        dailyWaterGoalMl: Int
    ) {
        _userProfile.value = _userProfile.value.copy(
            weightKg = weightKg,
            heightCm = heightCm,
            activityLevel = activityLevel,
            goal = goal,
            dailyStepGoal = dailyStepGoal,
            dailyWaterGoalMl = dailyWaterGoalMl
        )
        showMessage("Profile & calorie targets updated!")
    }
}
