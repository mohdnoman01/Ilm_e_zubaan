package com.ilmezubaan.app.data.local.entities

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "concepts")
data class ConceptEntity(
    @PrimaryKey val conceptId: String,
    val englishMeaning: String,
    val category: String,
    val difficultyLevel: String,
    val languagesJson: String, // Stored as JSON string via TypeConverter
    val context: String? = null,
    val updatedAt: Long
)

data class ConceptLanguageData(
    val script: String,
    val roman: String,
    val audioUrl: String? = null,
    val example: String? = null,
    val exampleMeaning: String? = null
)
