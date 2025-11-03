package com.example.nexusnews.util

import retrofit2.HttpException
import java.io.IOException
import java.net.SocketTimeoutException
import java.net.UnknownHostException

/**
 * Centralized error handling utility.
 * Converts exceptions to user-friendly messages.
 */
object ErrorHandler {
    
    // HTTP Status Code Constants
    private const val HTTP_BAD_REQUEST = 400
    private const val HTTP_UNAUTHORIZED = 401
    private const val HTTP_FORBIDDEN = 403
    private const val HTTP_NOT_FOUND = 404
    private const val HTTP_TOO_MANY_REQUESTS = 429
    private const val HTTP_REQUEST_TIMEOUT = 408
    private const val HTTP_INTERNAL_SERVER_ERROR = 500
    private const val HTTP_BAD_GATEWAY = 502
    private const val HTTP_SERVICE_UNAVAILABLE = 503
    
    /**
     * Converts an exception to a user-friendly error message.
     */
    fun getErrorMessage(throwable: Throwable): String {
        return when (throwable) {
            is UnknownHostException -> "No internet connection. Please check your network."
            is SocketTimeoutException -> "Connection timeout. Please try again."
            is IOException -> "Network error. Please check your connection."
            is HttpException -> getHttpErrorMessage(throwable)
            else -> throwable.message ?: "An unexpected error occurred."
        }
    }
    
    /**
     * Converts HTTP exception to specific error message.
     */
    private fun getHttpErrorMessage(exception: HttpException): String {
        return when (exception.code()) {
            HTTP_BAD_REQUEST -> "Bad request. Please try again."
            HTTP_UNAUTHORIZED -> "Unauthorized. Please check your credentials."
            HTTP_FORBIDDEN -> "Access forbidden."
            HTTP_NOT_FOUND -> "Resource not found."
            HTTP_TOO_MANY_REQUESTS -> "Too many requests. Please try again later."
            HTTP_INTERNAL_SERVER_ERROR, HTTP_BAD_GATEWAY,
            HTTP_SERVICE_UNAVAILABLE -> "Server error. Please try again later."
            else -> "HTTP error ${exception.code()}: ${exception.message()}"
        }
    }
    
    /**
     * Checks if error is recoverable (user can retry).
     */
    fun isRecoverable(throwable: Throwable): Boolean {
        return when (throwable) {
            is UnknownHostException,
            is SocketTimeoutException,
            is IOException -> true

            is HttpException ->
                throwable.code() in listOf(
                    HTTP_REQUEST_TIMEOUT,
                    HTTP_TOO_MANY_REQUESTS,
                    HTTP_INTERNAL_SERVER_ERROR,
                    HTTP_BAD_GATEWAY,
                    HTTP_SERVICE_UNAVAILABLE
                )
            else -> false
        }
    }
}

