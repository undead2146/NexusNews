package com.example.nexusnews.domain.model

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
) {
    BUSINESS("business", "Business"),
    ENTERTAINMENT("entertainment", "Entertainment"),
    GENERAL("general", "General"),
    HEALTH("health", "Health"),
    SCIENCE("science", "Science"),
    SPORTS("sports", "Sports"),
    TECHNOLOGY("technology", "Technology"),
}
