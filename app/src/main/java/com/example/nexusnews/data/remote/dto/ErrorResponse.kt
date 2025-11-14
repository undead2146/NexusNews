package com.example.nexusnews.data.remote.dto

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

/**
 * Standard error response from API endpoints.
 * Provides structured error information for debugging and user feedback.
 */
@JsonClass(generateAdapter = true)
data class ErrorResponse(
    @Json(name = "code")
    val code: String? = null,
    @Json(name = "message")
    val message: String,
    @Json(name = "details")
    val details: Map<String, Any>? = null,
    @Json(name = "timestamp")
    val timestamp: Long? = null,
)
