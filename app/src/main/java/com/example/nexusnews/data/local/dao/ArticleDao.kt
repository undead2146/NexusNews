package com.example.nexusnews.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.nexusnews.data.local.entity.ArticleEntity
import kotlinx.coroutines.flow.Flow
import java.time.LocalDateTime

/**
 * Data Access Object for article caching operations.
 * Provides methods for storing and retrieving cached articles.
 */
@Dao
interface ArticleDao {
    /**
     * Gets all cached articles ordered by publish date.
     */
    @Query("SELECT * FROM articles ORDER BY published_at DESC")
    fun getAllArticles(): Flow<List<ArticleEntity>>

    /**
     * Gets a single article by ID.
     */
    @Query("SELECT * FROM articles WHERE id = :articleId")
    suspend fun getArticleById(articleId: String): ArticleEntity?

    /**
     * Inserts or replaces articles in the cache.
     */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertArticles(articles: List<ArticleEntity>)

    /**
     * Inserts or replaces a single article.
     */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertArticle(article: ArticleEntity)

    /**
     * Deletes articles older than the specified threshold.
     * Used for cache cleanup.
     */
    @Query("DELETE FROM articles WHERE cached_at < :threshold")
    suspend fun deleteOldArticles(threshold: LocalDateTime)

    /**
     * Clears all cached articles.
     */
    @Query("DELETE FROM articles")
    suspend fun clearAll()
}
