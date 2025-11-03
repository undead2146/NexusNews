package com.example.nexusnews.data.remote.interceptor

import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.Protocol
import okhttp3.Request
import okhttp3.Response
import okhttp3.ResponseBody.Companion.toResponseBody
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

class ErrorInterceptorTest {

    private lateinit var errorInterceptor: ErrorInterceptor
    private lateinit var moshi: Moshi

    @Before
    fun setup() {
        moshi = Moshi.Builder()
            .add(KotlinJsonAdapterFactory())
            .build()
        errorInterceptor = ErrorInterceptor(moshi)
    }

    @Test
    fun `intercept returns response when successful`() {
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
        val result = errorInterceptor.intercept(chain)

        // Then
        assertEquals(200, result.code)
    }

    @Test
    fun `intercept throws UnauthorizedException for 401 with error message`() {
        // Given
        val request = Request.Builder()
            .url("https://api.example.com/test")
            .build()

        val errorBody = """{"message": "Invalid API key", "code": "AUTH_001"}"""
        val response = Response.Builder()
            .request(request)
            .protocol(Protocol.HTTP_2)
            .code(401)
            .message("Unauthorized")
            .body(errorBody.toResponseBody("application/json".toMediaTypeOrNull()))
            .build()

        val chain = TestChain(request, response)

        // When
        val exception = try {
            errorInterceptor.intercept(chain)
            null
        } catch (e: Exception) {
            e
        }

        // Then
        assertTrue(exception is UnauthorizedException)
        assertEquals("Invalid API key", exception?.message)
    }

    @Test
    fun `intercept throws ForbiddenException for 403`() {
        // Given
        val request = Request.Builder()
            .url("https://api.example.com/test")
            .build()

        val errorBody = """{"message": "Access forbidden"}"""
        val response = Response.Builder()
            .request(request)
            .protocol(Protocol.HTTP_2)
            .code(403)
            .message("Forbidden")
            .body(errorBody.toResponseBody("application/json".toMediaTypeOrNull()))
            .build()

        val chain = TestChain(request, response)

        // When
        val exception = try {
            errorInterceptor.intercept(chain)
            null
        } catch (e: Exception) {
            e
        }

        // Then
        assertTrue(exception is ForbiddenException)
        assertEquals("Access forbidden", exception?.message)
    }

    @Test
    fun `intercept throws NotFoundException for 404`() {
        // Given
        val request = Request.Builder()
            .url("https://api.example.com/test")
            .build()

        val response = Response.Builder()
            .request(request)
            .protocol(Protocol.HTTP_2)
            .code(404)
            .message("Not Found")
            .body("{}".toResponseBody("application/json".toMediaTypeOrNull()))
            .build()

        val chain = TestChain(request, response)

        // When
        val exception = try {
            errorInterceptor.intercept(chain)
            null
        } catch (e: Exception) {
            e
        }

        // Then
        assertTrue(exception is NotFoundException)
    }

    @Test
    fun `intercept throws RateLimitException for 429`() {
        // Given
        val request = Request.Builder()
            .url("https://api.example.com/test")
            .build()

        val errorBody = """{"message": "Rate limit exceeded"}"""
        val response = Response.Builder()
            .request(request)
            .protocol(Protocol.HTTP_2)
            .code(429)
            .message("Too Many Requests")
            .body(errorBody.toResponseBody("application/json".toMediaTypeOrNull()))
            .build()

        val chain = TestChain(request, response)

        // When
        val exception = try {
            errorInterceptor.intercept(chain)
            null
        } catch (e: Exception) {
            e
        }

        // Then
        assertTrue(exception is RateLimitException)
        assertEquals("Rate limit exceeded", exception?.message)
    }

    @Test
    fun `intercept throws ServerException for 500 with code`() {
        // Given
        val request = Request.Builder()
            .url("https://api.example.com/test")
            .build()

        val response = Response.Builder()
            .request(request)
            .protocol(Protocol.HTTP_2)
            .code(500)
            .message("Internal Server Error")
            .body("{}".toResponseBody("application/json".toMediaTypeOrNull()))
            .build()

        val chain = TestChain(request, response)

        // When
        val exception = try {
            errorInterceptor.intercept(chain)
            null
        } catch (e: Exception) {
            e
        }

        // Then
        assertTrue(exception is ServerException)
        assertEquals(500, (exception as? ServerException)?.code)
    }

    @Test
    fun `intercept throws HttpException for other 4xx codes`() {
        // Given
        val request = Request.Builder()
            .url("https://api.example.com/test")
            .build()

        val response = Response.Builder()
            .request(request)
            .protocol(Protocol.HTTP_2)
            .code(422)
            .message("Unprocessable Entity")
            .body("{}".toResponseBody("application/json".toMediaTypeOrNull()))
            .build()

        val chain = TestChain(request, response)

        // When
        val exception = try {
            errorInterceptor.intercept(chain)
            null
        } catch (e: Exception) {
            e
        }

        // Then
        assertTrue(exception is HttpException)
        assertEquals(422, (exception as? HttpException)?.code)
    }

    @Test
    fun `intercept uses response message when error body parsing fails`() {
        // Given
        val request = Request.Builder()
            .url("https://api.example.com/test")
            .build()

        val invalidJson = """{"invalid": json}"""
        val response = Response.Builder()
            .request(request)
            .protocol(Protocol.HTTP_2)
            .code(400)
            .message("Bad Request")
            .body(invalidJson.toResponseBody("application/json".toMediaTypeOrNull()))
            .build()

        val chain = TestChain(request, response)

        // When
        val exception = try {
            errorInterceptor.intercept(chain)
            null
        } catch (e: Exception) {
            e
        }

        // Then
        assertTrue(exception is HttpException)
        assertEquals("Bad Request", exception?.message)
    }

    @Test
    fun `intercept uses default message when both body and response message are empty`() {
        // Given
        val request = Request.Builder()
            .url("https://api.example.com/test")
            .build()

        val response = Response.Builder()
            .request(request)
            .protocol(Protocol.HTTP_2)
            .code(502)
            .message("")
            .body(null)
            .build()

        val chain = TestChain(request, response)

        // When
        val exception = try {
            errorInterceptor.intercept(chain)
            null
        } catch (e: Exception) {
            e
        }

        // Then
        assertTrue(exception is ServerException)
        assertEquals("HTTP 502", exception?.message)
    }

    // Helper class for testing
    private class TestChain(
        private val request: Request,
        private val response: Response
    ) : okhttp3.Interceptor.Chain {
        override fun request(): Request = request
        override fun proceed(request: Request): Response = response
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
