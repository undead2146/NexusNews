package com.example.nexusnews.domain.repository

import com.example.nexusnews.domain.model.Article
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
     * @return Flow emitting Result states (Loading, Success, Error)
     */
    fun getArticles(forceRefresh: Boolean = false): Flow<Result<List<Article>>>

    /**
     * Fetches a single article by ID.
     */
    fun getArticleById(id: String): Flow<Result<Article>>

    /**
     * Searches articles by query.
     */
    fun searchArticles(query: String): Flow<Result<List<Article>>>
}
