package com.ilmezubaan.app.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ilmezubaan.app.data.remote.gemini.WordInsight
import com.ilmezubaan.app.data.repository.GeminiWordRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

data class WordInsightUiState(
    val selectedWord: String = "",
    val insight: WordInsight? = null,
    val isLoading: Boolean = false,
    val errorMessage: String? = null
)

class WordInsightViewModel(
    private val repository: GeminiWordRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(WordInsightUiState())
    val uiState: StateFlow<WordInsightUiState> = _uiState.asStateFlow()

    fun loadWordInsight(
        word: String,
        learningLanguage: String,
        nativeLanguage: String
    ) {
        val cleanWord = word.trim()
        if (cleanWord.isBlank()) {
            _uiState.value = _uiState.value.copy(
                errorMessage = "Select or enter a word first.",
                insight = null,
                selectedWord = ""
            )
            return
        }

        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(
                selectedWord = cleanWord,
                isLoading = true,
                errorMessage = null,
                insight = null
            )

            repository.getWordInsight(
                word = cleanWord,
                learningLanguage = learningLanguage,
                nativeLanguage = nativeLanguage
            ).fold(
                onSuccess = { insight ->
                    _uiState.value = _uiState.value.copy(
                        insight = insight,
                        isLoading = false,
                        errorMessage = null
                    )
                },
                onFailure = { error ->
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        errorMessage = error.message ?: "Unable to get a response right now."
                    )
                }
            )
        }
    }

    fun clearError() {
        _uiState.value = _uiState.value.copy(errorMessage = null)
    }
}
