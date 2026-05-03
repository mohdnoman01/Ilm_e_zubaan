package com.ilmezubaan.app.data.repository

import android.util.Log
import com.google.gson.Gson
import com.google.gson.JsonObject
import com.google.gson.JsonParser
import com.ilmezubaan.app.BuildConfig
import com.ilmezubaan.app.data.remote.gemini.GeminiApiService
import com.ilmezubaan.app.data.remote.gemini.GeminiContent
import com.ilmezubaan.app.data.remote.gemini.GeminiGenerateContentRequest
import com.ilmezubaan.app.data.remote.gemini.GeminiGenerationConfig
import com.ilmezubaan.app.data.remote.gemini.GeminiPart
import com.ilmezubaan.app.data.remote.gemini.WordInsight
import retrofit2.HttpException

import javax.inject.Inject

class GeminiWordRepository @Inject constructor(
    private val apiService: GeminiApiService,
    private val gson: Gson,
) {
    private val apiKey: String = BuildConfig.GEMINI_API_KEY
    private val primaryModel: String = "models/gemini-2.5-flash"
    private val fallbackModel: String = "models/gemini-2.5-flash-lite"
    companion object {
        private const val TAG = "GeminiWordRepository"
    }

    suspend fun getWordInsight(
        word: String,
        learningLanguage: String,
        nativeLanguage: String
    ): Result<WordInsight> {
        if (apiKey.isBlank()) {
            return Result.failure(IllegalStateException("Gemini API key is missing in BuildConfig."))
        }

        return runCatching {
            val request = GeminiGenerateContentRequest(
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
                                text = """
                                    Explain this word for a beginner learner.
                                    Word: $word
                                    Language being learned: $learningLanguage
                                    Support language: $nativeLanguage
                                """.trimIndent()
                            )
                        )
                    )
                ),
                generationConfig = GeminiGenerationConfig()
            )

            val response = try {
                apiService.generateContent(
                    model = primaryModel,
                    apiKey = apiKey,
                    request = request
                )
            } catch (error: HttpException) {
                val errorBody = error.response()?.errorBody()?.string().orEmpty()
                Log.e(
                    TAG,
                    "Primary Gemini request failed. code=${error.code()} model=$primaryModel body=$errorBody",
                    error
                )
                if (error.code() != 404) throw error

                apiService.generateContent(
                    model = fallbackModel,
                    apiKey = apiKey,
                    request = request
                )
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
                ?: throw IllegalStateException("Gemini returned an empty response.")

            parseWordInsight(rawJson)
        }.recoverCatching { error ->
            if (error is HttpException) {
                val errorBody = error.response()?.errorBody()?.string().orEmpty()
                Log.e(
                    TAG,
                    "Gemini request failed. code=${error.code()} primaryModel=$primaryModel fallbackModel=$fallbackModel body=$errorBody",
                    error
                )
            } else {
                Log.e(TAG, "Gemini request failed. primaryModel=$primaryModel fallbackModel=$fallbackModel", error)
            }

            if (error is HttpException && error.code() == 404) {
                throw IllegalStateException(
                    "Gemini model not found for this API key. Tried $primaryModel and $fallbackModel."
                )
            }
            throw error
        }
    }

    private fun buildSystemPrompt(
        learningLanguage: String,
        nativeLanguage: String
    ): String {
        return """
            You are Ilm-e-Zaban's word helper for learners in Pakistan.
            The learner may be a beginner or non-literate.
            Keep every answer short, simple, and beginner-friendly.
            Focus on one word only.
            Use easy English for meaning.
            Use clear Urdu script for urduMeaning.
            Use a simple Roman-style pronunciation.
            Make the example sentence short, daily-life, and easy to understand.
            Target language: $learningLanguage.
            Learner support language: $nativeLanguage.
            Return valid JSON only.
            Use exactly these keys:
            {
              "meaning": "...",
              "urduMeaning": "...",
              "pronunciation": "...",
              "exampleSentence": "..."
            }
            Escape internal quotes properly.
            Do not place line breaks inside JSON string values.
            Do not add markdown, code fences, notes, labels, or extra keys.
        """.trimIndent()
    }

    private fun parseWordInsight(rawJson: String): WordInsight {
        return runCatching {
            gson.fromJson(rawJson, WordInsight::class.java)
        }.recoverCatching {
            val normalizedJson = normalizeMalformedJson(rawJson)
            gson.fromJson(normalizedJson, WordInsight::class.java)
        }.recoverCatching {
            val jsonObject = JsonParser.parseString(normalizeMalformedJson(rawJson)).asJsonObject
            wordInsightFromJsonObject(jsonObject)
        }.recoverCatching {
            extractWordInsightFromText(rawJson)
        }.getOrElse { error ->
            Log.e(TAG, "Unable to parse Gemini response. raw=$rawJson", error)
            throw IllegalStateException("Gemini returned text in an unexpected format.")
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
        fun extract(label: String, fallbackLabel: String? = null): String {
            val regex = Regex(
                pattern = """(?im)^["\s\{\[]*${Regex.escape(label)}["\s:=-]*(.+?)(?=^\s*[A-Za-z][A-Za-z ]*["\s:=-]|\z)""",
                options = setOf(RegexOption.DOT_MATCHES_ALL, RegexOption.MULTILINE)
            )
            val primary = regex.find(text)?.groupValues?.getOrNull(1)?.cleanExtractedValue()
            if (!primary.isNullOrBlank()) return primary

            if (fallbackLabel != null) {
                val fallbackRegex = Regex(
                    pattern = """(?im)^["\s\{\[]*${Regex.escape(fallbackLabel)}["\s:=-]*(.+?)(?=^\s*[A-Za-z][A-Za-z ]*["\s:=-]|\z)""",
                    options = setOf(RegexOption.DOT_MATCHES_ALL, RegexOption.MULTILINE)
                )
                val fallback = fallbackRegex.find(text)?.groupValues?.getOrNull(1)?.cleanExtractedValue()
                if (!fallback.isNullOrBlank()) return fallback
            }

            return ""
        }

        return WordInsight(
            meaning = extract("meaning"),
            urduMeaning = extract("urduMeaning", fallbackLabel = "urdu meaning"),
            pronunciation = extract("pronunciation"),
            exampleSentence = extract("exampleSentence", fallbackLabel = "example sentence")
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
