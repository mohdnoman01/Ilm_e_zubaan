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

import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
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
        viewModelScope.launch(kotlinx.coroutines.Dispatchers.IO) {
            repository.checkAndUpdateStreak()
            conceptRepository.syncConcepts()
            observeFeaturedWord()
        }
    }

    private fun observeFeaturedWord() {
        viewModelScope.launch {
            combine(conceptRepository.allConcepts, userStats) { concepts, stats ->
                Pair(concepts, stats.selectedLanguageName)
            }.collectLatest { (concepts, languageName) ->
                if (concepts.isNotEmpty() && !languageName.isNullOrEmpty()) {
                    // Filter concepts that have the selected language available
                    val filtered = concepts.filter { concept ->
                        concept.languages.keys.any { it.equals(languageName, ignoreCase = true) }
                    }

                    if (filtered.isNotEmpty()) {
                        // Use seed based on today's date for daily word consistency
                        val today = Calendar.getInstance().apply {
                            set(Calendar.HOUR_OF_DAY, 0)
                            set(Calendar.MINUTE, 0)
                            set(Calendar.SECOND, 0)
                            set(Calendar.MILLISECOND, 0)
                        }.timeInMillis
                        
                        val random = Random(today)
                        _featuredWord.value = filtered[random.nextInt(filtered.size)]
                    } else {
                        _featuredWord.value = null
                    }
                } else {
                    _featuredWord.value = null
                }
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

    fun updateAvatar(avatar: String) {
        viewModelScope.launch {
            repository.updateAvatar(avatar)
        }
    }

    fun clearAllData(onComplete: () -> Unit) {
        viewModelScope.launch {
            repository.clearAllData()
            onComplete()
        }
    }

    fun saveUser(name: String, onComplete: () -> Unit) {
        viewModelScope.launch {
            repository.updateUserName(name)
            onComplete()
        }
    }
}
