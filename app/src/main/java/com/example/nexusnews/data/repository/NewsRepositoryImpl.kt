package com.example.nexusnews.data.repository

import com.example.nexusnews.data.remote.NewsRemoteDataSource
import com.example.nexusnews.domain.model.Article
import com.example.nexusnews.domain.repository.NewsRepository
import com.example.nexusnews.util.Result
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import timber.log.Timber
import java.io.IOException
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Implementation of NewsRepository using direct network calls.
 * Local caching will be added when Room database is implemented.
 */
@Singleton
class NewsRepositoryImpl
    @Inject
    constructor(
        private val remoteDataSource: NewsRemoteDataSource,
    ) : NewsRepository {
        override fun getArticles(forceRefresh: Boolean): Flow<Result<List<Article>>> =
            flow {
                emit(Result.Loading)
                try {
                    Timber.d("Fetching articles from remote API")
                    // Default to top headlines for general article fetching
                    val articles =
                        remoteDataSource.getTopHeadlines(
                            country = "us", // Default to US news
                            pageSize = 20,
                        )
                    emit(Result.Success(articles))
                } catch (e: IOException) {
                    Timber.e(e, "Error fetching articles")
                    emit(Result.Error(e))
                }
            }

        override fun getArticleById(id: String): Flow<Result<Article>> =
            flow {
                emit(Result.Loading)
                try {
                    // Single article fetching not yet implemented
                    // For now, this is a placeholder that returns an error
                    emit(Result.Error(UnsupportedOperationException("Single article fetching not yet implemented")))
                } catch (e: IOException) {
                    Timber.e(e, "Error fetching article by ID: $id")
                    emit(Result.Error(e))
                }
            }

        override fun searchArticles(query: String): Flow<Result<List<Article>>> =
            flow {
                emit(Result.Loading)
                try {
                    Timber.d("Searching articles with query: $query")
                    val articles =
                        remoteDataSource.searchArticles(
                            query = query,
                            sortBy = "relevancy", // Sort by relevance for search results
                            pageSize = 20,
                        )
                    emit(Result.Success(articles))
                } catch (e: IOException) {
                    Timber.e(e, "Error searching articles with query: $query")
                    emit(Result.Error(e))
                }
            }
    }
