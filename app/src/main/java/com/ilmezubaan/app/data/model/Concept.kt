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
    val type: String = "AUDIO", // "AUDIO" or "VIDEO"
    val mediaUrl: String? = null,
    val audioUrl: String? = null, // Deprecated, use mediaUrl
    val example: String? = null,
    val exampleMeaning: String? = ""
)
