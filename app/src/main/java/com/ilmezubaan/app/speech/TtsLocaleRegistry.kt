package com.ilmezubaan.app.speech

import java.util.Locale

data class SpeechText(
    val text: String,
    val languageCode: String
)

object TtsLocaleRegistry {
    private val localeByCode = mapOf(
        "en" to Locale.US,
        "ur" to Locale("ur", "PK"),
        "pa" to Locale("pa", "PK"),
        "sd" to Locale("sd", "PK"),
        "ps" to Locale("ps", "PK"),
        "bal" to Locale("bal", "PK"),
        "skr" to Locale("skr", "PK")
    )

    private val codeByName = mapOf(
        "english" to "en",
        "urdu" to "ur",
        "punjabi" to "pa",
        "sindhi" to "sd",
        "pashto" to "ps",
        "balochi" to "bal",
        "saraiki" to "skr",
        "seraiki" to "skr"
    )

    fun codeFor(languageNameOrCode: String?): String {
        val normalized = languageNameOrCode
            ?.trim()
            ?.lowercase(Locale.ROOT)
            .orEmpty()

        return codeByName[normalized] ?: localeByCode.keys.firstOrNull { it == normalized } ?: "en"
    }

    fun localeFor(languageNameOrCode: String?): Locale {
        return localeByCode[codeFor(languageNameOrCode)] ?: Locale.US
    }
}
