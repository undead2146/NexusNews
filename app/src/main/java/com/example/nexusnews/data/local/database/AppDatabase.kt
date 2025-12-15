package com.example.nexusnews.data.local.database

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.example.nexusnews.data.local.dao.ArticleDao
import com.example.nexusnews.data.local.dao.BookmarkDao
import com.example.nexusnews.data.local.entity.ArticleEntity
import com.example.nexusnews.data.local.entity.BookmarkEntity

/**
 * Main Room database for NexusNews.
 * Contains articles cache and bookmarks.
 */
@Database(
    entities = [ArticleEntity::class, BookmarkEntity::class],
    version = 1,
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
}
