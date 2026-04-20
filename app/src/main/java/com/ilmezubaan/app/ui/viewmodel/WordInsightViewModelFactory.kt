package com.ilmezubaan.app.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.ilmezubaan.app.data.repository.GeminiWordRepository

class WordInsightViewModelFactory(
    private val repository: GeminiWordRepository
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(WordInsightViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return WordInsightViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
