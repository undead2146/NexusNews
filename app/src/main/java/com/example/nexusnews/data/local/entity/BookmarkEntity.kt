package com.example.nexusnews.data.local.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
import com.example.nexusnews.util.constants.DatabaseConstants
import java.time.LocalDateTime

/**
 * Room entity for storing bookmarked articles.
 * Uses foreign key relationship with ArticleEntity.
 */
@Entity(
    tableName = DatabaseConstants.BOOKMARKS_TABLE,
    foreignKeys = [
        ForeignKey(
            entity = ArticleEntity::class,
            parentColumns = ["id"],
            childColumns = ["article_id"],
            onDelete = ForeignKey.CASCADE,
        ),
    ],
    indices = [Index(value = ["article_id"], unique = true)],
)
data class BookmarkEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    @ColumnInfo(name = "article_id")
    val articleId: String,
    @ColumnInfo(name = DatabaseConstants.COLUMN_BOOKMARKED_AT)
    val bookmarkedAt: LocalDateTime = LocalDateTime.now(),
    @ColumnInfo(name = "is_favorite")
    val isFavorite: Boolean = false,
)
