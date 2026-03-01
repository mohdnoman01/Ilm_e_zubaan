package com.ilmezubaan.app.ui.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.google.firebase.firestore.FirebaseFirestore
import com.ilmezubaan.app.data.local.AppDatabase
import com.ilmezubaan.app.data.repository.ConceptRepository
import com.ilmezubaan.app.data.repository.UserStatsRepository
import com.ilmezubaan.app.ui.screens.AudioVideoScreen
import com.ilmezubaan.app.ui.screens.HomeScreen
import com.ilmezubaan.app.ui.screens.LanguageSelectScreen
import com.ilmezubaan.app.ui.screens.LessonListScreen
import com.ilmezubaan.app.ui.screens.LiteracyScreen
import com.ilmezubaan.app.ui.screens.LoginScreen
import com.ilmezubaan.app.ui.screens.ProfileScreen
import com.ilmezubaan.app.ui.screens.VocabularyScreen
import com.ilmezubaan.app.ui.viewmodel.ConceptViewModel
import com.ilmezubaan.app.ui.viewmodel.ConceptViewModelFactory
import com.ilmezubaan.app.ui.viewmodel.HomeViewModel
import com.ilmezubaan.app.ui.viewmodel.HomeViewModelFactory
import com.ilmezubaan.app.ui.viewmodel.LanguageViewModel

@Composable
fun AppNavGraph() {
    val navController = rememberNavController()
    val context = LocalContext.current
    
    val database = AppDatabase.getDatabase(context)
    val userStatsRepository = UserStatsRepository(database.userStatsDao())
    val conceptRepository = ConceptRepository(
        conceptDao = database.conceptDao(),
        metadataDao = database.languageMetadataDao(),
        firestore = FirebaseFirestore.getInstance()
    )
    
    val languageViewModel: LanguageViewModel = viewModel()
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
            LoginScreen(
                onLoginSuccess = {
                    navController.navigate(NavRoutes.HOME) {
                        popUpTo(NavRoutes.LOGIN) { inclusive = true }
                    }
                }
            )
        }

        composable(NavRoutes.HOME) {
            HomeScreen(
                onLanguageClick = {
                    navController.navigate(NavRoutes.LANGUAGE)
                },
                onLessonClick = { lesson ->
                    navController.navigate(
                        "${NavRoutes.PLAYER}/${lesson.title}/${lesson.type}"
                    )
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

        composable(NavRoutes.VOCABULARY) {
            val selectedLanguage by languageViewModel.selectedLanguage.collectAsState()
            VocabularyScreen(
                language = selectedLanguage.name,
                onBack = { navController.popBackStack() },
                onLessonClick = { lesson ->
                    navController.navigate(
                        "${NavRoutes.PLAYER}/${lesson.title}/${lesson.type}"
                    )
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
                    navController.navigate(
                        "${NavRoutes.PLAYER}/${lesson.title}/${lesson.type}"
                    )
                }
            )
        }

        composable(NavRoutes.PROFILE) {
            ProfileScreen(
                onBack = { navController.popBackStack() }
            )
        }

        composable(NavRoutes.LANGUAGE) {
            LanguageSelectScreen(
                onLanguageChosen = { languageName ->
                    navController.navigate("${NavRoutes.LESSONS}/$languageName")
                },
                viewModel = languageViewModel
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
                    navController.navigate(
                        "${NavRoutes.PLAYER}/${lesson.title}/${lesson.type}"
                    )
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
