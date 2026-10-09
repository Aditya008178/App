package com.example.data.entities

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "daily_metrics")
data class DailyMetricEntity(
    @PrimaryKey
    val date: String, // YYYY-MM-DD
    val steps: Int = 0,
    val stepGoal: Int = 10000,
    val activeCalories: Int = 0,
    val calorieGoal: Int = 2200,
    val waterMl: Int = 0,
    val waterGoalMl: Int = 2500,
    val weightKg: Double = 72.5,
    val sleepHours: Double = 7.5,
    val restingHeartRateBpm: Int = 62,
    val activeMinutes: Int = 0,
    val lastSyncedTime: Long = System.currentTimeMillis()
)
