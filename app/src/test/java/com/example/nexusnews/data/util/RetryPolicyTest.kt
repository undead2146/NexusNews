package com.example.nexusnews.data.util

import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test
import java.io.IOException
import java.net.SocketTimeoutException
import java.net.UnknownHostException

@OptIn(ExperimentalCoroutinesApi::class)
class RetryPolicyTest {

    private val retryPolicy = RetryPolicy(
        maxAttempts = 3,
        initialDelayMs = 100,
        maxDelayMs = 1000,
        backoffMultiplier = 2.0
    )

    @Test
    fun `getDelayForAttempt returns increasing delays with exponential backoff`() {
        // When
        val delay0 = retryPolicy.getDelayForAttempt(0)
        val delay1 = retryPolicy.getDelayForAttempt(1)
        val delay2 = retryPolicy.getDelayForAttempt(2)

        // Then
        assertTrue(delay0 >= 100) // Initial delay + jitter
        assertTrue(delay1 >= 200) // 2x initial + jitter
        assertTrue(delay2 >= 400) // 4x initial + jitter
        assertTrue(delay1 > delay0)
        assertTrue(delay2 > delay1)
    }

    @Test
    fun `getDelayForAttempt caps delay at maxDelayMs`() {
        // When - Very high attempt number
        val delay = retryPolicy.getDelayForAttempt(10)

        // Then
        assertTrue(delay <= 1250) // maxDelay + max jitter (25%)
    }

    @Test
    fun `shouldRetry returns true for SocketTimeoutException`() {
        // Given
        val exception = SocketTimeoutException("Timeout")

        // When
        val result = retryPolicy.shouldRetry(exception, 0)

        // Then
        assertTrue(result)
    }

    @Test
    fun `shouldRetry returns true for UnknownHostException`() {
        // Given
        val exception = UnknownHostException("Host not found")

        // When
        val result = retryPolicy.shouldRetry(exception, 0)

        // Then
        assertTrue(result)
    }

    @Test
    fun `shouldRetry returns true for IOException`() {
        // Given
        val exception = IOException("Network error")

        // When
        val result = retryPolicy.shouldRetry(exception, 0)

        // Then
        assertTrue(result)
    }

    @Test
    fun `shouldRetry returns false for IllegalArgumentException`() {
        // Given
        val exception = IllegalArgumentException("Bad argument")

        // When
        val result = retryPolicy.shouldRetry(exception, 0)

        // Then
        assertFalse(result)
    }

    @Test
    fun `shouldRetry returns false for NullPointerException`() {
        // Given
        val exception = NullPointerException("Null value")

        // When
        val result = retryPolicy.shouldRetry(exception, 0)

        // Then
        assertFalse(result)
    }

    @Test
    fun `shouldRetry returns false when max attempts reached`() {
        // Given
        val exception = IOException("Network error")

        // When
        val result = retryPolicy.shouldRetry(exception, 3) // At max attempts

        // Then
        assertFalse(result)
    }

    @Test
    fun `shouldRetry returns false when attempt exceeds max attempts`() {
        // Given
        val exception = IOException("Network error")

        // When
        val result = retryPolicy.shouldRetry(exception, 5) // Beyond max attempts

        // Then
        assertFalse(result)
    }

    @Test
    fun `withRetry succeeds on first attempt`() = runTest {
        // Given
        var attempts = 0

        // When
        val result = withRetry(retryPolicy) {
            attempts++
            "success"
        }

        // Then
        assertEquals("success", result)
        assertEquals(1, attempts)
    }

    @Test
    fun `withRetry retries on failure then succeeds`() = runTest {
        // Given
        var attempts = 0

        // When
        val result = withRetry(retryPolicy) {
            attempts++
            if (attempts < 2) {
                throw IOException("Network error")
            }
            "success"
        }

        // Then
        assertEquals("success", result)
        assertEquals(2, attempts)
    }

    @Test
    fun `withRetry throws exception after max attempts`() = runTest {
        // Given
        var attempts = 0
        val policy = RetryPolicy(maxAttempts = 2)

        // When
        val exception = try {
            withRetry(policy) {
                attempts++
                throw IOException("Persistent error")
            }
            null
        } catch (e: Exception) {
            e
        }

        // Then
        assertTrue(exception is IOException)
        assertEquals("Persistent error", exception?.message)
        assertEquals(2, attempts)
    }

    @Test
    fun `withRetry does not retry non-retryable exceptions`() = runTest {
        // Given
        var attempts = 0

        // When
        val exception = try {
            withRetry(retryPolicy) {
                attempts++
                throw IllegalArgumentException("Bad argument")
            }
            null
        } catch (e: Exception) {
            e
        }

        // Then
        assertTrue(exception is IllegalArgumentException)
        assertEquals(1, attempts) // Only one attempt, no retries
    }

    @Test
    fun `custom retry policy parameters are respected`() {
        // Given
        val customPolicy = RetryPolicy(
            maxAttempts = 5,
            initialDelayMs = 50,
            maxDelayMs = 500,
            backoffMultiplier = 3.0
        )

        // When
        val delay0 = customPolicy.getDelayForAttempt(0)
        val delay1 = customPolicy.getDelayForAttempt(1)

        // Then
        assertTrue(delay0 >= 50) // Custom initial delay
        assertTrue(delay1 >= 150) // 3x initial delay
        assertTrue(delay1 > delay0)
    }
}