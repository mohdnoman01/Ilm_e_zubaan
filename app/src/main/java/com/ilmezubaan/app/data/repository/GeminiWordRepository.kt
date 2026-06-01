package com.ilmezubaan.app.data.repository

import com.google.gson.Gson
import com.google.gson.JsonObject
import com.ilmezubaan.app.BuildConfig
import com.ilmezubaan.app.data.local.dao.WordInsightDao
import com.ilmezubaan.app.data.local.entities.WordInsightEntity
import com.ilmezubaan.app.data.remote.gemini.GeminiApiService
import com.ilmezubaan.app.data.remote.gemini.GeminiContent
import com.ilmezubaan.app.data.remote.gemini.GeminiGenerateContentRequest
import com.ilmezubaan.app.data.remote.gemini.GeminiGenerationConfig
import com.ilmezubaan.app.data.remote.gemini.GeminiPart
import com.ilmezubaan.app.data.remote.gemini.WordInsight
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.withContext
import retrofit2.HttpException
import timber.log.Timber
import javax.inject.Inject

class GeminiWordRepository @Inject constructor(
    private val apiService: GeminiApiService,
    private val gson: Gson,
    private val wordInsightDao: WordInsightDao,
) {
    // We use two API keys to avoid rate limits (HTTP 429)
    private val apiKeys: List<String> = listOf(
        BuildConfig.GEMINI_API_KEY_1,
        BuildConfig.GEMINI_API_KEY_2
    ).filter { it.isNotBlank() }

    private val models: List<String> = listOf(
        "gemini-2.5-flash",
        "gemini-2.5-flash-lite"
    )

    suspend fun getWordInsight(
        word: String,
        learningLanguage: String,
        nativeLanguage: String
    ): Result<WordInsight> = withContext(Dispatchers.IO) {
        val cleanWord = word.trim().lowercase()
        
        // 1. Check Cache first
        runCatching {
            wordInsightDao.getInsight(cleanWord, learningLanguage, nativeLanguage)
        }.getOrNull()?.let { cached ->
            Timber.d("Returning cached insight for: $cleanWord")
            return@withContext Result.success(
                WordInsight(
                    meaning = cached.meaning,
                    urduMeaning = cached.urduMeaning,
                    pronunciation = cached.pronunciation,
                    exampleSentence = cached.exampleSentence
                )
            )
        }

        if (apiKeys.isEmpty()) {
            return@withContext Result.failure(IllegalStateException("No Gemini API keys found. Please check local.properties and Sync Gradle."))
        }

        val request = createRequest(cleanWord, learningLanguage, nativeLanguage)

        // Try rotation logic: Each key with each model
        val errors = mutableListOf<Throwable>()

        for (key in apiKeys) {
            for (model in models) {
                val result = tryRequest(key, model, request)
                
                if (result.isSuccess) {
                    val insight = result.getOrThrow()
                    saveToCache(cleanWord, learningLanguage, nativeLanguage, insight)
                    return@withContext Result.success(insight)
                }

                val error = result.exceptionOrNull() ?: Exception("Unknown error")
                errors.add(error)

                if ((error is HttpException) && (error.code() == 429)) {
                    Timber.w("Model $model with key hit 429. Trying next...")
                    delay(300) // Small breather
                    continue 
                } else {
                    Timber.e(error, "Model $model failed with non-429 error: ${error.message}")
                    // If it's a fatal error like 401/403, we might want to stop early, 
                    // but let's try other keys just in case one is valid.
                }
            }
        }

        val allAreQuota = errors.all { it is HttpException && it.code() == 429 }
        if (allAreQuota) {
            Result.failure(IllegalStateException("All API keys and models exceeded quota. Please wait 1 minute and try again."))
        } else {
            val mostRelevantError = errors.find { it !is HttpException || it.code() != 429 } ?: errors.first()
            Result.failure(mostRelevantError)
        }
    }

    private suspend fun tryRequest(apiKey: String, model: String, request: GeminiGenerateContentRequest): Result<WordInsight> {
        return runCatching {
            val response = try {
                apiService.generateContent(
                    model = model,
                    apiKey = apiKey,
                    request = request
                )
            } catch (error: HttpException) {
                val errorBody = error.response()?.errorBody()?.string()?.take(500)
                Timber.e(error, "Gemini HTTP ${error.code()} from $model: $errorBody")
                throw error
            }

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
        val cleaned = rawJson.trim()
        return runCatching {
            val insight = gson.fromJson(cleaned, WordInsight::class.java)
            if (insight.meaning.isBlank() && insight.urduMeaning.isBlank()) {
                throw IllegalStateException("Parsed insight is empty")
            }
            insight
        }.recoverCatching {
            val normalizedJson = normalizeMalformedJson(cleaned)
            val insight = gson.fromJson(normalizedJson, WordInsight::class.java)
            if (insight.meaning.isBlank() && insight.urduMeaning.isBlank()) {
                throw IllegalStateException("Parsed normalized insight is empty")
            }
            insight
        }.recoverCatching {
            val insight = extractWordInsightFromText(cleaned)
            if (insight.meaning.isBlank() && insight.urduMeaning.isBlank()) {
                throw IllegalStateException("Extracted insight is empty")
            }
            insight
        }.getOrElse { error ->
            Timber.e(error, "Unable to parse Gemini response. raw=$rawJson")
            throw IllegalStateException("Gemini returned an invalid response. Please try again.")
        }
    }

    private fun normalizeMalformedJson(raw: String): String {
        if (raw.isEmpty()) return raw
        // Use regex for basic normalization instead of a complex manual loop
        return raw.replace(Regex("(?<!\\\\)\\n"), "\\n")
            .replace(Regex("(?<!\\\\)\\r"), "")
            .trim()
    }

    private fun extractWordInsightFromText(text: String): WordInsight {
        fun extract(vararg labels: String): String {
            for (label in labels) {
                // Improved regex to handle quoted or unquoted keys and various separators, 
                // capturing until next key or end of string.
                val pattern = """(?im)["']?${Regex.escape(label)}["']?\s*[:=-]\s*["']?(.+?)(?=["']?\s*,?\s*["']?[A-Za-z_]+["']?\s*[:=-]|\s*["'}]?\s*\z)"""
                val regex = Regex(pattern, setOf(RegexOption.DOT_MATCHES_ALL))
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
