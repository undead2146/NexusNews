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
class RetryInterceptor @Inject constructor(
    private val retryPolicy: RetryPolicy = RetryPolicy()
) : Interceptor {

    override fun intercept(chain: Interceptor.Chain): Response {
        val request = chain.request()

        // Check if retry is disabled for this specific request
        if (request.header("No-Retry") != null) {
            return chain.proceed(request)
        }

        var attempt = 0
        var response: Response? = null
        var exception: IOException? = null

        while (attempt < retryPolicy.maxAttempts) {
            try {
                response = chain.proceed(request)

                // If successful or non-retryable error, return response
                if (response.isSuccessful || !isRetryableStatusCode(response.code)) {
                    return response
                }

                // Close the response body before retry to free resources
                response.close()

                exception = IOException("HTTP ${response.code}: ${response.message}")

            } catch (e: IOException) {
                exception = e
                Timber.w(e, "Request failed on attempt ${attempt + 1}")
            }

            // Check if we should retry based on the exception
            if (exception != null && retryPolicy.shouldRetry(exception, attempt)) {
                val delay = retryPolicy.getDelayForAttempt(attempt)
                Timber.d("Retrying request to ${request.url} in ${delay}ms (attempt ${attempt + 1}/${retryPolicy.maxAttempts})")

                Thread.sleep(delay)
                attempt++
            } else {
                break
            }
        }

        // If we have an exception, throw it
        exception?.let { throw it }

        // Otherwise return the last response (shouldn't happen in normal flow)
        return response ?: throw IOException("No response after $attempt attempts")
    }

    /**
     * Determines if an HTTP status code should trigger a retry.
     * Only certain status codes are considered retryable to avoid
     * wasting resources on permanent failures.
     *
     * @param code HTTP status code
     * @return true if the request should be retried, false otherwise
     */
    private fun isRetryableStatusCode(code: Int): Boolean {
        return code in listOf(408, 429, 500, 502, 503, 504)
    }
}