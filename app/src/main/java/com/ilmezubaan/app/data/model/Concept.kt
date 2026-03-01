package com.ilmezubaan.app.data.model

data class Concept(
    val conceptId: String,
    val englishMeaning: String,
    val category: String,
    val difficultyLevel: String,
    val languages: Map<String, LanguageDetail>,
    val updatedAt: Long
)

data class LanguageDetail(
    val script: String,
    val roman: String,
    val audioUrl: String? = null
)
