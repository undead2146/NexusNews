package com.example.nexusnews.data.remote.interceptor

import com.example.nexusnews.data.util.RetryPolicy
import okhttp3.Interceptor
import okhttp3.Response
import timber.log.Timber
import java.io.IOException
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Interceptor that implements automatic retry logic for failed HTTP requests.
 * Uses exponential backoff strategy to prevent overwhelming failing services.
 */
@Singleton
class RetryInterceptor
    @Inject
    constructor(
        private val retryPolicy: RetryPolicy,
    ) : Interceptor {
        @Suppress("ReturnCount")
        override fun intercept(chain: Interceptor.Chain): Response {
            val request = chain.request()

            // Check if retry is disabled for this specific request
            if (request.header("No-Retry") != null) {
                return chain.proceed(request)
            }

            var attempt = 0
            var lastException: IOException? = null

            while (attempt < retryPolicy.maxAttempts) {
                try {
                    val response = chain.proceed(request)

                    if (response.isSuccessful) {
                        return response
                    }

                    if (!isRetryableStatusCode(response.code)) {
                        response.close()
                        throw IOException("HTTP ${response.code}: ${response.message}")
                    }

                    // Retryable failure
                    response.close()
                    val exception = IOException("HTTP ${response.code}: ${response.message}")
                    lastException = exception

                    if (!retryPolicy.shouldRetry(exception, attempt)) {
                        throw exception
                    }

                    // Retry
                    val delay = retryPolicy.getDelayForAttempt(attempt)
                    Timber.d(
                        "Retrying request to ${request.url} in ${delay}ms " +
                            "(attempt ${attempt + 1}/${retryPolicy.maxAttempts})",
                    )
                    Thread.sleep(delay)
                    attempt++
                } catch (e: IOException) {
                    lastException = e
                    Timber.w(e, "Request failed on attempt ${attempt + 1}")

                    if (!retryPolicy.shouldRetry(e, attempt)) {
                        throw e
                    }

                    // Retry
                    val delay = retryPolicy.getDelayForAttempt(attempt)
                    Timber.d(
                        "Retrying request to ${request.url} in ${delay}ms " +
                            "(attempt ${attempt + 1}/${retryPolicy.maxAttempts})",
                    )
                    Thread.sleep(delay)
                    attempt++
                }
            }

            // If we get here, all retries exhausted
            throw lastException ?: IOException("Request failed after ${retryPolicy.maxAttempts} attempts")
        }

        /**
         * Determines if an HTTP status code should trigger a retry.
         * Only certain status codes are considered retryable to avoid
         * wasting resources on permanent failures.
         *
         * @param code HTTP status code
         * @return true if the request should be retried, false otherwise
         */
        private fun isRetryableStatusCode(code: Int): Boolean = code in RETRYABLE_STATUS_CODES
    }

/**
 * HTTP status codes that are considered retryable.
 * These typically indicate temporary server issues or rate limiting.
 */
private val RETRYABLE_STATUS_CODES =
    listOf(
        REQUEST_TIMEOUT_CODE,
        TOO_MANY_REQUESTS_CODE,
        INTERNAL_SERVER_ERROR_CODE,
        BAD_GATEWAY_CODE,
        SERVICE_UNAVAILABLE_CODE,
        GATEWAY_TIMEOUT_CODE,
    )

/**
 * HTTP status code constants for retryable errors.
 */
private const val REQUEST_TIMEOUT_CODE = 408
private const val TOO_MANY_REQUESTS_CODE = 429
private const val INTERNAL_SERVER_ERROR_CODE = 500
private const val BAD_GATEWAY_CODE = 502
private const val SERVICE_UNAVAILABLE_CODE = 503
private const val GATEWAY_TIMEOUT_CODE = 504
