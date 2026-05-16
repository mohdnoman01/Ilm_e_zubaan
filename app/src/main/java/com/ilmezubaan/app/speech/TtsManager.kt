package com.ilmezubaan.app.speech

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.speech.tts.TextToSpeech
import timber.log.Timber
import java.util.UUID

class TtsManager(context: Context) : TextToSpeech.OnInitListener {
    private val appContext = context.applicationContext
    private val preferences = appContext.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
    private var textToSpeech: TextToSpeech? = null
    private var initialized = false

    private fun ensureTts() {
        if (textToSpeech == null) {
            Timber.d("Initializing TTS Engine...")
            textToSpeech = TextToSpeech(appContext, this)
        }
    }

    override fun onInit(status: Int) {
        if (status == TextToSpeech.SUCCESS) {
            initialized = true
            textToSpeech?.setSpeechRate(DEFAULT_SPEECH_RATE)
            textToSpeech?.setPitch(DEFAULT_PITCH)
            Timber.d("TTS Initialized successfully")
        } else {
            initialized = false
            Timber.e("TTS Initialization failed with status: $status")
        }
    }

    fun speak(speechText: SpeechText) {
        ensureTts()
        val cleanText = speechText.text.trim()
        val engine = textToSpeech

        if (engine == null) {
            Timber.e("Speak failed: Engine is null")
            return
        }

        if (!initialized) {
            Timber.w("Speak called before initialization. Initializing again if null.")
            if (textToSpeech == null) {
                textToSpeech = TextToSpeech(appContext, this)
            }
            return
        }

        if (cleanText.isEmpty()) return

        val requestedLocale = TtsLocaleRegistry.localeFor(speechText.languageCode)
        Timber.d("TTS Requesting locale: $requestedLocale for language: ${speechText.languageCode}")
        
        var localeStatus = engine.setLanguage(requestedLocale)

        // Improved fallback logic: If regional language is missing, try Urdu (most similar script)
        if (localeStatus == TextToSpeech.LANG_MISSING_DATA ||
            localeStatus == TextToSpeech.LANG_NOT_SUPPORTED
        ) {
            Timber.w("Locale $requestedLocale not supported. Attempting Urdu fallback...")
            val urduLocale = java.util.Locale("ur", "PK")
            localeStatus = engine.setLanguage(urduLocale)
            
            if (localeStatus == TextToSpeech.LANG_MISSING_DATA ||
                localeStatus == TextToSpeech.LANG_NOT_SUPPORTED
            ) {
                Timber.w("Urdu fallback failed. Falling back to English.")
                engine.setLanguage(LocaleFallback)
            }
        }

        val result = engine.speak(
            cleanText,
            TextToSpeech.QUEUE_FLUSH,
            Bundle(),
            UUID.randomUUID().toString()
        )
        
        if (result == TextToSpeech.ERROR) {
            Timber.e("Engine.speak returned error for text: $cleanText")
        }
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
        const val DEFAULT_SPEECH_RATE = 0.85f // Slightly slower for better clarity
        const val DEFAULT_PITCH = 0.95f      // Slightly lower pitch for a calmer, more natural tone
        val LocaleFallback = java.util.Locale.US
    }
}
