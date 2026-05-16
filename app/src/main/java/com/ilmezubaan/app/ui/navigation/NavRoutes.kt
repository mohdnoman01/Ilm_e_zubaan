package com.ilmezubaan.app.ui.navigation

import kotlinx.serialization.Serializable

@Serializable
sealed interface Route {
    @Serializable
    data object Login : Route
    
    @Serializable
    data object Home : Route
    
    @Serializable
    data object LanguageNative : Route
    
    @Serializable
    data object LanguageLearn : Route
    
    @Serializable
    data class Lessons(val language: String) : Route
    
    @Serializable
    data class Player(
        val title: String, 
        val type: String,
        val audioUrl: String? = null
    ) : Route
    
    @Serializable
    data object Profile : Route
    
    @Serializable
    data object Literacy : Route
    
    @Serializable
    data object Vocabulary : Route
    
    @Serializable
    data object AI : Route
    
    @Serializable
    data object Explore : Route
    
    @Serializable
    data object PrivacySettings : Route
}

// Legacy object for compatibility during transition if needed
object NavRoutes {
    const val LOGIN = "login"
    const val HOME = "home"
    const val LANGUAGE_NATIVE = "language_native"
    const val LANGUAGE_LEARN = "language_learn"
    const val LESSONS = "lessons"
    const val PLAYER = "player"
    const val PROFILE = "profile"
    const val LITERACY = "literacy"
    const val VOCABULARY = "vocabulary"
    const val AI = "ai"
    const val EXPLORE = "explore"
    const val PRIVACY_SETTINGS = "privacy_settings"
}
