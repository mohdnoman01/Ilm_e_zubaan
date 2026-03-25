package com.ilmezubaan.app.data.model

data class Lesson(
    val title: String,
    val type: String,
    val conceptId: String? = null,
    val subtitle: String? = null,
    val audioUrl: String? = null
)
