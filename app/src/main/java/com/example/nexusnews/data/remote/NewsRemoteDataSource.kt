package com.example.nexusnews.data.remote

import com.example.nexusnews.data.mapper.ArticleMapper
import com.example.nexusnews.data.remote.api.NewsApiService
import com.example.nexusnews.data.remote.dto.ApiException
import com.example.nexusnews.data.remote.dto.NewsApiResponse
import com.example.nexusnews.domain.model.Article
import retrofit2.Response
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Remote data source for news operations using NewsAPI.
 * Implements the RemoteDataSource interface.
 */
@Singleton
class NewsRemoteDataSource
    @Inject
    constructor(
        private val newsApiService: NewsApiService,
        networkMonitor: com.example.nexusnews.data.util.NetworkMonitor,
    ) : NetworkDataSource(networkMonitor),
        RemoteDataSource {
        /**
         * Fetches top headlines from NewsAPI.
         *
         * @param country 2-letter ISO 3166-1 country code (optional)
         * @param category News category (optional)
         * @param query Search keywords (optional)
         * @param page Page number for pagination (default: 1)
         * @param pageSize Number of articles per page (default: 20)
         * @return List of Article domain models
         * @throws ApiException if API returns an error
         * @throws IOException if network request fails
         */
        suspend fun getTopHeadlines(
            country: String? = null,
            category: String? = null,
            query: String? = null,
            page: Int = 1,
            pageSize: Int = 20,
        ): List<Article> =
            safeApiCall {
                val response =
                    newsApiService.getTopHeadlines(
                        country = country,
                        category = category,
                        query = query,
                        pageSize = pageSize,
                        page = page,
                    )
                handleApiResponse(response)
            }

        /**
         * Searches for articles using NewsAPI everything endpoint.
         *
         * @param query Search keywords (required)
         * @param sources Comma-separated news sources (optional)
         * @param from Start date in ISO 8601 format (optional)
         * @param to End date in ISO 8601 format (optional)
         * @param language 2-letter ISO-639-1 language code (optional)
         * @param sortBy Sort order (optional)
         * @param page Page number for pagination (default: 1)
         * @param pageSize Number of articles per page (default: 20)
         * @return List of Article domain models
         * @throws ApiException if API returns an error
         * @throws IOException if network request fails
         */
        suspend fun searchArticles(
            query: String,
            sources: String? = null,
            from: String? = null,
            to: String? = null,
            language: String? = null,
            sortBy: String? = null,
            page: Int = 1,
            pageSize: Int = 20,
        ): List<Article> =
            safeApiCall {
                val response =
                    newsApiService.getEverything(
                        query = query,
                        sources = sources,
                        from = from,
                        to = to,
                        language = language,
                        sortBy = sortBy,
                        pageSize = pageSize,
                        page = page,
                    )
                handleApiResponse(response)
            }

        /**
         * Handles the API response and converts it to domain models.
         *
         * @param response Retrofit Response containing NewsApiResponse
         * @return List of Article domain models
         * @throws ApiException if API returns an error status
         */
        private fun handleApiResponse(response: Response<NewsApiResponse>): List<Article> {
            if (!response.isSuccessful) {
                throw ApiException(
                    code = response.code().toString(),
                    message = response.message(),
                )
            }

            val body = response.body()
            if (body == null || body.status != "ok") {
                val code = if (body == null) "null_response" else "api_error"
                val message =
                    if (body == null) {
                        "API returned null response body"
                    } else {
                        "API returned error status: ${body.status}"
                    }
                throw ApiException(
                    code = code,
                    message = message,
                )
            }

            val articles = body.articles
            return ArticleMapper.toDomainList(articles)
        }
    }
