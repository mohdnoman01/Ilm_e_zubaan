package com.ilmezubaan.app.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ilmezubaan.app.data.local.entities.UserStats
import com.ilmezubaan.app.data.model.Concept
import com.ilmezubaan.app.data.repository.ConceptRepository
import com.ilmezubaan.app.data.repository.UserStatsRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.util.Calendar
import java.util.Random

import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import javax.inject.Provider

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val repository: UserStatsRepository,
    private val conceptRepositoryProvider: Provider<ConceptRepository>
) : ViewModel() {

    val userStats: StateFlow<UserStats> = repository.userStats
        .map { it ?: UserStats() }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = UserStats()
        )

    val featuredWord: StateFlow<Concept?> by lazy {
        combine(conceptRepositoryProvider.get().allConcepts, userStats) { concepts, stats ->
            selectFeaturedWord(concepts, stats.selectedLanguageName)
        }
        .flowOn(Dispatchers.Default)
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = null
        )
    }

    fun refreshHomeData(force: Boolean = false) {
        viewModelScope.launch(Dispatchers.IO) {
            android.util.Log.d("HomeViewModel", "refreshHomeData started")
            repository.checkAndUpdateStreak()
            android.util.Log.d("HomeViewModel", "streak updated")
            conceptRepositoryProvider.get().syncConcepts(force)
            android.util.Log.d("HomeViewModel", "concepts synced")
        }
    }

    private fun selectFeaturedWord(concepts: List<Concept>, languageName: String?): Concept? {
        if (concepts.isEmpty() || languageName.isNullOrEmpty()) return null

        val filtered = concepts.filter { concept ->
            concept.languages.keys.any { it.equals(languageName, ignoreCase = true) }
        }

        if (filtered.isEmpty()) return null

        val today = Calendar.getInstance().apply {
            set(Calendar.HOUR_OF_DAY, 0)
            set(Calendar.MINUTE, 0)
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)
        }.timeInMillis

        return filtered[Random(today).nextInt(filtered.size)]
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
