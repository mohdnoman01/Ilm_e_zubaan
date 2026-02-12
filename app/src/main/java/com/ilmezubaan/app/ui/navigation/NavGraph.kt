package com.ilmezubaan.app.ui.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.ilmezubaan.app.ui.screens.*

@Composable
fun AppNavGraph() {
    val navController = rememberNavController()

    NavHost(
        navController = navController,
        startDestination = NavRoutes.HOME
    ) {

        composable(NavRoutes.HOME) {
            HomeScreen(
                onLanguageClick = {
                    navController.navigate(NavRoutes.LANGUAGE)
                },
                onLessonClick = { lesson ->
                    navController.navigate(
                        "${NavRoutes.PLAYER}/${lesson.title}/${lesson.type}"
                    )
                }
            )
        }

        composable(NavRoutes.LANGUAGE) {
            LanguageSelectScreen(
                onLanguageChosen = { language ->
                    navController.navigate("${NavRoutes.LESSONS}/$language")
                }
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
