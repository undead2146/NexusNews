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
        // NOTE: API key injection from secure storage will be implemented in future updates
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

            val requestBuilder =
                originalRequest
                    .newBuilder()
                    .header(ApiConstants.USER_AGENT_HEADER, NetworkConstants.USER_AGENT)
                    .header(ApiConstants.ACCEPT_HEADER, ApiConstants.APPLICATION_JSON)

            // Add API key as query parameter for NewsAPI
            // Remove any existing apiKey parameter first to avoid duplicates
            apiKey?.let { key ->
                val originalUrl = originalRequest.url
                val urlBuilder = originalUrl.newBuilder()

                // Remove existing apiKey parameters (may be empty string from method call)
                val querySize = originalUrl.querySize
                val paramsToKeep = mutableListOf<Pair<String, String>>()
                for (i in 0 until querySize) {
                    val name = originalUrl.queryParameterName(i)
                    val value = originalUrl.queryParameterValue(i)
                    if (name != "apiKey" && value != null) {
                        paramsToKeep.add(name to value)
                    }
                }

                // Rebuild URL without apiKey, then add the real one
                urlBuilder.query(null) // Clear all query params
                paramsToKeep.forEach { (name, value) ->
                    urlBuilder.addQueryParameter(name, value)
                }
                urlBuilder.addQueryParameter("apiKey", key)

                requestBuilder.url(urlBuilder.build())
                Timber.d("Added API key as query parameter to ${originalRequest.url}")
            } ?: Timber.w("No API key configured - requests may fail")

            return chain.proceed(requestBuilder.build())
        }
    }
