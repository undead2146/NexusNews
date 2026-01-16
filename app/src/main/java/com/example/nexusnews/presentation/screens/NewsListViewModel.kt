package com.example.nexusnews.presentation.screens

import androidx.lifecycle.viewModelScope
import com.example.nexusnews.data.local.datastore.CategoryPreferencesDataStore
import com.example.nexusnews.domain.model.Article
import com.example.nexusnews.domain.model.NewsCategory
import com.example.nexusnews.domain.repository.NewsRepository
import com.example.nexusnews.presentation.common.BaseViewModel
import com.example.nexusnews.presentation.common.UiState
import com.example.nexusnews.presentation.common.toUiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject

/**
 * ViewModel for the News List screen.
 * Manages news article fetching and UI state.
 */
@HiltViewModel
class NewsListViewModel
    @Inject
    constructor(
        private val newsRepository: NewsRepository,
        private val categoryPreferences: CategoryPreferencesDataStore,
    ) : BaseViewModel<UiState<List<Article>>>(UiState.Idle) {
        // Alias state to uiState for compatibility with View
        val uiState: StateFlow<UiState<List<Article>>> = state

        private val _isRefreshing = MutableStateFlow(false)
        val isRefreshing: StateFlow<Boolean> = _isRefreshing.asStateFlow()

        private val _selectedCategory = MutableStateFlow<NewsCategory?>(null)
        val selectedCategory: StateFlow<NewsCategory?> = _selectedCategory.asStateFlow()

        // Pagination state
        private val _currentPage = MutableStateFlow(1)
        private val _canLoadMore = MutableStateFlow(true)
        val canLoadMore: StateFlow<Boolean> = _canLoadMore.asStateFlow()

        private val _isLoadingMore = MutableStateFlow(false)
        val isLoadingMore: StateFlow<Boolean> = _isLoadingMore.asStateFlow()

        init {
            // Observe category preference changes
            viewModelScope.launch {
                categoryPreferences.selectedCategory.collect { category ->
                    _selectedCategory.value = category
                    loadNews()
                }
            }
        }

        /**
         * Load news articles from the repository.
         */
        fun loadNews(forceRefresh: Boolean = false) {
            if (currentState.isLoading()) return // Prevent multiple simultaneous loads

            viewModelScope.launch(exceptionHandler) {
                val category = _selectedCategory.value
                Timber.d("Loading news articles (category: ${category?.value ?: "all"}, forceRefresh: $forceRefresh)")

                // Reset pagination when loading fresh articles
                _currentPage.value = 1
                _canLoadMore.value = true

                updateState { UiState.Loading }

                newsRepository
                    .getArticles(forceRefresh, category)
                    .collect { result ->
                        val uiState = result.toUiState()
                        updateState { uiState }

                        when (uiState) {
                            is UiState.Success -> {
                                Timber.d("Successfully loaded ${uiState.data.size} articles")
                            }
                            is UiState.Error -> {
                                Timber.e(uiState.throwable, "Failed to load articles: ${uiState.message}")
                            }
                            else -> {
                                // Loading state already handled
                            }
                        }
                    }
            }
        }

        /**
         * Refresh news articles (pull-to-refresh).
         */
        fun refreshNews() {
            _isRefreshing.update { true }
            loadNews(forceRefresh = true)
            _isRefreshing.update { false }
        }

        /**
         * Select a category to filter news articles.
         *
         * @param category The category to select, or null for "All"
         */
        fun selectCategory(category: NewsCategory?) {
            viewModelScope.launch {
                categoryPreferences.setSelectedCategory(category)
            }
        }

        /**
         * Retry loading news after an error.
         */
        fun retryLoadNews() {
            loadNews(forceRefresh = false)
        }

        /**
         * Load more articles (pagination).
         */
        fun loadMoreArticles() {
            if (_isLoadingMore.value || !_canLoadMore.value || currentState.isLoading()) return

            viewModelScope.launch(exceptionHandler) {
                _isLoadingMore.value = true
                val nextPage = _currentPage.value + 1
                val category = _selectedCategory.value

                Timber.d("Loading more articles (page: $nextPage, category: ${category?.value ?: "all"})")

                newsRepository
                    .getArticlesPage(nextPage, category)
                    .collect { result ->
                        when (result) {
                            is com.example.nexusnews.util.Result.Success -> {
                                val newArticles = result.data
                                if (newArticles.isEmpty()) {
                                    _canLoadMore.value = false
                                    Timber.d("No more articles to load")
                                } else {
                                    // Append to existing articles
                                    val currentArticles = (currentState as? UiState.Success)?.data ?: emptyList()
                                    val updatedArticles = currentArticles + newArticles
                                    updateState { UiState.Success(updatedArticles) }
                                    _currentPage.value = nextPage
                                    Timber.d("Loaded ${newArticles.size} more articles (total: ${updatedArticles.size})")
                                }
                            }
                            is com.example.nexusnews.util.Result.Error -> {
                                Timber.e(result.exception, "Failed to load more articles")
                                // Don't update state, just stop loading
                            }
                            else -> {}
                        }
                        _isLoadingMore.value = false
                    }
            }
        }

        // Bookmark operations

        /**
         * Toggles bookmark status for an article.
         */
        fun toggleBookmark(article: Article) {
            viewModelScope.launch(exceptionHandler) {
                try {
                    if (newsRepository.isBookmarked(article.id).first()) {
                        newsRepository.removeBookmark(article.id)
                        Timber.d("Bookmark removed: ${article.id}")
                    } else {
                        newsRepository.addBookmark(article)
                        Timber.d("Bookmark added: ${article.id}")
                    }
                } catch (e: Exception) {
                    Timber.e(e, "Failed to toggle bookmark for: ${article.id}")
                }
            }
        }

        /**
         * Checks if an article is bookmarked.
         */
        fun isBookmarked(articleId: String) = newsRepository.isBookmarked(articleId)

        /**
         * Checks if an article is a favorite.
         */
        fun isFavorite(articleId: String) = newsRepository.isFavorite(articleId)

        /**
         * Toggles favorite status for a bookmarked article.
         */
        fun toggleFavorite(article: Article) {
            viewModelScope.launch(exceptionHandler) {
                try {
                    newsRepository.toggleFavorite(article)
                    Timber.d("Favorite toggled for: ${article.id}")
                } catch (e: Exception) {
                    Timber.e(e, "Failed to toggle favorite for: ${article.id}")
                }
            }
        }
    }
