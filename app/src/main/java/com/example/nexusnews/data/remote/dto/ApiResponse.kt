package com.example.nexusnews.data.remote.dto

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

/**
 * Generic wrapper for API responses.
 * Provides consistent structure for success and error handling across all API endpoints.
 *
 * @param T The type of data expected in the response
 */
@JsonClass(generateAdapter = true)
data class ApiResponse<T>(
    @Json(name = "status")
    val status: String,

    @Json(name = "data")
    val data: T? = null,

    @Json(name = "error")
    val error: ErrorResponse? = null,

    @Json(name = "message")
    val message: String? = null
) {
    /**
     * Checks if the response is successful based on status field.
     *
     * @return true if status is "ok" or "success", false otherwise
     */
    fun isSuccessful(): Boolean = status == "ok" || status == "success"

    /**
     * Returns data or throws exception if not successful.
     * Should only be called after checking isSuccessful().
     *
     * @return The response data
     * @throws ApiException if response is not successful or data is null
     */
    fun getDataOrThrow(): T {
        if (!isSuccessful() || data == null) {
            throw ApiException(
                message = error?.message ?: message ?: "Unknown API error",
                code = error?.code
            )
        }
        return data
    }
}

/**
 * Exception thrown when API returns an error response.
 * Contains error details from the API response.
 */
class ApiException(
    message: String,
    val code: String? = null
) : Exception(message)