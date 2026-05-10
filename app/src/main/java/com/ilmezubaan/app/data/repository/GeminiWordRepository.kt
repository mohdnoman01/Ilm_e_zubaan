package com.ilmezubaan.app.data.repository

import android.util.Log
import com.google.gson.Gson
import com.google.gson.JsonObject
import com.google.gson.JsonParser
import com.ilmezubaan.app.BuildConfig
import com.ilmezubaan.app.data.local.dao.WordInsightDao
import com.ilmezubaan.app.data.local.entities.WordInsightEntity
import com.ilmezubaan.app.data.remote.gemini.GeminiApiService
import com.ilmezubaan.app.data.remote.gemini.GeminiContent
import com.ilmezubaan.app.data.remote.gemini.GeminiGenerateContentRequest
import com.ilmezubaan.app.data.remote.gemini.GeminiGenerationConfig
import com.ilmezubaan.app.data.remote.gemini.GeminiPart
import com.ilmezubaan.app.data.remote.gemini.WordInsight
import kotlinx.coroutines.delay
import retrofit2.HttpException
import javax.inject.Inject

class GeminiWordRepository @Inject constructor(
    private val apiService: GeminiApiService,
    private val gson: Gson,
    private val wordInsightDao: WordInsightDao
) {
    // We use two API keys to avoid rate limits (HTTP 429)
    private val apiKeys: List<String> = listOf(
        BuildConfig.GEMINI_API_KEY_1,
        BuildConfig.GEMINI_API_KEY_2
    ).filter { it.isNotBlank() }

    private val models: List<String> = listOf(
        "models/gemini-1.5-flash",
        "models/gemini-2.0-flash-lite",
        "models/gemini-1.5-flash-8b"
    )

    companion object {
        private const val TAG = "GeminiWordRepository"
    }

    suspend fun getWordInsight(
        word: String,
        learningLanguage: String,
        nativeLanguage: String
    ): Result<WordInsight> {
        val cleanWord = word.trim().lowercase()
        
        // 1. Check Cache first
        runCatching {
            wordInsightDao.getInsight(cleanWord, learningLanguage, nativeLanguage)
        }.getOrNull()?.let { cached ->
            Log.d(TAG, "Returning cached insight for: $cleanWord")
            return Result.success(
                WordInsight(
                    meaning = cached.meaning,
                    urduMeaning = cached.urduMeaning,
                    pronunciation = cached.pronunciation,
                    exampleSentence = cached.exampleSentence
                )
            )
        }

        if (apiKeys.isEmpty()) {
            return Result.failure(IllegalStateException("No Gemini API keys found. Please check local.properties and Sync Gradle."))
        }

        val request = createRequest(cleanWord, learningLanguage, nativeLanguage)

        // Try rotation logic: Each key with each model
        for (key in apiKeys) {
            for (model in models) {
                val result = tryRequest(key, model, request)
                
                if (result.isSuccess) {
                    val insight = result.getOrThrow()
                    saveToCache(cleanWord, learningLanguage, nativeLanguage, insight)
                    return Result.success(insight)
                }

                val error = result.exceptionOrNull()
                if (error is HttpException && error.code() == 429) {
                    Log.w(TAG, "Model $model with key hit 429. Trying next...")
                    delay(500) // Small breather
                    continue 
                }
            }
        }

        return Result.failure(IllegalStateException("All API keys and models exceeded quota. Please wait 1 minute and try again."))
    }

    private suspend fun tryRequest(apiKey: String, model: String, request: GeminiGenerateContentRequest): Result<WordInsight> {
        return runCatching {
            val response = apiService.generateContent(
                model = model,
                apiKey = apiKey,
                request = request
            )

            val rawJson = response.candidates
                ?.firstOrNull()
                ?.content
                ?.parts
                ?.joinToString(separator = "") { it.text.orEmpty() }
                ?.trim()
                ?.removePrefix("```json")
                ?.removePrefix("```")
                ?.removeSuffix("```")
                ?.trim()
                ?: throw IllegalStateException("Empty response")

            parseWordInsight(rawJson)
        }
    }

    private fun createRequest(word: String, learningLanguage: String, nativeLanguage: String): GeminiGenerateContentRequest {
        return GeminiGenerateContentRequest(
            systemInstruction = GeminiContent(
                parts = listOf(
                    GeminiPart(
                        text = buildSystemPrompt(
                            learningLanguage = learningLanguage,
                            nativeLanguage = nativeLanguage
                        )
                    )
                )
            ),
            contents = listOf(
                GeminiContent(
                    role = "user",
                    parts = listOf(
                        GeminiPart(
                            text = "Explain the word '$word' for a beginner learner."
                        )
                    )
                )
            ),
            generationConfig = GeminiGenerationConfig()
        )
    }

    private suspend fun saveToCache(word: String, learning: String, native: String, insight: WordInsight) {
        runCatching {
            wordInsightDao.insertInsight(
                WordInsightEntity(
                    word = word,
                    learningLanguage = learning,
                    nativeLanguage = native,
                    meaning = insight.meaning,
                    urduMeaning = insight.urduMeaning,
                    pronunciation = insight.pronunciation,
                    exampleSentence = insight.exampleSentence
                )
            )
        }
    }

    private fun buildSystemPrompt(
        learningLanguage: String,
        nativeLanguage: String
    ): String {
        return """
            You are Ilm-e-Zaban's word helper for learners in Pakistan.
            Target language: $learningLanguage. Support language: $nativeLanguage.
            Keep answers short, simple, and beginner-friendly.
            
            Return valid JSON only.
            Example for word "Kitab":
            {
              "meaning": "A book that you read.",
              "urduMeaning": "کتاب",
              "pronunciation": "ki-taab",
              "exampleSentence": "I am reading a kitab."
            }

            Use exactly these keys: "meaning", "urduMeaning", "pronunciation", "exampleSentence".
            Do not add markdown, notes, or extra keys.
        """.trimIndent()
    }

    private fun parseWordInsight(rawJson: String): WordInsight {
        return runCatching {
            val insight = gson.fromJson(rawJson, WordInsight::class.java)
            if (insight.meaning.isBlank() && insight.urduMeaning.isBlank()) {
                throw IllegalStateException("Parsed insight is empty")
            }
            insight
        }.recoverCatching {
            val normalizedJson = normalizeMalformedJson(rawJson)
            val insight = gson.fromJson(normalizedJson, WordInsight::class.java)
            if (insight.meaning.isBlank() && insight.urduMeaning.isBlank()) {
                throw IllegalStateException("Parsed normalized insight is empty")
            }
            insight
        }.recoverCatching {
            val jsonObject = JsonParser.parseString(normalizeMalformedJson(rawJson)).asJsonObject
            val insight = wordInsightFromJsonObject(jsonObject)
            if (insight.meaning.isBlank() && insight.urduMeaning.isBlank()) {
                throw IllegalStateException("Parsed JSON object insight is empty")
            }
            insight
        }.recoverCatching {
            val insight = extractWordInsightFromText(rawJson)
            if (insight.meaning.isBlank() && insight.urduMeaning.isBlank()) {
                throw IllegalStateException("Extracted insight is empty")
            }
            insight
        }.getOrElse { error ->
            Log.e(TAG, "Unable to parse Gemini response. raw=$rawJson", error)
            throw IllegalStateException("Gemini returned an invalid or empty response. Please try again.")
        }
    }

    private fun normalizeMalformedJson(raw: String): String {
        val builder = StringBuilder(raw.length + 16)
        var inString = false
        var escaping = false

        for (char in raw) {
            when {
                escaping -> {
                    builder.append(char)
                    escaping = false
                }
                char == '\\' -> {
                    builder.append(char)
                    escaping = true
                }
                char == '"' -> {
                    builder.append(char)
                    inString = !inString
                }
                inString && char == '\n' -> builder.append("\\n")
                inString && char == '\r' -> builder.append("\\r")
                else -> builder.append(char)
            }
        }

        return builder.toString()
    }

    private fun wordInsightFromJsonObject(jsonObject: JsonObject): WordInsight {
        fun valueFor(key: String): String {
            return jsonObject.get(key)?.asString?.trim().orEmpty()
        }

        return WordInsight(
            meaning = valueFor("meaning"),
            urduMeaning = valueFor("urduMeaning"),
            pronunciation = valueFor("pronunciation"),
            exampleSentence = valueFor("exampleSentence")
        )
    }

    private fun extractWordInsightFromText(text: String): WordInsight {
        fun extract(vararg labels: String): String {
            for (label in labels) {
                val regex = Regex(
                    pattern = """(?im)^["\s\{\[]*${Regex.escape(label)}["\s:=-]*(.+?)(?=^\s*[A-Za-z][A-Za-z _]*["\s:=-]|\z)""",
                    options = setOf(RegexOption.DOT_MATCHES_ALL, RegexOption.MULTILINE)
                )
                val match = regex.find(text)?.groupValues?.getOrNull(1)?.cleanExtractedValue()
                if (!match.isNullOrBlank()) return match
            }
            return ""
        }

        return WordInsight(
            meaning = extract("meaning", "definition", "explanation"),
            urduMeaning = extract("urduMeaning", "urdu_meaning", "urdu meaning", "urdu"),
            pronunciation = extract("pronunciation", "roman", "how to say"),
            exampleSentence = extract("exampleSentence", "example_sentence", "example sentence", "example")
        )
    }

    private fun String.cleanExtractedValue(): String {
        return trim()
            .trim('"')
            .trim(',')
            .replace("\\n", "\n")
            .replace("\\r", "")
            .trim()
    }
}
