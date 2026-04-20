package com.ilmezubaan.app.data.remote.gemini

import com.google.gson.annotations.SerializedName

data class GeminiGenerateContentRequest(
    @SerializedName("system_instruction")
    val systemInstruction: GeminiContent? = null,
    val contents: List<GeminiContent>,
    @SerializedName("generationConfig")
    val generationConfig: GeminiGenerationConfig? = null
)

data class GeminiContent(
    val parts: List<GeminiPart>,
    val role: String? = null
)

data class GeminiPart(
    val text: String
)

data class GeminiGenerationConfig(
    val temperature: Double = 0.2,
    @SerializedName("topP")
    val topP: Double = 0.8,
    @SerializedName("maxOutputTokens")
    val maxOutputTokens: Int = 180,
    @SerializedName("responseMimeType")
    val responseMimeType: String = "application/json"
)

data class GeminiGenerateContentResponse(
    val candidates: List<GeminiCandidate>? = null
)

data class GeminiCandidate(
    val content: GeminiContentResponse? = null
)

data class GeminiContentResponse(
    val parts: List<GeminiTextPart>? = null
)

data class GeminiTextPart(
    val text: String? = null
)

data class WordInsight(
    val meaning: String,
    @SerializedName("urduMeaning")
    val urduMeaning: String,
    val pronunciation: String,
    @SerializedName("exampleSentence")
    val exampleSentence: String
)
