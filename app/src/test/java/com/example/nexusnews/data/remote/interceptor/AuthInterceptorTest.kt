package com.example.nexusnews.data.remote.interceptor

import com.example.nexusnews.util.constants.ApiConstants
import okhttp3.Interceptor
import okhttp3.Request
import okhttp3.Response
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Before
import org.junit.Test
import org.mockito.kotlin.any
import org.mockito.kotlin.mock
import org.mockito.kotlin.whenever

class AuthInterceptorTest {
    private lateinit var authInterceptor: AuthInterceptor
    private lateinit var chain: Interceptor.Chain
    private lateinit var response: Response

    @Before
    fun setup() {
        authInterceptor = AuthInterceptor()
        chain = mock()
        response = mock()

        whenever(chain.proceed(any())).thenReturn(response)
    }

    @Test
    fun `intercept adds user agent header`() {
        // Given
        val request =
            Request
                .Builder()
                .url("https://api.example.com/test")
                .build()
        whenever(chain.request()).thenReturn(request)

        // When
        authInterceptor.intercept(chain)

        // Then
        org.mockito.kotlin.verify(chain).proceed(
            org.mockito.kotlin.check { modifiedRequest ->
                assertEquals(
                    "NexusNews-Android/1.0",
                    modifiedRequest.header(ApiConstants.USER_AGENT_HEADER),
                )
            },
        )
    }

    @Test
    fun `intercept adds accept header`() {
        // Given
        val request =
            Request
                .Builder()
                .url("https://api.example.com/test")
                .build()
        whenever(chain.request()).thenReturn(request)

        // When
        authInterceptor.intercept(chain)

        // Then
        org.mockito.kotlin.verify(chain).proceed(
            org.mockito.kotlin.check { modifiedRequest ->
                assertEquals(
                    ApiConstants.APPLICATION_JSON,
                    modifiedRequest.header(ApiConstants.ACCEPT_HEADER),
                )
            },
        )
    }

    @Test
    fun `intercept adds authorization header when API key is set`() {
        // Given
        authInterceptor.setApiKey("test-api-key")
        val request =
            Request
                .Builder()
                .url("https://api.example.com/test")
                .build()
        whenever(chain.request()).thenReturn(request)

        // When
        authInterceptor.intercept(chain)

        // Then
        org.mockito.kotlin.verify(chain).proceed(
            org.mockito.kotlin.check { modifiedRequest ->
                assertEquals(
                    "Bearer test-api-key",
                    modifiedRequest.header(ApiConstants.AUTHORIZATION_HEADER),
                )
            },
        )
    }

    @Test
    fun `intercept does not add authorization header when API key is not set`() {
        // Given
        val request =
            Request
                .Builder()
                .url("https://api.example.com/test")
                .build()
        whenever(chain.request()).thenReturn(request)

        // When
        authInterceptor.intercept(chain)

        // Then
        org.mockito.kotlin.verify(chain).proceed(
            org.mockito.kotlin.check { modifiedRequest ->
                assertNull(modifiedRequest.header(ApiConstants.AUTHORIZATION_HEADER))
            },
        )
    }

    @Test
    fun `intercept skips authentication when No-Authentication header present`() {
        // Given
        val request =
            Request
                .Builder()
                .url("https://api.example.com/test")
                .header("No-Authentication", "true")
                .build()
        whenever(chain.request()).thenReturn(request)

        // When
        authInterceptor.intercept(chain)

        // Then
        org.mockito.kotlin
            .verify(chain)
            .proceed(request) // Original request unchanged
    }

    @Test
    fun `intercept preserves existing headers while adding new ones`() {
        // Given
        authInterceptor.setApiKey("test-key")
        val request =
            Request
                .Builder()
                .url("https://api.example.com/test")
                .header("Custom-Header", "custom-value")
                .build()
        whenever(chain.request()).thenReturn(request)

        // When
        authInterceptor.intercept(chain)

        // Then
        org.mockito.kotlin.verify(chain).proceed(
            org.mockito.kotlin.check { modifiedRequest ->
                assertEquals("custom-value", modifiedRequest.header("Custom-Header"))
                assertNotNull(modifiedRequest.header(ApiConstants.USER_AGENT_HEADER))
                assertNotNull(modifiedRequest.header(ApiConstants.AUTHORIZATION_HEADER))
                assertNotNull(modifiedRequest.header(ApiConstants.ACCEPT_HEADER))
            },
        )
    }

    @Test
    fun `setApiKey updates the stored API key`() {
        // Given
        val newApiKey = "new-api-key"

        // When
        authInterceptor.setApiKey(newApiKey)

        // Then - Verify by checking that authorization header is added
        val request =
            Request
                .Builder()
                .url("https://api.example.com/test")
                .build()
        whenever(chain.request()).thenReturn(request)

        authInterceptor.intercept(chain)

        org.mockito.kotlin.verify(chain).proceed(
            org.mockito.kotlin.check { modifiedRequest ->
                assertEquals(
                    "Bearer new-api-key",
                    modifiedRequest.header(ApiConstants.AUTHORIZATION_HEADER),
                )
            },
        )
    }
}
