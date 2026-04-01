package com.ilmezubaan.app.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ilmezubaan.app.data.local.entities.UserStats
import com.ilmezubaan.app.data.model.Concept
import com.ilmezubaan.app.data.repository.ConceptRepository
import com.ilmezubaan.app.data.repository.UserStatsRepository
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.util.Calendar
import java.util.Random

class HomeViewModel(
    private val repository: UserStatsRepository,
    private val conceptRepository: ConceptRepository
) : ViewModel() {

    val userStats: StateFlow<UserStats> = repository.userStats
        .map { it ?: UserStats() }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = UserStats()
        )

    private val _featuredWord = MutableStateFlow<Concept?>(null)
    val featuredWord: StateFlow<Concept?> = _featuredWord.asStateFlow()

    init {
        viewModelScope.launch {
            repository.checkAndUpdateStreak()
            updateFeaturedWord()
        }
    }

    private suspend fun updateFeaturedWord() {
        conceptRepository.allConcepts.collectLatest { concepts ->
            if (concepts.isNotEmpty()) {
                // Use seed based on today's date for daily word
                val today = Calendar.getInstance().apply {
                    set(Calendar.HOUR_OF_DAY, 0)
                    set(Calendar.MINUTE, 0)
                    set(Calendar.SECOND, 0)
                    set(Calendar.MILLISECOND, 0)
                }.timeInMillis
                
                val random = Random(today)
                _featuredWord.value = concepts[random.nextInt(concepts.size)]
            }
        }
    }

    fun getGreeting(): String {
        val hour = Calendar.getInstance().get(Calendar.HOUR_OF_DAY)
        return when (hour) {
            in 5..11 -> "Good Morning"
            in 12..16 -> "Good Afternoon"
            in 17..21 -> "Good Evening"
            else -> "Good Night"
        }
    }
}
