package com.ilmezubaan.app.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.ilmezubaan.app.data.local.entities.WordInsightEntity

@Dao
interface WordInsightDao {
    @Query("SELECT * FROM word_insights WHERE word = :word AND learningLanguage = :learningLanguage AND nativeLanguage = :nativeLanguage LIMIT 1")
    suspend fun getInsight(word: String, learningLanguage: String, nativeLanguage: String): WordInsightEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertInsight(insight: WordInsightEntity)

    @Query("DELETE FROM word_insights WHERE cachedAt < :timestamp")
    suspend fun deleteOldInsights(timestamp: Long)
}
