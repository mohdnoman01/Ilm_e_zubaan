package com.ilmezubaan.app.data.local.entities

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "user_stats")
data class UserStats(
    @PrimaryKey val id: Int = 0,
    val userName: String = "Noman",
    val xpPoints: Int = 0,
    val currentStreak: Int = 0,
    val lastAppOpenDate: Long = 0L,
    val lastLessonTitle: String? = null,
    val lastLessonType: String? = null,
    val lastLessonProgress: Float = 0f
)
