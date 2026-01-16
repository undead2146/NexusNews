package com.example.nexusnews.data.repository

import com.example.nexusnews.data.local.dao.ArticleDao
import com.example.nexusnews.data.local.dao.BookmarkDao
import com.example.nexusnews.data.mapper.toDomain
import com.example.nexusnews.data.mapper.toDomainList
import com.example.nexusnews.data.mapper.toEntity
import com.example.nexusnews.data.mapper.toEntityList
import com.example.nexusnews.data.remote.NewsRemoteDataSource
import com.example.nexusnews.domain.model.Article
import com.example.nexusnews.domain.repository.NewsRepository
import com.example.nexusnews.util.Result
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map
import timber.log.Timber
import java.io.IOException
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Implementation of NewsRepository with Room database caching and bookmark support.
 */
@Singleton
class NewsRepositoryImpl
    @Inject
    constructor(
        private val remoteDataSource: NewsRemoteDataSource,
        private val articleDao: ArticleDao,
        private val bookmarkDao: BookmarkDao,
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
                    // Cache articles
                    articleDao.insertArticles(articles.toEntityList())
                    emit(Result.Success(articles))
                } catch (e: Exception) {
                    Timber.e(e, "Error fetching articles: ${e.message}")
                    emit(Result.Error(e))
                }
            }

        override fun getArticleById(id: String): Flow<Result<Article>> =
            flow {
                emit(Result.Loading)
                try {
                    val articleEntity = articleDao.getArticleById(id)
                    if (articleEntity != null) {
                        emit(Result.Success(articleEntity.toDomain()))
                    } else {
                        emit(Result.Error(Exception("Article not found")))
                    }
                } catch (e: Exception) {
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
                    Timber.e(e, "Error searching articles with query: $query. Message: ${e.message}")
                    emit(Result.Error(e))
                }
            }

        // Bookmark operations

        override fun getBookmarks(): Flow<Result<List<Article>>> =
            flow {
                emit(Result.Loading)
                try {
                    bookmarkDao.getAllBookmarks().collect { entities ->
                        emit(Result.Success(entities.toDomainList()))
                    }
                } catch (e: Exception) {
                    Timber.e(e, "Error fetching bookmarks")
                    emit(Result.Error(e))
                }
            }

        override fun getFavorites(): Flow<Result<List<Article>>> =
            flow {
                emit(Result.Loading)
                try {
                    bookmarkDao.getFavorites().collect { entities ->
                        emit(Result.Success(entities.toDomainList()))
                    }
                } catch (e: Exception) {
                    Timber.e(e, "Error fetching favorites")
                    emit(Result.Error(e))
                }
            }

        override fun isBookmarked(articleId: String): Flow<Boolean> = bookmarkDao.isBookmarked(articleId)

        override suspend fun addBookmark(article: Article) {
            try {
                Timber.d("Adding bookmark for article: ${article.id}")
                bookmarkDao.addBookmark(article.toEntity())
            } catch (e: Exception) {
                Timber.e(e, "Error adding bookmark")
                throw e
            }
        }

        override suspend fun removeBookmark(articleId: String) {
            try {
                Timber.d("Removing bookmark for article: $articleId")
                bookmarkDao.deleteBookmark(articleId)
            } catch (e: Exception) {
                Timber.e(e, "Error removing bookmark")
                throw e
            }
        }

        override suspend fun toggleFavorite(articleId: String) {
            try {
                val bookmark = bookmarkDao.getBookmark(articleId)
                if (bookmark != null) {
                    val newFavoriteStatus = !bookmark.isFavorite
                    Timber.d("Toggling favorite for article: $articleId to $newFavoriteStatus")
                    bookmarkDao.updateFavoriteStatus(articleId, newFavoriteStatus)
                } else {
                    Timber.w("Cannot toggle favorite - article not bookmarked: $articleId")
                }
            } catch (e: Exception) {
                Timber.e(e, "Error toggling favorite")
                throw e
            }
        }
    }
