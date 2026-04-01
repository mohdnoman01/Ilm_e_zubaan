package com.ilmezubaan.app.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.ilmezubaan.app.data.repository.ConceptRepository
import com.ilmezubaan.app.data.repository.UserStatsRepository

class HomeViewModelFactory(
    private val repository: UserStatsRepository,
    private val conceptRepository: ConceptRepository
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(HomeViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return HomeViewModel(repository, conceptRepository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
