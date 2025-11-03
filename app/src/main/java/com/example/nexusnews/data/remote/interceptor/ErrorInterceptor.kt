package com.example.nexusnews.data.remote.interceptor

import com.example.nexusnews.data.remote.dto.ErrorResponse
import com.squareup.moshi.Moshi
import okhttp3.Interceptor
import okhttp3.Response
import timber.log.Timber
import java.io.IOException
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Interceptor for handling and parsing HTTP errors from API responses.
 * Converts HTTP error responses into typed exceptions for better error handling.
 */
@Singleton
class ErrorInterceptor @Inject constructor(
    private val moshi: Moshi
) : Interceptor {

    private val errorAdapter = moshi.adapter(ErrorResponse::class.java)

    override fun intercept(chain: Interceptor.Chain): Response {
        val request = chain.request()
        val response = chain.proceed(request)

        // If response is successful, return it as-is
        if (response.isSuccessful) {
            return response
        }

        // Parse error response body
        val errorBody = response.body?.string()
        val errorResponse = try {
            errorBody?.let { errorAdapter.fromJson(it) }
        } catch (e: Exception) {
            Timber.e(e, "Failed to parse error response body")
            null
        }

        val errorMessage = errorResponse?.message
            ?: response.message
            ?: "HTTP ${response.code}"

        Timber.e("API Error [${response.code}]: $errorMessage for ${request.url}")

        // Throw appropriate exception based on HTTP status code
        throw when (response.code) {
            401 -> UnauthorizedException(errorMessage)
            403 -> ForbiddenException(errorMessage)
            404 -> NotFoundException(errorMessage)
            429 -> RateLimitException(errorMessage)
            in 500..599 -> ServerException(errorMessage, response.code)
            else -> HttpException(errorMessage, response.code)
        }
    }
}

/**
 * Custom exceptions for different types of HTTP errors.
 * These provide more specific error handling than generic IOException.
 */
sealed class NetworkException(message: String) : IOException(message)

class UnauthorizedException(message: String) : NetworkException(message)
class ForbiddenException(message: String) : NetworkException(message)
class NotFoundException(message: String) : NetworkException(message)
class RateLimitException(message: String) : NetworkException(message)
class ServerException(message: String, val code: Int) : NetworkException(message)
class HttpException(message: String, val code: Int) : NetworkException(message)