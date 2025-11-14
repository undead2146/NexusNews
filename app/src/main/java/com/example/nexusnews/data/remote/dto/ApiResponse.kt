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
    val message: String? = null,
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
                code = error?.code,
            )
        }
        return data
    }
}

/**
 * NewsAPI specific response wrapper.
 * Matches NewsAPI's response structure with status, totalResults, and articles.
 */
@JsonClass(generateAdapter = true)
data class NewsApiResponse(
    @Json(name = "status")
    val status: String,
    @Json(name = "totalResults")
    val totalResults: Int,
    @Json(name = "articles")
    val articles: List<NewsApiArticle>,
) {
    /**
     * Checks if the NewsAPI response is successful.
     *
     * @return true if status is "ok", false otherwise
     */
    fun isSuccessful(): Boolean = status == "ok"
}

/**
 * NewsAPI Article DTO.
 * Represents a single news article from NewsAPI.
 */
@JsonClass(generateAdapter = true)
data class NewsApiArticle(
    @Json(name = "source")
    val source: NewsApiSource,
    @Json(name = "author")
    val author: String?,
    @Json(name = "title")
    val title: String,
    @Json(name = "description")
    val description: String?,
    @Json(name = "url")
    val url: String,
    @Json(name = "urlToImage")
    val urlToImage: String?,
    @Json(name = "publishedAt")
    val publishedAt: String,
    @Json(name = "content")
    val content: String?,
)

/**
 * NewsAPI Source DTO.
 * Represents the source of a news article.
 */
@JsonClass(generateAdapter = true)
data class NewsApiSource(
    @Json(name = "id")
    val id: String?,
    @Json(name = "name")
    val name: String,
)

/**
 * Exception thrown when API returns an error response.
 * Contains error details from the API response.
 */
class ApiException(
    message: String,
    val code: String? = null,
) : Exception(message)
