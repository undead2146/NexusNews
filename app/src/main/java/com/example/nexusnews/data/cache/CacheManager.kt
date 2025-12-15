package com.example.nexusnews.data.cache

/**
 * Statistics about cached data.
 */
data class CacheStatistics(
    val totalSizeBytes: Long,
    val articleCacheSizeBytes: Long,
    val imageCacheSizeBytes: Long,
    val articleCount: Int,
) {
    /**
     * Total size in megabytes.
     */
    val totalSizeMB: Double
        get() = totalSizeBytes / (1024.0 * 1024.0)

    /**
     * Article cache size in megabytes.
     */
    val articleCacheSizeMB: Double
        get() = articleCacheSizeBytes / (1024.0 * 1024.0)

    /**
     * Image cache size in megabytes.
     */
    val imageCacheSizeMB: Double
        get() = imageCacheSizeBytes / (1024.0 * 1024.0)
}

/**
 * Manager for cache operations and statistics.
 */
interface CacheManager {
    /**
     * Gets current cache statistics.
     */
    suspend fun getCacheStatistics(): CacheStatistics

    /**
     * Clears article cache.
     */
    suspend fun clearArticleCache()

    /**
     * Clears image cache.
     */
    suspend fun clearImageCache()

    /**
     * Clears all cache data.
     */
    suspend fun clearAllCache()
}
