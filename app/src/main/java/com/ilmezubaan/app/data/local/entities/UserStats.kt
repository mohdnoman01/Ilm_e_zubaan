package com.ilmezubaan.app.data.local.entities

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "user_stats")
data class UserStats(
    @PrimaryKey val id: Int = 0,
    val userName: String = "User",
    val xpPoints: Int = 0,
    val currentStreak: Int = 0,
    val avatarEmoji: String = "👤",
    val lastAppOpenDate: Long = 0L,
    val nativeLanguageName: String? = null, // The language the user knows
    val selectedLanguageName: String? = null, // The language the user wants to learn
    val lastLessonTitle: String? = null,
    val lastLessonType: String? = null,
    val lastLessonProgress: Float = 0f
)
