package com.example.nexusnews.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Bookmark
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.outlined.BookmarkBorder
import androidx.compose.material.icons.outlined.FavoriteBorder
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SwipeToDismissBox
import androidx.compose.material3.SwipeToDismissBoxValue
import androidx.compose.material3.rememberSwipeToDismissBoxState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.unit.dp
import com.example.nexusnews.domain.model.Article

/**
 * Swipeable article item with bookmark and favorite actions.
 *
 * Swipe right: Toggle bookmark
 * Swipe left: Toggle favorite (only if already bookmarked)
 *
 * @param article The article to display
 * @param isBookmarked Whether the article is bookmarked
 * @param isFavorite Whether the article is marked as favorite
 * @param onBookmarkToggle Callback when bookmark is toggled
 * @param onFavoriteToggle Callback when favorite is toggled (only if bookmarked)
 * @param onClick Callback when article is clicked
 * @param modifier Modifier for customization
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SwipeableArticleItem(
    article: Article,
    isBookmarked: Boolean,
    isFavorite: Boolean,
    onBookmarkToggle: () -> Unit,
    onFavoriteToggle: () -> Unit,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val haptic = LocalHapticFeedback.current

    val dismissState =
        rememberSwipeToDismissBoxState(
            confirmValueChange = { value ->
                when (value) {
                    SwipeToDismissBoxValue.StartToEnd -> {
                        // Swipe right - toggle bookmark
                        haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                        onBookmarkToggle()
                        false // Don't dismiss, just trigger action
                    }
                    SwipeToDismissBoxValue.EndToStart -> {
                        // Swipe left - toggle favorite
                        haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                        onFavoriteToggle()
                        false // Don't dismiss, just trigger action
                    }
                    SwipeToDismissBoxValue.Settled -> false
                }
            },
        )

    // Reset swipe state after action
    LaunchedEffect(dismissState.currentValue) {
        if (dismissState.currentValue != SwipeToDismissBoxValue.Settled) {
            dismissState.reset()
        }
    }

    SwipeToDismissBox(
        state = dismissState,
        modifier = modifier,
        backgroundContent = {
            SwipeBackground(
                swipeDirection = dismissState.targetValue,
                isBookmarked = isBookmarked,
                isFavorite = isFavorite,
            )
        },
        content = {
            ArticleItem(
                article = article,
                onClick = onClick,
                isBookmarked = isBookmarked,
                onBookmarkClick = onBookmarkToggle,
            )
        },
    )
}

/**
 * Background shown during swipe gesture.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun SwipeBackground(
    swipeDirection: SwipeToDismissBoxValue,
    isBookmarked: Boolean,
    isFavorite: Boolean,
) {
    val backgroundColor =
        when (swipeDirection) {
            SwipeToDismissBoxValue.StartToEnd -> {
                // Swipe right - bookmark action
                if (isBookmarked) {
                    MaterialTheme.colorScheme.errorContainer
                } else {
                    MaterialTheme.colorScheme.primaryContainer
                }
            }
            SwipeToDismissBoxValue.EndToStart -> {
                // Swipe left - favorite action
                if (isFavorite) {
                    MaterialTheme.colorScheme.surfaceVariant
                } else {
                    MaterialTheme.colorScheme.tertiaryContainer
                }
            }
            SwipeToDismissBoxValue.Settled -> Color.Transparent
        }

    val icon =
        when (swipeDirection) {
            SwipeToDismissBoxValue.StartToEnd -> {
                if (isBookmarked) {
                    Icons.Outlined.BookmarkBorder // Remove bookmark
                } else {
                    Icons.Filled.Bookmark // Add bookmark
                }
            }
            SwipeToDismissBoxValue.EndToStart -> {
                // Swipe left - toggle favorite
                if (isFavorite) {
                    Icons.Outlined.FavoriteBorder // Remove favorite
                } else {
                    Icons.Filled.Favorite // Add favorite
                }
            }
            SwipeToDismissBoxValue.Settled -> null
        }

    val alignment =
        when (swipeDirection) {
            SwipeToDismissBoxValue.StartToEnd -> Alignment.CenterStart
            SwipeToDismissBoxValue.EndToStart -> Alignment.CenterEnd
            SwipeToDismissBoxValue.Settled -> Alignment.Center
        }

    Box(
        modifier =
            Modifier
                .fillMaxSize()
                .background(backgroundColor)
                .padding(horizontal = 24.dp),
        contentAlignment = alignment,
    ) {
        AnimatedVisibility(
            visible = icon != null && swipeDirection != SwipeToDismissBoxValue.Settled,
            enter = fadeIn(animationSpec = tween(200)),
            exit = fadeOut(animationSpec = tween(200)),
        ) {
            icon?.let {
                Icon(
                    imageVector = it,
                    contentDescription = null,
                    tint =
                        when (swipeDirection) {
                            SwipeToDismissBoxValue.StartToEnd -> {
                                if (isBookmarked) {
                                    MaterialTheme.colorScheme.onErrorContainer
                                } else {
                                    MaterialTheme.colorScheme.onPrimaryContainer
                                }
                            }
                            SwipeToDismissBoxValue.EndToStart -> {
                                MaterialTheme.colorScheme.onTertiaryContainer
                            }
                            SwipeToDismissBoxValue.Settled -> MaterialTheme.colorScheme.onSurface
                        },
                )
            }
        }
    }
}
