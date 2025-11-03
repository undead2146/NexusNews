package com.example.nexusnews.data.remote.interceptor

import com.example.nexusnews.util.constants.ApiConstants
import com.example.nexusnews.util.constants.NetworkConstants
import okhttp3.Interceptor
import okhttp3.Response
import timber.log.Timber
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Interceptor for adding authentication headers to all API requests.
 * Handles User-Agent and Authorization headers automatically.
 * API keys will be injected via secure storage in future implementations.
 */
@Singleton
class AuthInterceptor @Inject constructor() : Interceptor {

    // TODO: Inject from secure storage (EncryptedSharedPreferences/DataStore)
    private var apiKey: String? = null

    /**
     * Sets the API key for authentication.
     * This is a temporary method - API keys should be stored securely.
     *
     * @param key The API key to use for authentication
     */
    fun setApiKey(key: String) {
        apiKey = key
        Timber.d("API key configured for authentication")
    }

    override fun intercept(chain: Interceptor.Chain): Response {
        val originalRequest = chain.request()

        // Skip authentication for requests that explicitly don't need it
        if (originalRequest.header("No-Authentication") != null) {
            return chain.proceed(originalRequest)
        }

        val requestBuilder = originalRequest.newBuilder()
            .header(ApiConstants.USER_AGENT_HEADER, NetworkConstants.USER_AGENT)
            .header(ApiConstants.ACCEPT_HEADER, ApiConstants.APPLICATION_JSON)

        // Add API key if available
        apiKey?.let { key ->
            requestBuilder.header(ApiConstants.AUTHORIZATION_HEADER, "Bearer $key")
            Timber.d("Added authentication header to ${originalRequest.url}")
        } ?: Timber.w("No API key configured - requests may fail")

        return chain.proceed(requestBuilder.build())
    }
}