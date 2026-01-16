package com.example.nexusnews.data.local.model

import androidx.room.ColumnInfo
import androidx.room.Embedded
import com.example.nexusnews.data.local.entity.ArticleEntity

/**
 * Represents a bookmark with its associated article data.
 * Used to capture the is_favorite status from the bookmarks table
 * when joined with the articles table.
 */
data class PopulatedBookmark(
    @Embedded val article: ArticleEntity,
    @ColumnInfo(name = "is_favorite") val isFavorite: Boolean
)
