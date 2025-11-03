package com.example.nexusnews.data.remote.interceptor

import com.example.nexusnews.data.remote.network.RetryPolicy
import okhttp3.Protocol
import okhttp3.Request
import okhttp3.Response
import okhttp3.ResponseBody.Companion.toResponseBody
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.mockito.kotlin.mock
import org.mockito.kotlin.whenever
import java.io.IOException

class RetryInterceptorTest {

    private lateinit var retryPolicy: RetryPolicy
    private lateinit var retryInterceptor: RetryInterceptor

    @Before
    fun setup() {
        retryPolicy = mock()
        retryInterceptor = RetryInterceptor(retryPolicy)
    }

    @Test
    fun `intercept returns response on first successful call`() {
        // Given
        val request = Request.Builder()
            .url("https://api.example.com/test")
            .build()

        val response = Response.Builder()
            .request(request)
            .protocol(Protocol.HTTP_2)
            .code(200)
            .message("OK")
            .body("{}".toResponseBody("application/json".toMediaTypeOrNull()))
            .build()

        val chain = TestChain(request, response)

        // When
        val result = retryInterceptor.intercept(chain)

        // Then
        assertEquals(200, result.code)
        assertEquals(1, chain.callCount)
    }

    @Test
    fun `intercept retries on retryable status code and succeeds on second attempt`() {
        // Given
        val request = Request.Builder()
            .url("https://api.example.com/test")
            .build()

        val failedResponse = Response.Builder()
            .request(request)
            .protocol(Protocol.HTTP_2)
            .code(500)
            .message("Internal Server Error")
            .body("{}".toResponseBody("application/json".toMediaTypeOrNull()))
            .build()

        val successResponse = Response.Builder()
            .request(request)
            .protocol(Protocol.HTTP_2)
            .code(200)
            .message("OK")
            .body("{}".toResponseBody("application/json".toMediaTypeOrNull()))
            .build()

        val chain = TestChain(request, failedResponse, successResponse)
        whenever(retryPolicy.shouldRetry(IOException("Server Error"), 1)).thenReturn(true)
        whenever(retryPolicy.getDelayForAttempt(1)).thenReturn(100L)

        // When
        val result = retryInterceptor.intercept(chain)

        // Then
        assertEquals(200, result.code)
        assertEquals(2, chain.callCount)
    }

    @Test
    fun `intercept retries on IOException and succeeds after retry`() {
        // Given
        val request = Request.Builder()
            .url("https://api.example.com/test")
            .build()

        val successResponse = Response.Builder()
            .request(request)
            .protocol(Protocol.HTTP_2)
            .code(200)
            .message("OK")
            .body("{}".toResponseBody("application/json".toMediaTypeOrNull()))
            .build()

        val chain = TestChain(request, IOException("Network error"), successResponse)
        whenever(retryPolicy.shouldRetry(IOException("Network error"), 1)).thenReturn(true)
        whenever(retryPolicy.getDelayForAttempt(1)).thenReturn(100L)

        // When
        val result = retryInterceptor.intercept(chain)

        // Then
        assertEquals(200, result.code)
        assertEquals(2, chain.callCount)
    }

    @Test
    fun `intercept throws exception after max retries exceeded`() {
        // Given
        val request = Request.Builder()
            .url("https://api.example.com/test")
            .build()

        val failedResponse = Response.Builder()
            .request(request)
            .protocol(Protocol.HTTP_2)
            .code(500)
            .message("Internal Server Error")
            .body("{}".toResponseBody("application/json".toMediaTypeOrNull()))
            .build()

        val chain = TestChain(request, failedResponse, failedResponse, failedResponse)
        whenever(retryPolicy.shouldRetry(IOException("Server Error"), 1)).thenReturn(true)
        whenever(retryPolicy.shouldRetry(IOException("Server Error"), 2)).thenReturn(true)
        whenever(retryPolicy.shouldRetry(IOException("Server Error"), 3)).thenReturn(false)
        whenever(retryPolicy.getDelayForAttempt(1)).thenReturn(100L)
        whenever(retryPolicy.getDelayForAttempt(2)).thenReturn(200L)

        // When
        val exception = try {
            retryInterceptor.intercept(chain)
            null
        } catch (e: Exception) {
            e
        }

        // Then
        assertTrue(exception is IOException)
        assertEquals("Server Error", exception?.message)
        assertEquals(3, chain.callCount)
    }

    @Test
    fun `intercept does not retry on non-retryable status codes`() {
        // Given
        val request = Request.Builder()
            .url("https://api.example.com/test")
            .build()

        val response = Response.Builder()
            .request(request)
            .protocol(Protocol.HTTP_2)
            .code(401)
            .message("Unauthorized")
            .body("{}".toResponseBody("application/json".toMediaTypeOrNull()))
            .build()

        val chain = TestChain(request, response)
        whenever(retryPolicy.shouldRetry(IOException("Unauthorized"), 1)).thenReturn(false)

        // When
        val exception = try {
            retryInterceptor.intercept(chain)
            null
        } catch (e: Exception) {
            e
        }

        // Then
        assertTrue(exception is IOException)
        assertEquals("Unauthorized", exception?.message)
        assertEquals(1, chain.callCount)
    }

    @Test
    fun `intercept handles multiple retry attempts with increasing delays`() {
        // Given
        val request = Request.Builder()
            .url("https://api.example.com/test")
            .build()

        val failedResponse = Response.Builder()
            .request(request)
            .protocol(Protocol.HTTP_2)
            .code(503)
            .message("Service Unavailable")
            .body("{}".toResponseBody("application/json".toMediaTypeOrNull()))
            .build()

        val successResponse = Response.Builder()
            .request(request)
            .protocol(Protocol.HTTP_2)
            .code(200)
            .message("OK")
            .body("{}".toResponseBody("application/json".toMediaTypeOrNull()))
            .build()

        val chain = TestChain(request, failedResponse, failedResponse, successResponse)
        whenever(retryPolicy.shouldRetry(IOException("Service Unavailable"), 1)).thenReturn(true)
        whenever(retryPolicy.shouldRetry(IOException("Service Unavailable"), 2)).thenReturn(true)
        whenever(retryPolicy.shouldRetry(IOException("Service Unavailable"), 3)).thenReturn(false)
        whenever(retryPolicy.getDelayForAttempt(1)).thenReturn(100L)
        whenever(retryPolicy.getDelayForAttempt(2)).thenReturn(200L)

        // When
        val result = retryInterceptor.intercept(chain)

        // Then
        assertEquals(200, result.code)
        assertEquals(3, chain.callCount)
    }

    // Helper class for testing
    private class TestChain(
        private val request: Request,
        private vararg val responses: Any // Can be Response or IOException
    ) : okhttp3.Interceptor.Chain {

        var callCount = 0
        private var currentIndex = 0

        override fun request(): Request = request

        override fun proceed(request: Request): Response {
            callCount++
            val current = responses.getOrNull(currentIndex++)
            return when (current) {
                is Response -> current
                is IOException -> throw current
                else -> throw IllegalStateException("Unexpected response type")
            }
        }

        override fun connection() = null
        override fun call() = throw NotImplementedError()
        override fun connectTimeoutMillis() = 0
        override fun withConnectTimeout(timeout: Int, unit: java.util.concurrent.TimeUnit) = this
        override fun readTimeoutMillis() = 0
        override fun withReadTimeout(timeout: Int, unit: java.util.concurrent.TimeUnit) = this
        override fun writeTimeoutMillis() = 0
        override fun withWriteTimeout(timeout: Int, unit: java.util.concurrent.TimeUnit) = this
    }
}