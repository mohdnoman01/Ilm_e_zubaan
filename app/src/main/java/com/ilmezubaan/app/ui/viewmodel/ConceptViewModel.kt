package com.ilmezubaan.app.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ilmezubaan.app.data.local.entities.ConceptEntity
import com.ilmezubaan.app.data.model.Concept
import com.ilmezubaan.app.data.model.LanguageMetadata
import com.ilmezubaan.app.data.repository.ConceptRepository
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

class ConceptViewModel(private val repository: ConceptRepository) : ViewModel() {

    // Dynamic UI State for language switching
    private val _sourceLanguage = MutableStateFlow("punjabi")
    val sourceLanguage: StateFlow<String> = _sourceLanguage.asStateFlow()

    private val _targetLanguage = MutableStateFlow("urdu")
    val targetLanguage: StateFlow<String> = _targetLanguage.asStateFlow()

    // Concepts observed from Room (Single Source of Truth)
    val concepts: StateFlow<List<Concept>> = repository.allConcepts
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    init {
        syncData()
    }

    fun syncData() {
        viewModelScope.launch {
            repository.syncConcepts()
            repository.syncMetadata()
        }
    }

    fun getMetadata(langId: String): Flow<LanguageMetadata?> {
        return repository.getMetadata(langId.lowercase())
    }

    fun insertConcepts(concepts: List<ConceptEntity>) {
        viewModelScope.launch {
            repository.insertConcepts(concepts)
        }
    }

    fun setSourceLanguage(lang: String) {
        _sourceLanguage.value = lang.lowercase()
    }

    fun setTargetLanguage(lang: String) {
        _targetLanguage.value = lang.lowercase()
    }
}
