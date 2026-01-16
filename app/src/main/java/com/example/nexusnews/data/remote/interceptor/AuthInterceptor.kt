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
class AuthInterceptor
    @Inject
    constructor() : Interceptor {
        // Initialize with key from BuildConfig
        private var apiKey: String? = com.example.nexusnews.BuildConfig.NEWS_API_KEY.takeIf { it.isNotBlank() }

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
            val host = originalRequest.url.host

            // Skip authentication for requests that explicitly don't need it
            if (originalRequest.header("No-Authentication") != null) {
                return chain.proceed(originalRequest)
            }

            val requestBuilder =
                originalRequest
                    .newBuilder()
                    .header(ApiConstants.USER_AGENT_HEADER, NetworkConstants.USER_AGENT)
                    .header(ApiConstants.ACCEPT_HEADER, ApiConstants.APPLICATION_JSON)

            // Add authorization based on host
            when {
                host.contains("newsapi.org", ignoreCase = true) -> {
                    Timber.d("Processing NewsAPI request. Configured Key Length: ${apiKey?.length ?: 0}")
                    apiKey?.let { key ->
                        requestBuilder.header(ApiConstants.API_KEY_HEADER, key)
                        Timber.d("Added NewsAPI key to header: ${originalRequest.url}")
                    } ?: Timber.w("No NewsAPI key configured in BuildConfig")
                }
                host.contains("openrouter.ai", ignoreCase = true) -> {
                    val openRouterKey = com.example.nexusnews.BuildConfig.OPENROUTER_API_KEY
                    Timber.d("Processing OpenRouter request. Configured Key Length: ${openRouterKey.length}")
                    if (openRouterKey.isNotBlank()) {
                        requestBuilder.header(ApiConstants.AUTHORIZATION_HEADER, "Bearer $openRouterKey")
                        Timber.d("Added OpenRouter API key to header")
                    } else {
                        Timber.w("No OpenRouter API key configured")
                    }
                }
            }

            return chain.proceed(requestBuilder.build())
        }
    }
