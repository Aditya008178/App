package com.example.data.entities

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "workout_logs")
data class WorkoutEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val date: String, // YYYY-MM-DD
    val workoutType: String, // Running, HIIT, Cycling, Strength, Walking, Yoga, Swimming
    val durationMinutes: Int,
    val caloriesBurned: Int,
    val avgHeartRateBpm: Int = 135,
    val distanceKm: Double = 0.0,
    val intensity: String = "Moderate", // Low, Moderate, High, Extreme
    val timestamp: Long = System.currentTimeMillis()
)
