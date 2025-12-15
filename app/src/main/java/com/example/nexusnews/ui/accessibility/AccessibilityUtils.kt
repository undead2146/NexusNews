package com.example.nexusnews.ui.accessibility

import androidx.compose.ui.semantics.CustomAccessibilityAction
import androidx.compose.ui.semantics.SemanticsPropertyReceiver
import androidx.compose.ui.semantics.customActions

/**
 * Utility functions for accessibility features.
 */
object AccessibilityUtils {
    /**
     * Minimum touch target size in dp as per Material Design guidelines.
     */
    const val MIN_TOUCH_TARGET_SIZE_DP = 48

    /**
     * Adds custom accessibility actions to a composable.
     *
     * @param actions List of custom actions with labels and handlers
     */
    fun SemanticsPropertyReceiver.addCustomActions(vararg actions: Pair<String, () -> Boolean>) {
        customActions =
            actions.map { (label, action) ->
                CustomAccessibilityAction(label, action)
            }
    }

    /**
     * Formats article metadata for screen readers.
     *
     * @param source Article source
     * @param publishedDate Published date string
     * @return Formatted string for accessibility
     */
    fun formatArticleMetadata(
        source: String,
        publishedDate: String,
    ): String = "From $source, published $publishedDate"

    /**
     * Formats bookmark status for screen readers.
     *
     * @param isBookmarked Whether article is bookmarked
     * @param isFavorite Whether article is favorite
     * @return Formatted string for accessibility
     */
    fun formatBookmarkStatus(
        isBookmarked: Boolean,
        isFavorite: Boolean = false,
    ): String =
        when {
            isFavorite -> "Bookmarked and marked as favorite"
            isBookmarked -> "Bookmarked"
            else -> "Not bookmarked"
        }
}
