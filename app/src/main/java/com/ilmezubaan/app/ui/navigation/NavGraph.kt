package com.ilmezubaan.app.ui.navigation

import android.net.Uri
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.google.firebase.database.FirebaseDatabase
import com.ilmezubaan.app.data.local.AppDatabase
import com.ilmezubaan.app.data.repository.ConceptRepository
import com.ilmezubaan.app.data.repository.UserStatsRepository
import com.ilmezubaan.app.ui.screens.AudioVideoScreen
import com.ilmezubaan.app.ui.screens.HomeScreen
import com.ilmezubaan.app.ui.screens.LanguageSelectScreen
import com.ilmezubaan.app.ui.screens.LessonListScreen
import com.ilmezubaan.app.ui.screens.LiteracyScreen
import com.ilmezubaan.app.ui.screens.LoginScreen
import com.ilmezubaan.app.ui.screens.PrivacySettingsScreen
import com.ilmezubaan.app.ui.screens.ProfileScreen
import com.ilmezubaan.app.ui.screens.VocabularyScreen
import com.ilmezubaan.app.ui.viewmodel.ConceptViewModel
import com.ilmezubaan.app.ui.viewmodel.ConceptViewModelFactory
import com.ilmezubaan.app.ui.viewmodel.HomeViewModel
import com.ilmezubaan.app.ui.viewmodel.HomeViewModelFactory
import com.ilmezubaan.app.ui.viewmodel.LanguageViewModel
import com.ilmezubaan.app.ui.viewmodel.LanguageViewModelFactory
import kotlinx.coroutines.launch

@Composable
fun AppNavGraph() {
    val navController = rememberNavController()
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    
    val database = AppDatabase.getDatabase(context)
    val userStatsRepository = UserStatsRepository(
        database.userStatsDao(),
        database.conceptDao(),
        database.languageMetadataDao()
    )
    
    val firebaseDatabase = FirebaseDatabase.getInstance("https://ilm-e-zubaan-default-rtdb.asia-southeast1.firebasedatabase.app/")
    
    val conceptRepository = ConceptRepository(
        conceptDao = database.conceptDao(),
        metadataDao = database.languageMetadataDao(),
        firebaseDatabase = firebaseDatabase
    )
    
    val languageViewModel: LanguageViewModel = viewModel(
        factory = LanguageViewModelFactory(userStatsRepository)
    )
    val homeViewModel: HomeViewModel = viewModel(
        factory = HomeViewModelFactory(userStatsRepository)
    )
    val conceptViewModel: ConceptViewModel = viewModel(
        factory = ConceptViewModelFactory(conceptRepository)
    )

    NavHost(
        navController = navController,
        startDestination = NavRoutes.LOGIN
    ) {
        composable(NavRoutes.LOGIN) {
            val userStats by homeViewModel.userStats.collectAsState()
            
            LoginScreen(
                onLoginSuccess = { isNewUser ->
                    if (isNewUser) {
                        navController.navigate(NavRoutes.LANGUAGE_NATIVE) {
                            popUpTo(NavRoutes.LOGIN) { inclusive = true }
                        }
                    } else if (userStats?.selectedLanguageName != null && userStats?.nativeLanguageName != null) {
                        navController.navigate(NavRoutes.HOME) {
                            popUpTo(NavRoutes.LOGIN) { inclusive = true }
                        }
                    } else {
                        // Fallback if metadata is missing
                        navController.navigate(NavRoutes.LANGUAGE_NATIVE) {
                            popUpTo(NavRoutes.LOGIN) { inclusive = true }
                        }
                    }
                }
            )
        }

        composable(NavRoutes.HOME) {
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

                languageViewModel = languageViewModel,
                homeViewModel = homeViewModel
            )
        }

        composable(NavRoutes.LANGUAGE_NATIVE) {
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
            val userStats by homeViewModel.userStats.collectAsState()
            ProfileScreen(
                userStats = userStats ?: com.ilmezubaan.app.data.local.entities.UserStats(),
                onBack = { navController.popBackStack() },
                onLogout = {
                    navController.navigate(NavRoutes.LOGIN) {
                        popUpTo(0) { inclusive = true }
                    }
                },
                onPrivacySettingsClick = {
                    navController.navigate(NavRoutes.PRIVACY_SETTINGS)
                },
                onClearData = {
                    scope.launch {
                        userStatsRepository.clearAllData()
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

        composable(
            route = "${NavRoutes.LESSONS}/{language}",
            arguments = listOf(
                navArgument("language") { type = NavType.StringType }
            )
        ) { backStackEntry ->
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
