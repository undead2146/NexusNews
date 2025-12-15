package com.example.nexusnews.di

import com.example.nexusnews.data.cache.CacheManager
import com.example.nexusnews.data.cache.CacheManagerImpl
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

/**
 * Hilt module for cache-related dependencies.
 */
@Module
@InstallIn(SingletonComponent::class)
abstract class CacheModule {
    @Binds
    @Singleton
    abstract fun bindCacheManager(impl: CacheManagerImpl): CacheManager
}
