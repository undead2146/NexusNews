package com.example.nexusnews.di

import android.content.Context
import androidx.room.Room
import com.example.nexusnews.data.local.dao.ArticleDao
import com.example.nexusnews.data.local.dao.ArticleSummaryDao
import com.example.nexusnews.data.local.dao.AiUsageDao
import com.example.nexusnews.data.local.dao.BookmarkDao
import com.example.nexusnews.data.local.database.AppDatabase
import com.example.nexusnews.util.constants.DatabaseConstants
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

/**
 * Hilt module for providing Room database dependencies.
 */
@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {
    /**
     * Provides the Room database instance.
     */
    @Provides
    @Singleton
    fun provideAppDatabase(
        @ApplicationContext context: Context,
    ): AppDatabase =
        Room
            .databaseBuilder(
                context,
                AppDatabase::class.java,
                DatabaseConstants.DATABASE_NAME,
            ).fallbackToDestructiveMigration()
            .build()

    /**
     * Provides ArticleDao from the database.
     */
    @Provides
    fun provideArticleDao(database: AppDatabase): ArticleDao = database.articleDao()

    /**
     * Provides BookmarkDao from the database.
     */
    @Provides
    fun provideBookmarkDao(database: AppDatabase): BookmarkDao = database.bookmarkDao()

    /**
     * Provides ArticleSummaryDao from the database.
     */
    @Provides
    fun provideArticleSummaryDao(database: AppDatabase): ArticleSummaryDao = database.articleSummaryDao()

    /**
     * Provides AiUsageDao from the database.
     */
    @Provides
    fun provideAiUsageDao(database: AppDatabase): AiUsageDao = database.aiUsageDao()
}
