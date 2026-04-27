package com.ilmezubaan.app.ui.navigation

import android.net.Uri
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.viewmodel.compose.viewModel
import com.google.gson.Gson
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import androidx.hilt.navigation.compose.hiltViewModel
import com.ilmezubaan.app.ui.screens.*
import com.ilmezubaan.app.data.repository.UserStatsRepository
import com.ilmezubaan.app.ui.viewmodel.ConceptViewModel
import com.ilmezubaan.app.ui.viewmodel.HomeViewModel
import com.ilmezubaan.app.ui.viewmodel.LanguageViewModel
import com.ilmezubaan.app.ui.viewmodel.WordInsightViewModel
import kotlinx.coroutines.launch

@Composable
fun AppNavGraph() {
    val navController = rememberNavController()

    NavHost(
        navController = navController,
        startDestination = NavRoutes.LOGIN
    ) {
        composable(NavRoutes.LOGIN) {
            val homeViewModel: HomeViewModel = hiltViewModel()
            val userStats by homeViewModel.userStats.collectAsState()
            
            LoginScreen(
                onLoginSuccess = { isNewUser ->
                    if (isNewUser) {
                        navController.navigate(NavRoutes.LANGUAGE_NATIVE) {
                            popUpTo(NavRoutes.LOGIN) { inclusive = true }
                        }
                    } else if (userStats.selectedLanguageName != null && userStats.nativeLanguageName != null) {
                        navController.navigate(NavRoutes.HOME) {
                            popUpTo(NavRoutes.LOGIN) { inclusive = true }
                        }
                    } else {
                        navController.navigate(NavRoutes.LANGUAGE_NATIVE) {
                            popUpTo(NavRoutes.LOGIN) { inclusive = true }
                        }
                    }
                },
                homeViewModel = homeViewModel
            )
        }

        composable(NavRoutes.HOME) {
            val homeViewModel: HomeViewModel = hiltViewModel()
            val languageViewModel: LanguageViewModel = hiltViewModel()
            HomeScreen(
                onLanguageClick = {
                    navController.navigate(NavRoutes.LANGUAGE_LEARN)
                },
                onLessonClick = { language ->
                    navController.navigate("${NavRoutes.LESSONS}/$language")
                },
                onProfileClick = {
                    navController.navigate(NavRoutes.PROFILE)
                },
                onLiteracyClick = {
                    navController.navigate(NavRoutes.LITERACY)
                },
                onVocabularyClick = {
                    navController.navigate(NavRoutes.VOCABULARY)
                },
                onAIClick = {
                    navController.navigate(NavRoutes.AI)
                },
                languageViewModel = languageViewModel,
                homeViewModel = homeViewModel
            )
        }

        composable(NavRoutes.LANGUAGE_NATIVE) {
            val languageViewModel: LanguageViewModel = hiltViewModel()
            LanguageSelectScreen(
                title = "What is your native language?",
                subtitle = "We will use this to explain words to you",
                onLanguageChosen = { 
                    navController.navigate(NavRoutes.LANGUAGE_LEARN)
                },
                onSelect = { language ->
                    languageViewModel.setNativeLanguage(language)
                },
                viewModel = languageViewModel
            )
        }

        composable(NavRoutes.LANGUAGE_LEARN) {
            val languageViewModel: LanguageViewModel = hiltViewModel()
            LanguageSelectScreen(
                title = "What language do you want to learn?",
                subtitle = "Select the regional language you're interested in",
                onLanguageChosen = { 
                    navController.navigate(NavRoutes.HOME) {
                        popUpTo(NavRoutes.LANGUAGE_NATIVE) { inclusive = true }
                        popUpTo(NavRoutes.LANGUAGE_LEARN) { inclusive = true }
                    }
                },
                onSelect = { language ->
                    languageViewModel.selectLanguage(language)
                },
                viewModel = languageViewModel
            )
        }

        composable(NavRoutes.VOCABULARY) {
            val languageViewModel: LanguageViewModel = hiltViewModel()
            val conceptViewModel: ConceptViewModel = hiltViewModel()
            val selectedLanguage by languageViewModel.selectedLanguage.collectAsState()
            val nativeLanguage by languageViewModel.nativeLanguage.collectAsState()
            
            VocabularyScreen(
                language = selectedLanguage.name,
                nativeLanguage = nativeLanguage?.name ?: "English",
                onBack = { navController.popBackStack() },
                onLessonClick = { lesson ->
                    val encodedTitle = Uri.encode(lesson.title)
                    val encodedType = Uri.encode(lesson.type)
                    navController.navigate("${NavRoutes.PLAYER}/$encodedTitle/$encodedType")
                },
                conceptViewModel = conceptViewModel
            )
        }

        composable(NavRoutes.LITERACY) {
            val languageViewModel: LanguageViewModel = hiltViewModel()
            val selectedLanguage by languageViewModel.selectedLanguage.collectAsState()
            LiteracyScreen(
                language = selectedLanguage.name,
                onBack = { navController.popBackStack() },
                onLessonClick = { lesson ->
                    val encodedTitle = Uri.encode(lesson.title)
                    val encodedType = Uri.encode(lesson.type)
                    navController.navigate("${NavRoutes.PLAYER}/$encodedTitle/$encodedType")
                }
            )
        }

        composable(NavRoutes.PROFILE) {
            val homeViewModel: HomeViewModel = hiltViewModel()
            val userStats by homeViewModel.userStats.collectAsState()
            ProfileScreen(
                userStats = userStats,
                onBack = { navController.popBackStack() },
                onLogout = {
                    navController.navigate(NavRoutes.LOGIN) {
                        popUpTo(0) { inclusive = true }
                    }
                },
                onPrivacySettingsClick = {
                    navController.navigate(NavRoutes.PRIVACY_SETTINGS)
                },
                onUpdateAvatar = { homeViewModel.updateAvatar(it) },
                onClearData = {
                    homeViewModel.clearAllData {
                        navController.navigate(NavRoutes.LOGIN) {
                            popUpTo(0) { inclusive = true }
                        }
                    }
                }
            )
        }

        composable(NavRoutes.PRIVACY_SETTINGS) {
            PrivacySettingsScreen(
                onBack = { navController.popBackStack() }
            )
        }

        composable(NavRoutes.AI) {
            val languageViewModel: LanguageViewModel = hiltViewModel()
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

        composable(
            route = "${NavRoutes.LESSONS}/{language}",
            arguments = listOf(
                navArgument("language") { type = NavType.StringType }
            )
        ) { backStackEntry ->
            val conceptViewModel: ConceptViewModel = hiltViewModel()
            val language = backStackEntry.arguments?.getString("language") ?: "Unknown"

            LessonListScreen(
                language = language,
                onLessonClick = { lesson ->
                    val encodedTitle = Uri.encode(lesson.title)
                    val encodedType = Uri.encode(lesson.type)
                    navController.navigate("${NavRoutes.PLAYER}/$encodedTitle/$encodedType")
                },
                conceptViewModel = conceptViewModel
            )
        }

        composable(
            route = "${NavRoutes.PLAYER}/{title}/{type}",
            arguments = listOf(
                navArgument("title") { type = NavType.StringType },
                navArgument("type") { type = NavType.StringType }
            )
        ) { backStackEntry ->
            val languageViewModel: LanguageViewModel = hiltViewModel()
            val conceptViewModel: ConceptViewModel = hiltViewModel()
            val title = backStackEntry.arguments?.getString("title") ?: "Lesson"
            val type = backStackEntry.arguments?.getString("type") ?: "AUDIO"
            val selectedLanguage by languageViewModel.selectedLanguage.collectAsState()

            AudioVideoScreen(
                lessonTitle = title,
                lessonType = type,
                onBack = { navController.popBackStack() },
                conceptViewModel = conceptViewModel,
                language = selectedLanguage.name
            )
        }
    }
}
