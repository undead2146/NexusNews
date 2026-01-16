package com.example.nexusnews.domain.repository

import com.example.nexusnews.domain.model.Article
import com.example.nexusnews.domain.model.NewsCategory
import com.example.nexusnews.util.Result
import kotlinx.coroutines.flow.Flow

/**
 * Repository interface defining contract for news data operations.
 * Following the Dependency Inversion Principle - domain layer defines the contract.
 */
interface NewsRepository {
    /**
     * Fetches articles with offline-first strategy.
     * @param forceRefresh If true, bypasses cache and fetches from network
     * @param category Optional category filter (null for all categories)
     * @return Flow emitting Result states (Loading, Success, Error)
     */
    fun getArticles(
        forceRefresh: Boolean = false,
        category: NewsCategory? = null
    ): Flow<Result<List<Article>>>

    /**
     * Fetches a specific page of articles.
     * @param page Page number (1-indexed)
     * @param category Optional category filter (null for all categories)
     * @return Flow emitting Result states (Loading, Success, Error)
     */
    fun getArticlesPage(
        page: Int,
        category: NewsCategory? = null
    ): Flow<Result<List<Article>>>

    /**
     * Fetches a single article by ID.
     */
    fun getArticleById(id: String): Flow<Result<Article>>

    /**
     * Searches articles by query.
     */
    fun searchArticles(query: String): Flow<Result<List<Article>>>

    // Bookmark operations

    /**
     * Gets all bookmarked articles.
     */
    fun getBookmarks(): Flow<Result<List<Article>>>

    /**
     * Gets only favorite articles.
     */
    fun getFavorites(): Flow<Result<List<Article>>>

    /**
     * Checks if an article is bookmarked.
     * Returns Flow for reactive UI updates.
     */
    fun isBookmarked(articleId: String): Flow<Boolean>

    fun isFavorite(articleId: String): Flow<Boolean>

    /**
     * Adds an article to bookmarks.
     */
    suspend fun addBookmark(article: Article)

    /**
     * Removes an article from bookmarks.
     */
    suspend fun removeBookmark(articleId: String)

    /**
     * Toggles the favorite status of an article.
     * If the article is not bookmarked, it will be added to bookmarks first.
     */
    suspend fun toggleFavorite(article: Article)
}
