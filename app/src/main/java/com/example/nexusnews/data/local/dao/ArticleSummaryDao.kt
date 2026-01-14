package com.example.nexusnews.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.nexusnews.data.local.entity.ArticleSummaryEntity
import kotlinx.coroutines.flow.Flow

/**
 * Data Access Object for article summary caching operations.
 * Provides methods for storing and retrieving AI-generated summaries.
 */
@Dao
interface ArticleSummaryDao {
    /**
     * Gets a summary for a specific article.
     * @param articleId The article ID to get summary for
     * @return Flow emitting the summary or null if not found
     */
    @Query("SELECT * FROM article_summaries WHERE article_id = :articleId LIMIT 1")
    fun getSummaryByArticleId(articleId: String): Flow<ArticleSummaryEntity?>

    /**
     * Gets a summary for a specific article (suspend).
     * @param articleId The article ID to get summary for
     * @return The summary or null if not found
     */
    @Query("SELECT * FROM article_summaries WHERE article_id = :articleId LIMIT 1")
    suspend fun getSummaryByArticleIdSync(articleId: String): ArticleSummaryEntity?

    /**
     * Gets a summary by its ID.
     * @param summaryId The summary ID
     * @return The summary or null if not found
     */
    @Query("SELECT * FROM article_summaries WHERE id = :summaryId LIMIT 1")
    suspend fun getSummaryById(summaryId: String): ArticleSummaryEntity?

    /**
     * Gets all summaries ordered by generation date.
     * @return Flow emitting all summaries
     */
    @Query("SELECT * FROM article_summaries ORDER BY generated_at DESC")
    fun getAllSummaries(): Flow<List<ArticleSummaryEntity>>

    /**
     * Inserts or replaces a summary.
     * @param summary The summary to insert
     */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSummary(summary: ArticleSummaryEntity)

    /**
     * Inserts or replaces multiple summaries.
     * @param summaries The summaries to insert
     */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSummaries(summaries: List<ArticleSummaryEntity>)

    /**
     * Deletes a summary by its ID.
     * @param summaryId The summary ID to delete
     */
    @Query("DELETE FROM article_summaries WHERE id = :summaryId")
    suspend fun deleteSummary(summaryId: String)

    /**
     * Deletes all summaries for a specific article.
     * @param articleId The article ID
     */
    @Query("DELETE FROM article_summaries WHERE article_id = :articleId")
    suspend fun deleteSummariesByArticleId(articleId: String)

    /**
     * Deletes summaries older than the specified threshold.
     * @param threshold The datetime threshold
     */
    @Query("DELETE FROM article_summaries WHERE generated_at < :threshold")
    suspend fun deleteOldSummaries(threshold: java.time.LocalDateTime)

    /**
     * Gets the total count of cached summaries.
     * @return The count of summaries
     */
    @Query("SELECT COUNT(*) FROM article_summaries")
    suspend fun getSummaryCount(): Int

    /**
     * Clears all summaries.
     */
    @Query("DELETE FROM article_summaries")
    suspend fun clearAll()
}
