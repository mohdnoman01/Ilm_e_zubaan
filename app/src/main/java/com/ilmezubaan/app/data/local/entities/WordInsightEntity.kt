package com.ilmezubaan.app.data.local.entities

import androidx.room.Entity

@Entity(
    tableName = "word_insights",
    primaryKeys = ["word", "learningLanguage", "nativeLanguage"]
)
data class WordInsightEntity(
    val word: String,
    val learningLanguage: String,
    val nativeLanguage: String,
    val meaning: String,
    val urduMeaning: String,
    val pronunciation: String,
    val exampleSentence: String,
    val cachedAt: Long = System.currentTimeMillis()
)
