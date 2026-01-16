package com.example.nexusnews.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.nexusnews.data.local.entity.AiRequestType
import com.example.nexusnews.data.local.entity.AiUsageEntity
import kotlinx.coroutines.flow.Flow
import java.time.LocalDateTime

/**
 * Data Access Object for AI usage tracking operations.
 * Provides methods for storing and retrieving AI usage statistics.
 */
@Dao
interface AiUsageDao {
    /**
     * Gets all usage records ordered by timestamp.
     * @return Flow emitting all usage records
     */
    @Query("SELECT * FROM ai_usage ORDER BY generated_at DESC")
    fun getAllUsage(): Flow<List<AiUsageEntity>>

    /**
     * Gets usage records for a specific request type.
     * @param requestType The type of request
     * @return Flow emitting usage records for the type
     */
    @Query("SELECT * FROM ai_usage WHERE request_type = :requestType ORDER BY generated_at DESC")
    fun getUsageByType(requestType: AiRequestType): Flow<List<AiUsageEntity>>

    /**
     * Gets usage records for a specific date range.
     * @param startDate Start of the date range
     * @param endDate End of the date range
     * @return Flow emitting usage records in the range
     */
    @Query("SELECT * FROM ai_usage WHERE generated_at BETWEEN :startDate AND :endDate ORDER BY generated_at DESC")
    fun getUsageByDateRange(startDate: LocalDateTime, endDate: LocalDateTime): Flow<List<AiUsageEntity>>

    /**
     * Gets total token usage for a specific date range.
     * @param startDate Start of the date range
     * @param endDate End of the date range
     * @return Total tokens used in the range
     */
    @Query("SELECT SUM(total_tokens) FROM ai_usage WHERE generated_at BETWEEN :startDate AND :endDate")
    suspend fun getTotalTokensByDateRange(startDate: LocalDateTime, endDate: LocalDateTime): Int?

    /**
     * Gets total request count for a specific date range.
     * @param startDate Start of the date range
     * @param endDate End of the date range
     * @return Total requests in the range
     */
    @Query("SELECT SUM(request_count) FROM ai_usage WHERE generated_at BETWEEN :startDate AND :endDate")
    suspend fun getTotalRequestsByDateRange(startDate: LocalDateTime, endDate: LocalDateTime): Int?

    /**
     * Gets today's total token usage.
     * @return Total tokens used today
     */
    @Query("SELECT SUM(total_tokens) FROM ai_usage WHERE date(generated_at) = date('now')")
    suspend fun getTodayTokenUsage(): Int?

    /**
     * Gets today's request count.
     * @return Total requests made today
     */
    @Query("SELECT SUM(request_count) FROM ai_usage WHERE date(generated_at) = date('now')")
    suspend fun getTodayRequestCount(): Int?

    /**
     * Gets usage statistics for the last N days.
     * @param days Number of days to look back
     * @return Flow emitting usage records for the period
     */
    @Query("SELECT * FROM ai_usage WHERE generated_at >= date('now', '-' || :days || ' days') ORDER BY generated_at DESC")
    fun getUsageForLastDays(days: Int): Flow<List<AiUsageEntity>>

    /**
     * Inserts or replaces a usage record.
     * @param usage The usage record to insert
     */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertUsage(usage: AiUsageEntity)

    /**
     * Inserts or replaces multiple usage records.
     * @param usageList The usage records to insert
     */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertUsageList(usageList: List<AiUsageEntity>)

    /**
     * Deletes usage records older than specified threshold.
     * @param threshold The datetime threshold
     */
    @Query("DELETE FROM ai_usage WHERE generated_at < :threshold")
    suspend fun deleteOldUsage(threshold: LocalDateTime)

    /**
     * Clears all usage records.
     */
    @Query("DELETE FROM ai_usage")
    suspend fun clearAll()
}
