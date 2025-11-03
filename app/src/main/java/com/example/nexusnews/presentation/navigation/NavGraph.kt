package com.example.nexusnews.presentation.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument

/**
 * Main navigation graph for the application.
 * Defines all composable destinations and navigation logic.
 */
@Composable
fun nexusNewsNavGraph(
    navController: NavHostController,
    modifier: Modifier = Modifier,
    startDestination: String = Screen.Home.route
) {
    NavHost(
        navController = navController,
        startDestination = startDestination,
        modifier = modifier
    ) {
        // Home Screen
        composable(Screen.Home.route) {
            // HomeScreen() - Implement later
        }
        
        // Search Screen
        composable(Screen.Search.route) {
            // SearchScreen() - Implement later
        }
        
        // Bookmarks Screen
        composable(Screen.Bookmarks.route) {
            // BookmarksScreen() - Implement later
        }
        
        // Settings Screen
        composable(Screen.Settings.route) {
            // SettingsScreen() - Implement later
        }
        
        // Article Detail Screen
        composable(
            route = Screen.ArticleDetail.route,
            arguments = listOf(
                navArgument("articleId") {
                    type = NavType.StringType
                }
            )
        ) { backStackEntry ->
            val articleId = backStackEntry.arguments?.getString("articleId")
            check(articleId != null) { "articleId is required" }
            // ArticleDetailScreen(articleId) - Implement later
        }
    }
}
