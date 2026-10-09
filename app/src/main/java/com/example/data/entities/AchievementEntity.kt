package com.example.data.entities

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "achievements")
data class AchievementEntity(
    @PrimaryKey
    val id: String,
    val title: String,
    val description: String,
    val badgeIcon: String,
    val category: String, // Steps, Nutrition, Workout, Consistency, Wearable
    val targetValue: Int,
    val currentProgress: Int,
    val isUnlocked: Boolean = false,
    val unlockedDate: String? = null,
    val xpReward: Int = 100
)
