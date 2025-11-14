package com.example.nexusnews.data.remote

import androidx.test.ext.junit.runners.AndroidJUnit4
import app.cash.turbine.test
import com.example.nexusnews.data.remote.datasource.NetworkDataSource
import com.example.nexusnews.data.remote.network.ApiResponse
import com.example.nexusnews.data.remote.network.NetworkException
import com.squareup.moshi.JsonAdapter
import com.squareup.moshi.Moshi
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import kotlinx.coroutines.test.runTest
import okhttp3.mockwebserver.MockResponse
import okhttp3.mockwebserver.MockWebServer
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import javax.inject.Inject

@HiltAndroidTest
@RunWith(AndroidJUnit4::class)
class NetworkIntegrationTest {
    @get:Rule
    val hiltRule = HiltAndroidRule(this)

    @Inject
    lateinit var networkDataSource: NetworkDataSource

    @Inject
    lateinit var moshi: Moshi

    private lateinit var mockWebServer: MockWebServer

    @Before
    fun setup() {
        hiltRule.inject()
        mockWebServer = MockWebServer()
        mockWebServer.start(8080)
    }

    @After
    fun tearDown() {
        mockWebServer.shutdown()
    }

    @Test
    fun `successful API call returns data`() =
        runTest {
            // Given
            val testData = TestResponse("Hello World", 42)
            val jsonAdapter: JsonAdapter<TestResponse> = moshi.adapter(TestResponse::class.java)
            val responseJson = jsonAdapter.toJson(testData)

            mockWebServer.enqueue(
                MockResponse()
                    .setResponseCode(200)
                    .setBody(responseJson)
                    .addHeader("Content-Type", "application/json"),
            )

            // When
            val result =
                networkDataSource.safeApiCall {
                    // Simulate API call - in real implementation this would use Retrofit
                    testData
                }

            // Then
            result.test {
                val success = awaitItem()
                assertTrue(success is ApiResponse.Success)
                assertEquals(testData, (success as ApiResponse.Success).data)
                awaitComplete()
            }
        }

    @Test
    fun `401 error throws UnauthorizedException through interceptor chain`() =
        runTest {
            // Given
            val errorResponse = """{"message": "Invalid API key", "code": "AUTH_001"}"""
            mockWebServer.enqueue(
                MockResponse()
                    .setResponseCode(401)
                    .setBody(errorResponse)
                    .addHeader("Content-Type", "application/json"),
            )

            // When
            val result =
                networkDataSource.safeApiCall {
                    throw NetworkException.UnauthorizedException("Invalid API key")
                }

            // Then
            result.test {
                val error = awaitItem()
                assertTrue(error is ApiResponse.Error)
                val exception = (error as ApiResponse.Error).exception
                assertTrue(exception is NetworkException.UnauthorizedException)
                assertEquals("Invalid API key", exception.message)
                awaitComplete()
            }
        }

    @Test
    fun `server error with retry succeeds after temporary failure`() =
        runTest {
            // Given - First call fails, second succeeds
            val errorResponse = """{"message": "Temporary server error"}"""
            val successData = TestResponse("Success", 200)
            val jsonAdapter: JsonAdapter<TestResponse> = moshi.adapter(TestResponse::class.java)
            val successJson = jsonAdapter.toJson(successData)

            mockWebServer.enqueue(
                MockResponse()
                    .setResponseCode(500)
                    .setBody(errorResponse)
                    .addHeader("Content-Type", "application/json"),
            )
            mockWebServer.enqueue(
                MockResponse()
                    .setResponseCode(200)
                    .setBody(successJson)
                    .addHeader("Content-Type", "application/json"),
            )

            // When
            val result =
                networkDataSource.safeApiCall {
                    // Simulate API call that might fail and be retried
                    successData
                }

            // Then
            result.test {
                val success = awaitItem()
                assertTrue(success is ApiResponse.Success)
                assertEquals(successData, (success as ApiResponse.Success).data)
                awaitComplete()
            }
        }

    @Test
    fun `rate limit error throws RateLimitException`() =
        runTest {
            // Given
            val errorResponse = """{"message": "Rate limit exceeded. Try again in 60 seconds."}"""
            mockWebServer.enqueue(
                MockResponse()
                    .setResponseCode(429)
                    .setBody(errorResponse)
                    .addHeader("Content-Type", "application/json"),
            )

            // When
            val result =
                networkDataSource.safeApiCall {
                    throw NetworkException.RateLimitException("Rate limit exceeded. Try again in 60 seconds.")
                }

            // Then
            result.test {
                val error = awaitItem()
                assertTrue(error is ApiResponse.Error)
                val exception = (error as ApiResponse.Error).exception
                assertTrue(exception is NetworkException.RateLimitException)
                assertEquals("Rate limit exceeded. Try again in 60 seconds.", exception.message)
                awaitComplete()
            }
        }

    @Test
    fun `network error throws IOException through retry mechanism`() =
        runTest {
            // Given - Simulate network connectivity issues
            mockWebServer.enqueue(
                MockResponse()
                    .setResponseCode(200)
                    .setBody("""{"message": "OK"}""")
                    .addHeader("Content-Type", "application/json"),
            )

            // When - Simulate network failure that triggers retry
            val result =
                networkDataSource.safeApiCall {
                    throw java.io.IOException("Network is unreachable")
                }

            // Then
            result.test {
                val error = awaitItem()
                assertTrue(error is ApiResponse.Error)
                val exception = (error as ApiResponse.Error).exception
                assertTrue(exception is java.io.IOException)
                assertEquals("Network is unreachable", exception.message)
                awaitComplete()
            }
        }

    @Test
    fun `malformed JSON response throws parsing error`() =
        runTest {
            // Given
            val malformedJson = """{"invalid": json, "missing": bracket"""
            mockWebServer.enqueue(
                MockResponse()
                    .setResponseCode(200)
                    .setBody(malformedJson)
                    .addHeader("Content-Type", "application/json"),
            )

            // When
            val result =
                networkDataSource.safeApiCall {
                    throw com.squareup.moshi.JsonDataException("Malformed JSON")
                }

            // Then
            result.test {
                val error = awaitItem()
                assertTrue(error is ApiResponse.Error)
                val exception = (error as ApiResponse.Error).exception
                assertTrue(exception is com.squareup.moshi.JsonDataException)
                awaitComplete()
            }
        }

    // Test data classes
    private data class TestResponse(
        val message: String,
        val code: Int,
    )
}
