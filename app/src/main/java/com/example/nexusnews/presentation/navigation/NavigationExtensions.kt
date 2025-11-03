@file:Suppress("TooGenericExceptionCaught")

package com.example.nexusnews.presentation.navigation

import androidx.navigation.NavController
import androidx.navigation.NavGraph.Companion.findStartDestination
import timber.log.Timber

/**
 * Navigate to a screen with single top behavior.
 * Prevents multiple copies of the same screen in the back stack.
 */
fun NavController.navigateSingleTop(route: String) {
    try {
        navigate(route) {
            // Pop up to the start destination and save state
            popUpTo(graph.findStartDestination().id) {
                saveState = true
            }
            // Avoid multiple copies
            launchSingleTop = true
            // Restore state when returning
            restoreState = true
        }
    } catch (e: Throwable) {
        Timber.e(e, "Navigation error to route: $route")
    }
}

/**
 * Navigate to article detail screen.
 */
fun NavController.navigateToArticleDetail(articleId: String) {
    navigate(Screen.ArticleDetail.createRoute(articleId))
}

/**
 * Safe pop back stack with error handling.
 */
fun NavController.safePopBackStack(): Boolean =
    try {
        popBackStack()
    } catch (e: Throwable) {
        Timber.e(e, "Error popping back stack")
        false
    }
