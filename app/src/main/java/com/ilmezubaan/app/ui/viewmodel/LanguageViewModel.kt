package com.ilmezubaan.app.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ilmezubaan.app.data.repository.UserStatsRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

data class Language(
    val name: String,
    val nativeName: String,
    val code: String
)

class LanguageViewModel(private val repository: UserStatsRepository) : ViewModel() {
    private val _languages = listOf(
        Language("Punjabi", " پنجابی", "pa"),
        Language("Sindhi", "سنڌي" , "sd"),
        Language("Pashto", "پښتو", "ps"),
        Language("Urdu", "اردو", "ur"),
        Language("Balochi", "بلوچی", "bal"),
        Language("Saraiki", "سرائیکی", "skr")
    )
    val languages: List<Language> = _languages

    private val _selectedLanguage = MutableStateFlow<Language>(_languages[0])
    val selectedLanguage: StateFlow<Language> = _selectedLanguage.asStateFlow()

    private val _nativeLanguage = MutableStateFlow<Language?>(null)
    val nativeLanguage: StateFlow<Language?> = _nativeLanguage.asStateFlow()

    init {
        viewModelScope.launch {
            repository.userStats.collect { stats ->
                stats?.selectedLanguageName?.let { savedName ->
                    _languages.find { it.name == savedName }?.let { _selectedLanguage.value = it }
                }
                stats?.nativeLanguageName?.let { nativeName ->
                    _languages.find { it.name == nativeName }?.let { _nativeLanguage.value = it }
                }
            }
        }
    }

    fun selectLanguage(language: Language) {
        _selectedLanguage.value = language
        viewModelScope.launch {
            repository.updateSelectedLanguage(language.name)
        }
    }

    fun setNativeLanguage(language: Language) {
        _nativeLanguage.value = language
        viewModelScope.launch {
            repository.updateNativeLanguage(language.name)
        }
    }
}
