package com.example.nexusnews.data.local.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
import com.example.nexusnews.util.constants.DatabaseConstants
import java.time.LocalDateTime

/**
 * Room entity for caching AI-generated article summaries.
 * Enables offline access and avoids regenerating summaries.
 */
@Entity(
    tableName = DatabaseConstants.ARTICLE_SUMMARIES_TABLE,
    foreignKeys =
        [
            ForeignKey(
                entity = ArticleEntity::class,
                parentColumns = [DatabaseConstants.COLUMN_ID],
                childColumns = [DatabaseConstants.COLUMN_ARTICLE_ID],
                onDelete = ForeignKey.CASCADE,
            ),
        ],
    indices = [Index(DatabaseConstants.COLUMN_ARTICLE_ID)],
)
data class ArticleSummaryEntity(
    @PrimaryKey
    @ColumnInfo(name = DatabaseConstants.COLUMN_ID)
    val id: String,
    @ColumnInfo(name = DatabaseConstants.COLUMN_ARTICLE_ID)
    val articleId: String,
    @ColumnInfo(name = DatabaseConstants.COLUMN_SUMMARY)
    val summary: String,
    @ColumnInfo(name = DatabaseConstants.COLUMN_MODEL_USED)
    val modelUsed: String,
    @ColumnInfo(name = DatabaseConstants.COLUMN_PROMPT_TOKENS)
    val promptTokens: Int,
    @ColumnInfo(name = DatabaseConstants.COLUMN_COMPLETION_TOKENS)
    val completionTokens: Int,
    @ColumnInfo(name = DatabaseConstants.COLUMN_TOTAL_TOKENS)
    val totalTokens: Int,
    @ColumnInfo(name = DatabaseConstants.COLUMN_GENERATED_AT)
    val generatedAt: LocalDateTime,
    @ColumnInfo(name = DatabaseConstants.COLUMN_LANGUAGE)
    val language: String = "en",
) {
    companion object {
        /**
         * Generates a unique ID for a summary based on article ID and model.
         */
        fun generateId(articleId: String, model: String): String = "${articleId}_${model}"
    }
}
