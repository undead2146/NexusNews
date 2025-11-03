package com.example.nexusnews.domain.model

import java.time.LocalDateTime

/**
 * Core domain model representing a news article.
 * This model is independent of any framework or data source.
 */
data class Article(
    val id: String,
    val title: String,
    val description: String?,
    val content: String?,
    val url: String,
    val imageUrl: String?,
    val author: String?,
    val source: String,
    val publishedAt: LocalDateTime,
    val category: String?,
)
