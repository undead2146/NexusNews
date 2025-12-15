package com.example.nexusnews.data.cache

import android.content.Context
import coil.imageLoader
import com.example.nexusnews.data.local.dao.ArticleDao
import dagger.hilt.android.qualifiers.ApplicationContext
import timber.log.Timber
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Implementation of CacheManager for managing app cache.
 */
@Singleton
class CacheManagerImpl
    @Inject
    constructor(
        @ApplicationContext private val context: Context,
        private val articleDao: ArticleDao,
    ) : CacheManager {
        override suspend fun getCacheStatistics(): CacheStatistics {
            return try {
                val articleCount = articleDao.getArticleCount()
                val cacheDir = context.cacheDir
                val articleCacheSize = calculateDirectorySize(cacheDir)

                // Coil image cache is in the cache directory
                val imageCacheSize = estimateImageCacheSize()

                CacheStatistics(
                    totalSizeBytes = articleCacheSize + imageCacheSize,
                    articleCacheSizeBytes = articleCacheSize,
                    imageCacheSizeBytes = imageCacheSize,
                    articleCount = articleCount,
                )
            } catch (e: Exception) {
                Timber.e(e, "Failed to get cache statistics")
                CacheStatistics(
                    totalSizeBytes = 0,
                    articleCacheSizeBytes = 0,
                    imageCacheSizeBytes = 0,
                    articleCount = 0,
                )
            }
        }

        override suspend fun clearArticleCache() {
            try {
                articleDao.clearAllArticles()
                Timber.d("Article cache cleared")
            } catch (e: Exception) {
                Timber.e(e, "Failed to clear article cache")
                throw e
            }
        }

        override suspend fun clearImageCache() {
            try {
                context.imageLoader.memoryCache?.clear()
                context.imageLoader.diskCache?.clear()
                Timber.d("Image cache cleared")
            } catch (e: Exception) {
                Timber.e(e, "Failed to clear image cache")
                throw e
            }
        }

        override suspend fun clearAllCache() {
            try {
                clearArticleCache()
                clearImageCache()
                Timber.d("All cache cleared")
            } catch (e: Exception) {
                Timber.e(e, "Failed to clear all cache")
                throw e
            }
        }

        private fun calculateDirectorySize(directory: java.io.File): Long {
            var size = 0L
            try {
                directory.listFiles()?.forEach { file ->
                    size +=
                        if (file.isDirectory) {
                            calculateDirectorySize(file)
                        } else {
                            file.length()
                        }
                }
            } catch (e: Exception) {
                Timber.e(e, "Failed to calculate directory size")
            }
            return size
        }

        private fun estimateImageCacheSize(): Long {
            return try {
                val diskCache = context.imageLoader.diskCache
                diskCache?.size ?: 0L
            } catch (e: Exception) {
                Timber.e(e, "Failed to estimate image cache size")
                0L
            }
        }
    }
