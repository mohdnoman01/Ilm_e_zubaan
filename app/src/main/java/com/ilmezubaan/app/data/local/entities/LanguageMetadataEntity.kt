package com.ilmezubaan.app.data.local.entities

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "language_metadata")
data class LanguageMetadataEntity(
    @PrimaryKey val languageId: String, // e.g., "punjabi"
    val introVideoUrl: String,
    val historyText: String,
    val region: String,
    val lifestyleBrief: String,
    val updatedAt: Long
)
