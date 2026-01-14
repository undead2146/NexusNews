package com.example.nexusnews.data.local.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.nexusnews.util.constants.DatabaseConstants
import java.time.LocalDateTime

/**
 * Room entity for tracking AI API usage.
 * Enables monitoring of token consumption and request counts.
 */
@Entity(tableName = DatabaseConstants.AI_USAGE_TABLE)
data class AiUsageEntity(
    @PrimaryKey
    @ColumnInfo(name = DatabaseConstants.COLUMN_ID)
    val id: String,
    @ColumnInfo(name = DatabaseConstants.COLUMN_REQUEST_TYPE)
    val requestType: AiRequestType,
    @ColumnInfo(name = DatabaseConstants.COLUMN_MODEL_USED)
    val modelUsed: String,
    @ColumnInfo(name = DatabaseConstants.COLUMN_PROMPT_TOKENS)
    val promptTokens: Int,
    @ColumnInfo(name = DatabaseConstants.COLUMN_COMPLETION_TOKENS)
    val completionTokens: Int,
    @ColumnInfo(name = DatabaseConstants.COLUMN_TOTAL_TOKENS)
    val totalTokens: Int,
    @ColumnInfo(name = DatabaseConstants.COLUMN_REQUEST_COUNT)
    val requestCount: Int,
    @ColumnInfo(name = DatabaseConstants.COLUMN_GENERATED_AT)
    val timestamp: LocalDateTime,
) {
    companion object {
        /**
         * Generates a unique ID for an AI usage record.
         */
        fun generateId(): String = "ai_usage_${System.currentTimeMillis()}_${(0..9999).random()}"
    }
}

/**
 * Types of AI requests that can be made.
 */
enum class AiRequestType {
    SUMMARIZATION,
    SENTIMENT_ANALYSIS,
    TRANSLATION,
    KEY_POINTS_EXTRACTION,
    ENTITY_RECOGNITION,
    TOPIC_CLASSIFICATION,
    BIAS_DETECTION,
    CHAT_ASSISTANT,
    CONTENT_GENERATION,
    RECOMMENDATION,
    OTHER,
}
