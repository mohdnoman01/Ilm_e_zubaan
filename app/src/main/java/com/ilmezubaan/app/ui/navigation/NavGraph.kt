package com.ilmezubaan.app.ui.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.ilmezubaan.app.data.local.AppDatabase
import com.ilmezubaan.app.data.repository.UserStatsRepository
import com.ilmezubaan.app.ui.screens.*
import com.ilmezubaan.app.ui.viewmodel.LanguageViewModel
import com.ilmezubaan.app.ui.viewmodel.HomeViewModel
import com.ilmezubaan.app.ui.viewmodel.HomeViewModelFactory

@Composable
fun AppNavGraph() {
    val navController = rememberNavController()
    val context = LocalContext.current
    
    // Set up database and repository for HomeViewModel
    val database = AppDatabase.getDatabase(context)
    val repository = UserStatsRepository(database.userStatsDao())
    
    // Create the ViewModels once at the NavHost level to share them across screens
    val languageViewModel: LanguageViewModel = viewModel()
    val homeViewModel: HomeViewModel = viewModel(
        factory = HomeViewModelFactory(repository)
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
                languageViewModel = languageViewModel,
                homeViewModel = homeViewModel
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
                }
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

            AudioVideoScreen(
                lessonTitle = title,
                lessonType = type,
                onBack = { navController.popBackStack() }
            )
        }
    }
}
