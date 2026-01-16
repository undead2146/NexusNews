package com.example.nexusnews.presentation.navigation

/**
 * Sealed class defining all navigation destinations.
 * Type-safe navigation routes with parameters.
 */
sealed class Screen(
    val route: String,
) {
    data object Home : Screen("home")

    data object Search : Screen("search")

    data object Bookmarks : Screen("bookmarks")

    data object Settings : Screen("settings")

    data object ArticleDetail : Screen("article/{articleId}") {
        fun createRoute(articleId: String) = "article/$articleId"
    }

    data object ChatAssistant : Screen("chat")

    data object Recommendations : Screen("recommendations")

    companion object {
        /**
         * Returns all bottom navigation screens.
         */
        fun bottomNavScreens() = listOf(Home, Search, Bookmarks, Settings)
    }
}
