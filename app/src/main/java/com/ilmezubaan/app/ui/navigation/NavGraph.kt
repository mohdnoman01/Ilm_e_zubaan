package com.ilmezubaan.app.ui.navigation

import android.net.Uri
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.toRoute
import com.ilmezubaan.app.ui.screens.*
import com.ilmezubaan.app.ui.viewmodel.ConceptViewModel
import com.ilmezubaan.app.ui.viewmodel.HomeViewModel
import com.ilmezubaan.app.ui.viewmodel.LanguageViewModel
import com.ilmezubaan.app.ui.viewmodel.WordInsightViewModel

@Composable
fun AppNavGraph() {
    val navController = rememberNavController()
    
    // Scoped to the entire activity to prevent redundant initialization
    val homeViewModel: HomeViewModel = hiltViewModel()
    val languageViewModel: LanguageViewModel = hiltViewModel()
    val conceptViewModel: ConceptViewModel = hiltViewModel()

    NavHost(
        navController = navController,
        startDestination = Route.Login
    ) {
        composable<Route.Login> {
            val userStats by homeViewModel.userStats.collectAsState()
            
            LoginScreen(
                onLoginSuccess = { isNewUser ->
                    if (isNewUser) {
                        navController.navigate(Route.LanguageNative) {
                            popUpTo(Route.Login) { inclusive = true }
                        }
                    } else if (userStats.selectedLanguageName != null && userStats.nativeLanguageName != null) {
                        navController.navigate(Route.Home) {
                            popUpTo(Route.Login) { inclusive = true }
                        }
                    } else {
                        navController.navigate(Route.LanguageNative) {
                            popUpTo(Route.Login) { inclusive = true }
                        }
                    }
                },
                homeViewModel = homeViewModel
            )
        }

        composable<Route.Home> {
            HomeScreen(
                onLanguageClick = {
                    navController.navigate(Route.LanguageLearn)
                },
                onLessonClick = { language ->
                    navController.navigate(Route.Lessons(language))
                },
                onProfileClick = {
                    navController.navigate(Route.Profile)
                },
                onLiteracyClick = {
                    navController.navigate(Route.Literacy)
                },
                onVocabularyClick = {
                    navController.navigate(Route.Vocabulary)
                },
                onAIClick = {
                    navController.navigate(Route.AI)
                },
                languageViewModel = languageViewModel,
                homeViewModel = homeViewModel
            )
        }

        composable<Route.LanguageNative> {
            LanguageSelectScreen(
                title = "What is your native language?",
                subtitle = "We will use this to explain words to you",
                onLanguageChosen = { 
                    navController.navigate(Route.LanguageLearn)
                },
                onSelect = { language ->
                    languageViewModel.setNativeLanguage(language)
                },
                viewModel = languageViewModel
            )
        }

        composable<Route.LanguageLearn> {
            LanguageSelectScreen(
                title = "What language do you want to learn?",
                subtitle = "Select the regional language you're interested in",
                onLanguageChosen = { 
                    navController.navigate(Route.Home) {
                        popUpTo(Route.LanguageNative) { inclusive = true }
                        popUpTo(Route.LanguageLearn) { inclusive = true }
                    }
                },
                onSelect = { language ->
                    languageViewModel.selectLanguage(language)
                },
                viewModel = languageViewModel
            )
        }

        composable<Route.Vocabulary> {
            val selectedLanguage by languageViewModel.selectedLanguage.collectAsState()
            val nativeLanguage by languageViewModel.nativeLanguage.collectAsState()
            
            VocabularyScreen(
                language = selectedLanguage.name,
                nativeLanguage = nativeLanguage?.name ?: "English",
                onBack = { navController.popBackStack() },
                conceptViewModel = conceptViewModel
            )
        }

        composable<Route.Literacy> {
            val selectedLanguage by languageViewModel.selectedLanguage.collectAsState()
            LiteracyScreen(
                language = selectedLanguage.name,
                onBack = { navController.popBackStack() },
                onLessonClick = { lesson ->
                    navController.navigate(Route.Player(lesson.title, lesson.type, lesson.audioUrl))
                }
            )
        }

        composable<Route.Profile> {
            val userStats by homeViewModel.userStats.collectAsState()
            ProfileScreen(
                userStats = userStats,
                onBack = { navController.popBackStack() },
                onLogout = {
                    navController.navigate(Route.Login) {
                        popUpTo(0) { inclusive = true }
                    }
                },
                onPrivacySettingsClick = {
                    navController.navigate(Route.PrivacySettings)
                },
                onUpdateAvatar = { homeViewModel.updateAvatar(it) },
                onClearData = {
                    homeViewModel.clearAllData {
                        navController.navigate(Route.Login) {
                            popUpTo(0) { inclusive = true }
                        }
                    }
                }
            )
        }

        composable<Route.PrivacySettings> {
            PrivacySettingsScreen(
                onBack = { navController.popBackStack() }
            )
        }

        composable<Route.AI> {
            val wordInsightViewModel: WordInsightViewModel = hiltViewModel()
            val selectedLanguage by languageViewModel.selectedLanguage.collectAsState()
            val nativeLanguage by languageViewModel.nativeLanguage.collectAsState()
            AIScreen(
                onBack = { navController.popBackStack() },
                learningLanguage = selectedLanguage.name,
                nativeLanguage = nativeLanguage?.name ?: "Urdu",
                viewModel = wordInsightViewModel
            )
        }

        composable<Route.Lessons> { backStackEntry ->
            val route: Route.Lessons = backStackEntry.toRoute()
            
            LessonListScreen(
                language = route.language,
                onLessonClick = { lesson ->
                    navController.navigate(Route.Player(lesson.title, lesson.type, lesson.audioUrl))
                },
                conceptViewModel = conceptViewModel
            )
        }

        composable<Route.Player> { backStackEntry ->
            val route: Route.Player = backStackEntry.toRoute()
            val selectedLanguage by languageViewModel.selectedLanguage.collectAsState()

            AudioVideoScreen(
                lessonTitle = route.title,
                lessonType = route.type,
                onBack = { navController.popBackStack() },
                conceptViewModel = conceptViewModel,
                language = selectedLanguage.name,
                audioUrl = route.audioUrl
            )
        }
    }
}
