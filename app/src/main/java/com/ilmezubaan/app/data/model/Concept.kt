package com.ilmezubaan.app.data.model

import com.google.firebase.database.IgnoreExtraProperties

@IgnoreExtraProperties
data class Concept(
    val conceptId: String = "",
    val englishMeaning: String = "",
    val category: String = "General",
    val difficultyLevel: String = "Basic",
    val languages: Map<String, LanguageDetail> = emptyMap(),
    val context: String? = null,
    val updatedAt: Long = 0L
)

@IgnoreExtraProperties
data class LanguageDetail(
    val script: String = "",
    val roman: String = "",
    val audioUrl: String? = null,
    val example: String? = null,
    val exampleMeaning: String? = "" // Changed to nullable or default empty to prevent parsing errors
)
