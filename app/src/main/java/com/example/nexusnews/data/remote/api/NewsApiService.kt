package com.example.nexusnews.data.remote.api

import com.example.nexusnews.data.remote.dto.NewsApiResponse
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query

/**
 * NewsAPI service interface.
 * Provides endpoints for fetching news articles from NewsAPI.
 */
interface NewsApiService {
    /**
     * Fetches top headlines from NewsAPI.
     *
     * @param apiKey NewsAPI key for authentication
     * @param country 2-letter ISO 3166-1 code of the country (e.g., "us", "gb")
     * @param category News category (business, entertainment, general, health, science, sports, technology)
     * @param sources Comma-separated string of news sources or blogs
     * @param q Keywords or phrases to search for
     * @param pageSize Number of results per page (max 100, default 20)
     * @param page Page number for pagination
     * @return Response containing NewsApiResponse with articles
     */
    @GET("v2/top-headlines")
    suspend fun getTopHeadlines(
        @Query("apiKey") apiKey: String,
        @Query("country") country: String? = null,
        @Query("category") category: String? = null,
        @Query("sources") sources: String? = null,
        @Query("q") query: String? = null,
        @Query("pageSize") pageSize: Int = 20,
        @Query("page") page: Int = 1,
    ): Response<NewsApiResponse>

    /**
     * Fetches all articles from NewsAPI based on search criteria.
     *
     * @param apiKey NewsAPI key for authentication
     * @param q Keywords or phrases to search for (required)
     * @param sources Comma-separated string of news sources or blogs
     * @param domains Comma-separated string of domains to restrict search to
     * @param excludeDomains Comma-separated string of domains to exclude
     * @param from Start date in ISO 8601 format (e.g., "2023-11-01" or "2023-11-01T12:00:00")
     * @param to End date in ISO 8601 format
     * @param language 2-letter ISO-639-1 code of the language
     * @param sortBy Sort order (relevancy, popularity, publishedAt)
     * @param pageSize Number of results per page (max 100, default 20)
     * @param page Page number for pagination
     * @return Response containing NewsApiResponse with articles
     */
    @GET("v2/everything")
    suspend fun getEverything(
        @Query("apiKey") apiKey: String,
        @Query("q") query: String,
        @Query("sources") sources: String? = null,
        @Query("domains") domains: String? = null,
        @Query("excludeDomains") excludeDomains: String? = null,
        @Query("from") from: String? = null,
        @Query("to") to: String? = null,
        @Query("language") language: String? = null,
        @Query("sortBy") sortBy: String? = null,
        @Query("pageSize") pageSize: Int = 20,
        @Query("page") page: Int = 1,
    ): Response<NewsApiResponse>
}
