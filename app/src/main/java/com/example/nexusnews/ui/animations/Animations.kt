package com.example.nexusnews.ui.animations

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.ui.graphics.TransformOrigin

/**
 * Centralized animation specifications for NexusNews.
 * Follows Material Motion guidelines for consistent animations.
 */
object NexusAnimations {
    // Duration constants
    private const val DURATION_SHORT = 200
    private const val DURATION_MEDIUM = 300
    private const val DURATION_LONG = 400

    /**
     * List item enter animation - fade in + slide up.
     */
    val listItemEnter =
        fadeIn(animationSpec = tween(DURATION_MEDIUM, easing = FastOutSlowInEasing)) +
            slideInVertically(
                animationSpec = tween(DURATION_MEDIUM, easing = FastOutSlowInEasing),
                initialOffsetY = { it / 4 },
            )

    /**
     * List item exit animation - fade out + slide down.
     */
    val listItemExit =
        fadeOut(animationSpec = tween(DURATION_SHORT)) +
            slideOutVertically(
                animationSpec = tween(DURATION_SHORT),
                targetOffsetY = { it / 4 },
            )

    /**
     * Bookmark icon toggle animation - scale with bounce.
     */
    val bookmarkToggle =
        scaleIn(
            animationSpec =
                spring(
                    dampingRatio = Spring.DampingRatioMediumBouncy,
                    stiffness = Spring.StiffnessLow,
                ),
            transformOrigin = TransformOrigin.Center,
        )

    /**
     * Bookmark icon exit animation - scale out.
     */
    val bookmarkExit =
        scaleOut(
            animationSpec = tween(DURATION_SHORT),
            transformOrigin = TransformOrigin.Center,
        )

    /**
     * Fade in animation for general use.
     */
    val fadeIn = fadeIn(animationSpec = tween(DURATION_MEDIUM))

    /**
     * Fade out animation for general use.
     */
    val fadeOut = fadeOut(animationSpec = tween(DURATION_SHORT))

    /**
     * Spring animation spec for smooth, natural motion.
     */
    val springSpec =
        spring<Float>(
            dampingRatio = Spring.DampingRatioMediumBouncy,
            stiffness = Spring.StiffnessMedium,
        )
}
