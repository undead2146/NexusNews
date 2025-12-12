package com.example.nexusnews.domain.model

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Settings
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import java.time.LocalDate

/**
 * Data class representing search parameters for news articles.
 */
data class SearchQuery(
    val query: String,
    val category: String? = null,
    val fromDate: LocalDate? = null,
    val toDate: LocalDate? = null,
    val sortBy: SortType = SortType.PUBLISHED_AT,
)

/**
 * Enum defining available sort options for news articles.
 */
enum class SortType {
    /**
     * Sort by relevancy to the search query.
     */
    RELEVANCY,

    /**
     * Sort by article publish date (newest first).
     */
    PUBLISHED_AT,

    /**
     * Sort by popularity (most popular first).
     */
    POPULARITY,
}

/**
 * Enum defining news categories supported by NewsAPI.
 */
enum class NewsCategory(
    val value: String,
    val displayName: String,
    val icon: ImageVector,
    val accentColor: Color,
    val description: String,
) {
    BUSINESS(
        "business",
        "Business",
        Icons.Filled.Add,
        Color(0xFF1976D2),
        "Latest business and financial news",
    ),
    ENTERTAINMENT(
        "entertainment",
        "Entertainment",
        Icons.Filled.Favorite,
        Color(0xFF7B1FA2),
        "Movies, music, and celebrity news",
    ),
    GENERAL(
        "general",
        "General",
        Icons.Filled.Home,
        Color(0xFF616161),
        "General news and current events",
    ),
    HEALTH(
        "health",
        "Health",
        Icons.Filled.Check,
        Color(0xFF388E3C),
        "Health, wellness, and medical news",
    ),
    SCIENCE(
        "science",
        "Science",
        Icons.Filled.Settings,
        Color(0xFF00796B),
        "Scientific discoveries and research",
    ),
    SPORTS(
        "sports",
        "Sports",
        Icons.Filled.Edit,
        Color(0xFFF57C00),
        "Sports news and updates",
    ),
    TECHNOLOGY(
        "technology",
        "Technology",
        Icons.Filled.Delete,
        Color(0xFF303F9F),
        "Tech news and innovations",
    ),
}
