package com.example.nexusnews.data.repository

import com.example.nexusnews.util.Result
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flow
import timber.log.Timber
import java.io.IOException

/**
 * Base repository providing common data operation patterns.
 * Implements offline-first strategy using NetworkBoundResource pattern.
 */
abstract class BaseRepository {
    
    /**
     * Helper function for network-bound resources with caching.
     * Implements the offline-first pattern:
     * 1. Emit cached data immediately
     * 2. Fetch from network
     * 3. Update cache
     * 4. Emit fresh data
     * 
     * @param fetchFromLocal Fetch cached data
     * @param shouldFetch Determine if network fetch is needed
     * @param fetchFromRemote Fetch from network
     * @param saveRemoteData Save network data to cache
     */
    protected fun <T> networkBoundResource(
        fetchFromLocal: suspend () -> T?,
        shouldFetch: (T?) -> Boolean = { true },
        fetchFromRemote: suspend () -> T,
        saveRemoteData: suspend (T) -> Unit = {}
    ): Flow<Result<T>> = flow {
        emit(Result.Loading)
        
        // Emit cached data first
        val localData = fetchFromLocal()
        if (localData != null) {
            emit(Result.Success(localData))
        }
        
        // Check if network fetch is needed
        if (shouldFetch(localData)) {
            try {
                val remoteData = fetchFromRemote()
                saveRemoteData(remoteData)
                emit(Result.Success(remoteData))
            } catch (e: IOException) {
                Timber.e(e, "Network fetch failed")
                // If we have cached data, don't emit error
                if (localData == null) {
                    emit(Result.Error(e))
                }
            }
        }
    }.catch { e ->
        Timber.e(e, "Error in networkBoundResource")
        emit(Result.Error(e))
    }
}

