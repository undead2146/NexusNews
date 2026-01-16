package com.example.nexusnews.data.mapper

import com.example.nexusnews.data.remote.dto.NewsApiArticle
import com.example.nexusnews.domain.model.Article
import timber.log.Timber
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.time.format.DateTimeParseException
import java.util.UUID

/**
 * Mapper for converting NewsAPI DTOs to domain models.
 */
object ArticleMapper {
    /**
     * Converts a NewsApiArticle to an Article domain model.
     */
    fun toDomain(newsApiArticle: NewsApiArticle): Article =
        Article(
            id = generateId(newsApiArticle),
            title = newsApiArticle.title,
            description = newsApiArticle.description,
            content = newsApiArticle.content,
            url = newsApiArticle.url,
            imageUrl = newsApiArticle.urlToImage,
            author = newsApiArticle.author,
            source = newsApiArticle.source.name,
            publishedAt = parsePublishedAt(newsApiArticle.publishedAt),
            category = null, // NewsAPI doesn't provide categories in article responses
            tags = extractTags(newsApiArticle.title, newsApiArticle.description),
        )

    /**
     * Converts a list of NewsApiArticle to a list of Article domain models.
     */
    fun toDomainList(newsApiArticles: List<NewsApiArticle>): List<Article> = newsApiArticles.map { toDomain(it) }

    /**
     * Converts a PopulatedBookmark to an Article domain model.
     */
    fun toDomain(populatedBookmark: com.example.nexusnews.data.local.model.PopulatedBookmark): Article =
        Article(
            id = populatedBookmark.article.id,
            title = populatedBookmark.article.title,
            description = populatedBookmark.article.description,
            content = populatedBookmark.article.content,
            url = populatedBookmark.article.url,
            imageUrl = populatedBookmark.article.imageUrl,
            author = populatedBookmark.article.author,
            source = populatedBookmark.article.source,
            publishedAt = populatedBookmark.article.publishedAt,
            category = populatedBookmark.article.category,
            tags = populatedBookmark.article.tags,
            isFavorite = populatedBookmark.isFavorite,
        )

    /**
     * Converts a list of PopulatedBookmark to a list of Article domain models.
     */
    fun toDomainListFromBookmarks(populatedBookmarks: List<com.example.nexusnews.data.local.model.PopulatedBookmark>): List<Article> =
        populatedBookmarks.map { toDomain(it) }

    /**
     * Generates a unique ID for the article based on URL.
     * Uses URL hash to ensure consistency across API calls.
     */
    private fun generateId(newsApiArticle: NewsApiArticle): String = UUID.nameUUIDFromBytes(newsApiArticle.url.toByteArray()).toString()

    /**
     * Extracts tags from article title and description.
     * Uses simple pattern matching to identify capitalized words/phrases as potential tags.
     *
     * @param title Article title
     * @param description Article description (nullable)
     * @return List of up to 5 unique tags
     */
    private fun extractTags(
        title: String,
        description: String?,
    ): List<String> {
        val text = "$title ${description ?: ""}"
        // Pattern matches capitalized words and multi-word phrases (e.g., "Climate Change", "United States")
        val tagPattern = "\\b[A-Z][a-z]+(?:\\s+[A-Z][a-z]+)*\\b".toRegex()

        return tagPattern
            .findAll(text)
            .map { it.value }
            .distinct()
            .filter { it.length > 2 } // Filter out very short tags
            .take(5)
            .toList()
    }

    /**
     * Parses the publishedAt string from NewsAPI to LocalDateTime.
     * Handles ISO 8601 format with timezone offset.
     */
    private fun parsePublishedAt(publishedAt: String): LocalDateTime =
        try {
            // NewsAPI returns ISO 8601 format like "2025-11-14T12:00:02Z"
            LocalDateTime.parse(publishedAt, DateTimeFormatter.ISO_OFFSET_DATE_TIME)
        } catch (e: DateTimeParseException) {
            // detekt:ignore TooGenericExceptionCaught
            // Fallback for different formats
            Timber.e(e, "Failed to parse publishedAt with ISO_OFFSET_DATE_TIME: $publishedAt")
            try {
                LocalDateTime.parse(publishedAt, DateTimeFormatter.ISO_LOCAL_DATE_TIME)
            } catch (e2: DateTimeParseException) {
                // detekt:ignore TooGenericExceptionCaught
                // If parsing fails, use current time as fallback
                Timber.e(e2, "Failed to parse publishedAt: $publishedAt")
                LocalDateTime.now()
            }
        }
}
