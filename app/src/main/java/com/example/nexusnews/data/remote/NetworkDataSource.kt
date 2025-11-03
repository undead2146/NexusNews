package com.example.nexusnews.data.remote

import com.example.nexusnews.data.remote.dto.ApiException
import com.example.nexusnews.data.remote.interceptor.NetworkException
import com.example.nexusnews.data.util.NetworkMonitor
import com.example.nexusnews.data.util.withRetry
import timber.log.Timber
import java.io.IOException
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Base class for network data sources providing common functionality.
 * Handles network connectivity checking, retry logic, and error handling.
 */
@Singleton
open class NetworkDataSource @Inject constructor(
    private val networkMonitor: NetworkMonitor
) {

    /**
     * Executes a network call with connectivity check and error handling.
     * Automatically retries failed requests and provides consistent error handling.
     *
     * @param execute The API call to execute as a suspending function
     * @return Result of the API call
     * @throws IOException if network is unavailable
     * @throws ApiException if API returns an error response
     * @throws NetworkException for HTTP-specific errors (401, 403, etc.)
     */
    protected suspend fun <T> safeApiCall(
        execute: suspend () -> T
    ): T {
        // Check network connectivity before making request
        if (!networkMonitor.isCurrentlyConnected()) {
            Timber.e("No network connection available")
            throw IOException("No network connection. Please check your internet.")
        }

        return try {
            // Execute with automatic retry logic
            withRetry {
                execute()
            }
        } catch (e: ApiException) {
            Timber.e(e, "API call failed with business logic error")
            throw e
        } catch (e: NetworkException) {
            Timber.e(e, "API call failed with network-specific error")
            throw e
        } catch (e: IOException) {
            Timber.e(e, "API call failed with network I/O error")
            throw e
        } catch (e: Exception) {
            Timber.e(e, "API call failed with unexpected error")
            throw IOException("Network request failed: ${e.message}", e)
        }
    }
}