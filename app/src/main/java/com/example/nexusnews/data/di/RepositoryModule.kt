package com.example.nexusnews.data.di

import com.example.nexusnews.data.remote.NewsRemoteDataSource
import com.example.nexusnews.data.repository.NewsRepositoryImpl
import com.example.nexusnews.domain.repository.NewsRepository
import com.example.nexusnews.data.local.dao.ArticleDao
import com.example.nexusnews.data.local.dao.BookmarkDao
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

/**
 * Hilt module providing repository dependencies.
 */
@Module
@InstallIn(SingletonComponent::class)
object RepositoryModule {
    @Provides
    @Singleton
    fun provideNewsRepository(
        remoteDataSource: NewsRemoteDataSource,
        articleDao: ArticleDao,
        bookmarkDao: BookmarkDao,
    ): NewsRepository = NewsRepositoryImpl(remoteDataSource, articleDao, bookmarkDao)
}
