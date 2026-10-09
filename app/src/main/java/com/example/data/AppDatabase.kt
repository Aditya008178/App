package com.example.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.data.dao.FitPulseDao
import com.example.data.entities.AchievementEntity
import com.example.data.entities.DailyMetricEntity
import com.example.data.entities.MealLogEntity
import com.example.data.entities.ReminderEntity
import com.example.data.entities.WorkoutEntity

@Database(
    entities = [
        MealLogEntity::class,
        WorkoutEntity::class,
        DailyMetricEntity::class,
        ReminderEntity::class,
        AchievementEntity::class
    ],
    version = 1,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {

    abstract fun fitPulseDao(): FitPulseDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getInstance(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "fitpulse_database"
                )
                    .fallbackToDestructiveMigration()
                    .build()
                INSTANCE = instance
                instance
            }
        }
    }
}
