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
    ) : BaseViewModel() {
        private val _uiState = MutableStateFlow<UiState<List<Article>>>(UiState.Idle)
        val uiState: StateFlow<UiState<List<Article>>> = _uiState.asStateFlow()

        private val _isRefreshing = MutableStateFlow(false)
        val isRefreshing: StateFlow<Boolean> = _isRefreshing.asStateFlow()

        private val _selectedCategory = MutableStateFlow<NewsCategory?>(null)
        val selectedCategory: StateFlow<NewsCategory?> = _selectedCategory.asStateFlow()

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
            if (_uiState.value.isLoading()) return // Prevent multiple simultaneous loads

            viewModelScope.launch(exceptionHandler) {
                Timber.d("Loading news articles (forceRefresh: $forceRefresh)")

                _uiState.update { UiState.Loading }

                newsRepository
                    .getArticles(forceRefresh)
                    .collect { result ->
                        val uiState = result.toUiState()
                        _uiState.update { uiState }

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
         * Toggles favorite status for a bookmarked article.
         */
        fun toggleFavorite(articleId: String) {
            viewModelScope.launch(exceptionHandler) {
                try {
                    newsRepository.toggleFavorite(articleId)
                    Timber.d("Favorite toggled for: $articleId")
                } catch (e: Exception) {
                    Timber.e(e, "Failed to toggle favorite for: $articleId")
                }
            }
        }
    }
