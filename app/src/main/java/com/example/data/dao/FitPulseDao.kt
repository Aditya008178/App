package com.example.data.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.data.entities.AchievementEntity
import com.example.data.entities.DailyMetricEntity
import com.example.data.entities.MealLogEntity
import com.example.data.entities.ReminderEntity
import com.example.data.entities.WorkoutEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface FitPulseDao {

    // --- Meals ---
    @Query("SELECT * FROM meal_logs WHERE date = :date ORDER BY timestamp DESC")
    fun getMealsForDate(date: String): Flow<List<MealLogEntity>>

    @Query("SELECT * FROM meal_logs ORDER BY timestamp DESC LIMIT 100")
    fun getAllMeals(): Flow<List<MealLogEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertMeal(meal: MealLogEntity): Long

    @Delete
    suspend fun deleteMeal(meal: MealLogEntity)

    // --- Workouts ---
    @Query("SELECT * FROM workout_logs WHERE date = :date ORDER BY timestamp DESC")
    fun getWorkoutsForDate(date: String): Flow<List<WorkoutEntity>>

    @Query("SELECT * FROM workout_logs ORDER BY timestamp DESC")
    fun getAllWorkouts(): Flow<List<WorkoutEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertWorkout(workout: WorkoutEntity): Long

    @Delete
    suspend fun deleteWorkout(workout: WorkoutEntity)

    // --- Daily Metrics ---
    @Query("SELECT * FROM daily_metrics WHERE date = :date LIMIT 1")
    fun getDailyMetric(date: String): Flow<DailyMetricEntity?>

    @Query("SELECT * FROM daily_metrics WHERE date = :date LIMIT 1")
    suspend fun getDailyMetricDirect(date: String): DailyMetricEntity?

    @Query("SELECT * FROM daily_metrics ORDER BY date DESC LIMIT 90")
    fun getRecentDailyMetrics(): Flow<List<DailyMetricEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsertDailyMetric(metric: DailyMetricEntity)

    // --- Reminders ---
    @Query("SELECT * FROM custom_reminders ORDER BY hour ASC, minute ASC")
    fun getAllReminders(): Flow<List<ReminderEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertReminder(reminder: ReminderEntity): Long

    @Update
    suspend fun updateReminder(reminder: ReminderEntity)

    @Delete
    suspend fun deleteReminder(reminder: ReminderEntity)

    // --- Achievements ---
    @Query("SELECT * FROM achievements ORDER BY isUnlocked DESC, targetValue ASC")
    fun getAllAchievements(): Flow<List<AchievementEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAchievements(achievements: List<AchievementEntity>)

    @Update
    suspend fun updateAchievement(achievement: AchievementEntity)

    @Query("SELECT COUNT(*) FROM achievements")
    suspend fun getAchievementCount(): Int
}
