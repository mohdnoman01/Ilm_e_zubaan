package com.ilmezubaan.app.ui.viewmodel

import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel

class LanguageViewModel : ViewModel() {
    val selectedLanguage = mutableStateOf("")

    fun selectLanguage(language: String) {
        selectedLanguage.value = language
    }
}
