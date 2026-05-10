package com.ilmezubaan.app.speech

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.speech.tts.TextToSpeech
import java.util.UUID

class TtsManager(context: Context) : TextToSpeech.OnInitListener {
    private val appContext = context.applicationContext
    private val preferences = appContext.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
    private var textToSpeech: TextToSpeech? = TextToSpeech(appContext, this)
    private var initialized = false

    override fun onInit(status: Int) {
        initialized = status == TextToSpeech.SUCCESS
        if (initialized) {
            textToSpeech?.setSpeechRate(DEFAULT_SPEECH_RATE)
            textToSpeech?.setPitch(DEFAULT_PITCH)
        }
    }

    fun speak(speechText: SpeechText) {
        val cleanText = speechText.text.trim()
        val engine = textToSpeech

        if (!initialized || engine == null || cleanText.isEmpty()) return

        val requestedLocale = TtsLocaleRegistry.localeFor(speechText.languageCode)
        val localeStatus = engine.setLanguage(requestedLocale)

        if (localeStatus == TextToSpeech.LANG_MISSING_DATA ||
            localeStatus == TextToSpeech.LANG_NOT_SUPPORTED
        ) {
            engine.setLanguage(LocaleFallback)
        }

        engine.speak(
            cleanText,
            TextToSpeech.QUEUE_FLUSH,
            Bundle(),
            UUID.randomUUID().toString()
        )
    }

    fun stop() {
        textToSpeech?.stop()
    }

    fun shutdown() {
        textToSpeech?.stop()
        textToSpeech?.shutdown()
        textToSpeech = null
        initialized = false
    }

    fun checkDataIntent(): Intent {
        return Intent(TextToSpeech.Engine.ACTION_CHECK_TTS_DATA)
    }

    fun installDataIntent(): Intent {
        return Intent(TextToSpeech.Engine.ACTION_INSTALL_TTS_DATA)
    }

    fun shouldPromptForMissingData(resultCode: Int): Boolean {
        return resultCode != TextToSpeech.Engine.CHECK_VOICE_DATA_PASS &&
            !preferences.getBoolean(KEY_INSTALL_PROMPT_SHOWN, false)
    }

    fun markInstallPromptShown() {
        preferences.edit().putBoolean(KEY_INSTALL_PROMPT_SHOWN, true).apply()
    }

    private companion object {
        const val PREFS_NAME = "tts_preferences"
        const val KEY_INSTALL_PROMPT_SHOWN = "install_prompt_shown"
        const val DEFAULT_SPEECH_RATE = 0.92f
        const val DEFAULT_PITCH = 1.0f
        val LocaleFallback = java.util.Locale.US
    }
}
