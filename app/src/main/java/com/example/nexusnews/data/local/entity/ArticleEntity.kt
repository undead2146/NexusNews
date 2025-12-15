package com.example.nexusnews.data.local.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.nexusnews.util.constants.DatabaseConstants
import java.time.LocalDateTime

/**
 * Room entity for caching news articles locally.
 * Enables offline access and faster loading.
 */
@Entity(tableName = DatabaseConstants.ARTICLES_TABLE)
data class ArticleEntity(
    @PrimaryKey
    @ColumnInfo(name = DatabaseConstants.COLUMN_ID)
    val id: String,
    @ColumnInfo(name = DatabaseConstants.COLUMN_TITLE)
    val title: String,
    @ColumnInfo(name = DatabaseConstants.COLUMN_DESCRIPTION)
    val description: String?,
    @ColumnInfo(name = DatabaseConstants.COLUMN_CONTENT)
    val content: String?,
    @ColumnInfo(name = DatabaseConstants.COLUMN_URL)
    val url: String,
    @ColumnInfo(name = DatabaseConstants.COLUMN_IMAGE_URL)
    val imageUrl: String?,
    @ColumnInfo(name = DatabaseConstants.COLUMN_AUTHOR)
    val author: String?,
    @ColumnInfo(name = DatabaseConstants.COLUMN_SOURCE)
    val source: String,
    @ColumnInfo(name = DatabaseConstants.COLUMN_PUBLISHED_AT)
    val publishedAt: LocalDateTime,
    @ColumnInfo(name = DatabaseConstants.COLUMN_CATEGORY)
    val category: String?,
    @ColumnInfo(name = "tags")
    val tags: List<String> = emptyList(),
    @ColumnInfo(name = "cached_at")
    val cachedAt: LocalDateTime = LocalDateTime.now(),
)
