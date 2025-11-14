package com.example.nexusnews.data.util

import com.example.nexusnews.util.constants.NetworkConstants
import kotlinx.coroutines.delay
import timber.log.Timber
import java.io.IOException
import java.net.SocketTimeoutException
import java.net.UnknownHostException
import kotlin.math.min
import kotlin.math.pow

/**
 * Percentage of delay to use as maximum jitter (0-25% of delay).
 * Jitter prevents multiple clients from retrying simultaneously.
 */
private const val JITTER_PERCENTAGE = 0.25

/**
 * Configurable retry policy for network requests.
 * Implements exponential backoff with jitter to prevent thundering herd problems.
 *
 * @param maxAttempts Maximum number of retry attempts (default: 3)
 * @param initialDelayMs Initial delay before first retry in milliseconds (default: 1000)
 * @param maxDelayMs Maximum delay between retries in milliseconds (default: 10000)
 * @param backoffMultiplier Multiplier for exponential backoff (default: 2.0)
 */
data class RetryPolicy(
    val maxAttempts: Int = NetworkConstants.MAX_RETRY_ATTEMPTS,
    val initialDelayMs: Long = NetworkConstants.INITIAL_RETRY_DELAY_MS,
    val maxDelayMs: Long = NetworkConstants.MAX_RETRY_DELAY_MS,
    val backoffMultiplier: Double = NetworkConstants.RETRY_BACKOFF_MULTIPLIER,
) {
    /**
     * Calculates delay for given attempt number using exponential backoff with jitter.
     * Formula: delay = min(initialDelay * (multiplier ^ attempt), maxDelay) + jitter
     * Jitter is added to prevent multiple clients from retrying simultaneously.
     *
     * @param attempt Current attempt number (0-indexed)
     * @return Delay in milliseconds before next retry
     */
    fun getDelayForAttempt(attempt: Int): Long {
        val exponentialDelay = initialDelayMs * backoffMultiplier.pow(attempt.toDouble())
        val cappedDelay = min(exponentialDelay, maxDelayMs.toDouble()).toLong()

        // Add jitter (0-25% of delay) to prevent thundering herd
        val jitter = (cappedDelay * JITTER_PERCENTAGE * Math.random()).toLong()

        return cappedDelay + jitter
    }

    /**
     * Determines if the given exception should trigger a retry.
     * Only certain types of exceptions are considered retryable to avoid
     * wasting resources on permanent failures.
     *
     * @param exception The exception that occurred
     * @param attempt Current attempt number (0-indexed)
     * @return true if the operation should be retried, false otherwise
     */
    fun shouldRetry(
        exception: Throwable,
        attempt: Int,
    ): Boolean {
        if (attempt >= maxAttempts) {
            Timber.d("Max retry attempts ($maxAttempts) reached")
            return false
        }

        return when (exception) {
            is SocketTimeoutException,
            is UnknownHostException,
            is IOException,
            -> {
                Timber.d("Retryable exception: ${exception::class.simpleName}")
                true
            }
            else -> {
                Timber.d("Non-retryable exception: ${exception::class.simpleName}")
                false
            }
        }
    }
}

/**
 * Executes a suspending block with retry logic using the provided policy.
 * Automatically handles retry delays and exception filtering.
 *
 * @param retryPolicy Configuration for retry behavior
 * @param block The suspending operation to execute with retries
 * @return Result of the successful operation
 * @throws Exception if all retry attempts fail
 */
suspend fun <T> withRetry(
    retryPolicy: RetryPolicy = RetryPolicy(),
    block: suspend () -> T,
): T {
    var currentAttempt = 0
    var lastException: Throwable? = null

    while (currentAttempt < retryPolicy.maxAttempts) {
        @Suppress("TooGenericExceptionCaught")
        try {
            return block()
        } catch (e: Exception) {
            lastException = e

            if (!retryPolicy.shouldRetry(e, currentAttempt)) {
                throw e
            }

            val delay = retryPolicy.getDelayForAttempt(currentAttempt)
            Timber.w(e, "Attempt ${currentAttempt + 1} failed. Retrying in ${delay}ms...")

            delay(delay)
            currentAttempt++
        }
    }

    error("Retry failed: ${lastException?.message ?: "No exception occurred"}")
}
