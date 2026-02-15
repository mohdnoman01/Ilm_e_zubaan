package com.ilmezubaan.app.data.repository

import com.ilmezubaan.app.data.local.dao.UserStatsDao
import com.ilmezubaan.app.data.local.entities.UserStats
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import java.util.Calendar
import java.util.concurrent.TimeUnit

class UserStatsRepository(private val userStatsDao: UserStatsDao) {

    val userStats: Flow<UserStats?> = userStatsDao.getUserStats()

    suspend fun updateLessonProgress(title: String, type: String, progress: Float) {
        val currentStats = userStatsDao.getUserStats().first() ?: UserStats()
        userStatsDao.insertUserStats(
            currentStats.copy(
                lastLessonTitle = title,
                lastLessonType = type,
                lastLessonProgress = progress
            )
        )
    }

    suspend fun checkAndUpdateStreak() {
        val currentStats = userStatsDao.getUserStats().first() ?: UserStats()
        val today = Calendar.getInstance().apply {
            set(Calendar.HOUR_OF_DAY, 0)
            set(Calendar.MINUTE, 0)
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)
        }.timeInMillis

        val lastOpen = currentStats.lastAppOpenDate
        if (lastOpen == 0L) {
            // First time opening the app
            userStatsDao.insertUserStats(
                currentStats.copy(
                    currentStreak = 1,
                    lastAppOpenDate = today,
                    xpPoints = 10
                )
            )
            return
        }

        val diffInMs = today - lastOpen
        val diffInDays = TimeUnit.MILLISECONDS.toDays(diffInMs)

        val newStreak = when {
            diffInDays == 0L -> currentStats.currentStreak // Already opened today
            diffInDays == 1L -> currentStats.currentStreak + 1 // Consecutive day
            else -> 1 // Streak broken
        }

        userStatsDao.insertUserStats(
            currentStats.copy(
                currentStreak = newStreak,
                lastAppOpenDate = today,
                xpPoints = currentStats.xpPoints + if (diffInDays > 0) 10 else 0
            )
        )
    }
}
