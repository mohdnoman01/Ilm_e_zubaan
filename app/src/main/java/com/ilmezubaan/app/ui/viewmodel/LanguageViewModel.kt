package com.ilmezubaan.app.ui.viewmodel

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

data class Language(
    val name: String,
    val nativeName: String,
    val code: String
)

class LanguageViewModel : ViewModel() {
    private val _languages = listOf(
        Language("Punjabi", " پنجابی", "pa"),
        Language("Sindhi", "سنڌي" , "sd"),
        Language("Pashto", "پښتو", "ps"),
        Language("Urdu", "اردو", "ur"),
        Language("Balochi", "بلوچی", "bal"),
        Language("Saraiki", "سرائیکی", "skr")
    )
    val languages: List<Language> = _languages

    // Default to Punjabi
    private val _selectedLanguage = MutableStateFlow<Language>(_languages[0])
    val selectedLanguage: StateFlow<Language> = _selectedLanguage.asStateFlow()

    fun selectLanguage(language: Language) {
        _selectedLanguage.value = language
    }
}
