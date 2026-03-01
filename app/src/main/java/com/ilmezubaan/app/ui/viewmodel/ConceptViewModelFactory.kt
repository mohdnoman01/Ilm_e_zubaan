package com.ilmezubaan.app.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.ilmezubaan.app.data.repository.ConceptRepository

class ConceptViewModelFactory(private val repository: ConceptRepository) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(ConceptViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return ConceptViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
