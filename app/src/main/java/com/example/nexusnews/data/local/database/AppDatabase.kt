package com.example.nexusnews.data.local.database

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.example.nexusnews.data.local.dao.ArticleDao
import com.example.nexusnews.data.local.dao.ArticleSummaryDao
import com.example.nexusnews.data.local.dao.AiUsageDao
import com.example.nexusnews.data.local.dao.BookmarkDao
import com.example.nexusnews.data.local.entity.ArticleEntity
import com.example.nexusnews.data.local.entity.ArticleSummaryEntity
import com.example.nexusnews.data.local.entity.AiUsageEntity
import com.example.nexusnews.data.local.entity.BookmarkEntity
import com.example.nexusnews.util.constants.DatabaseConstants

/**
 * Main Room database for NexusNews.
 * Contains articles cache, bookmarks, AI summaries, and usage tracking.
 */
@Database(
    entities =
        [
            ArticleEntity::class,
            BookmarkEntity::class,
            ArticleSummaryEntity::class,
            AiUsageEntity::class,
        ],
    version = DatabaseConstants.DATABASE_VERSION,
    exportSchema = false,
)
@TypeConverters(Converters::class)
abstract class AppDatabase : RoomDatabase() {
    /**
     * Provides access to article caching operations.
     */
    abstract fun articleDao(): ArticleDao

    /**
     * Provides access to bookmark operations.
     */
    abstract fun bookmarkDao(): BookmarkDao

    /**
     * Provides access to article summary caching operations.
     */
    abstract fun articleSummaryDao(): ArticleSummaryDao

    /**
     * Provides access to AI usage tracking operations.
     */
    abstract fun aiUsageDao(): AiUsageDao
}
