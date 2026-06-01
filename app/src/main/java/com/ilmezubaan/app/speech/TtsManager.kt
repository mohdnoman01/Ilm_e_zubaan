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

    private var pendingSpeech: SpeechText? = null

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
            
            // Speak pending request if any
            pendingSpeech?.let { 
                speak(it)
                pendingSpeech = null
            }
        } else {
            initialized = false
            Timber.e("TTS Initialization failed with status: $status")
            pendingSpeech = null
        }
    }

    fun speak(speechText: SpeechText) {
        val cleanText = speechText.text.trim()
        if (cleanText.isEmpty()) return

        if (!initialized) {
            Timber.w("TTS not initialized. Queuing request.")
            pendingSpeech = speechText
            ensureTts()
            return
        }

        val engine = textToSpeech ?: return
        val requestedLocale = TtsLocaleRegistry.localeFor(speechText.languageCode)
        
        var localeStatus = engine.setLanguage(requestedLocale)

        // Robust fallback logic for Pakistani regional languages
        if (localeStatus <= TextToSpeech.LANG_MISSING_DATA) {
            val isArabicScript = cleanText.any { it in '\u0600'..'\u06FF' || it in '\u0750'..'\u077F' }
            
            if (isArabicScript) {
                Timber.w("Locale $requestedLocale not supported for Arabic-script text. Trying Urdu fallback...")
                localeStatus = engine.setLanguage(java.util.Locale("ur", "PK"))
                if (localeStatus <= TextToSpeech.LANG_MISSING_DATA) {
                    localeStatus = engine.setLanguage(java.util.Locale("ur"))
                }
            } else {
                Timber.w("Locale $requestedLocale not supported. Falling back to English.")
                localeStatus = engine.setLanguage(java.util.Locale.US)
            }
        }

        if (localeStatus <= TextToSpeech.LANG_MISSING_DATA) {
            Timber.e("No suitable TTS engine found for text. Status: $localeStatus")
            return
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
        const val DEFAULT_SPEECH_RATE = 0.85f // Slightly slower for better clarity
        const val DEFAULT_PITCH = 0.95f      // Slightly lower pitch for a calmer, more natural tone
    }
}
