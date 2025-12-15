package com.example.nexusnews.presentation.screens.bookmarks

import androidx.lifecycle.viewModelScope
import com.example.nexusnews.domain.model.Article
import com.example.nexusnews.domain.repository.NewsRepository
import com.example.nexusnews.presentation.common.BaseViewModel
import com.example.nexusnews.presentation.common.UiState
import com.example.nexusnews.presentation.common.toUiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject

/**
 * ViewModel for the Bookmarks screen.
 * Manages bookmarked articles and favorites filtering.
 */
@HiltViewModel
class BookmarksViewModel
    @Inject
    constructor(
        private val newsRepository: NewsRepository,
    ) : BaseViewModel() {
        private val _showFavoritesOnly = MutableStateFlow(false)
        val showFavoritesOnly: StateFlow<Boolean> = _showFavoritesOnly.asStateFlow()

        /**
         * Bookmarks state that switches between all bookmarks and favorites.
         */
        val bookmarks: StateFlow<UiState<List<Article>>> =
            showFavoritesOnly
                .flatMapLatest { favoritesOnly ->
                    if (favoritesOnly) {
                        newsRepository.getFavorites()
                    } else {
                        newsRepository.getBookmarks()
                    }
                }.map { it.toUiState() }
                .stateIn(
                    viewModelScope,
                    SharingStarted.WhileSubscribed(5000),
                    UiState.Loading,
                )

        /**
         * Toggles between showing all bookmarks and favorites only.
         */
        fun toggleFavoritesFilter() {
            _showFavoritesOnly.update { !it }
            Timber.d("Favorites filter toggled to: ${_showFavoritesOnly.value}")
        }

        /**
         * Removes a bookmark.
         */
        fun removeBookmark(articleId: String) {
            viewModelScope.launch(exceptionHandler) {
                try {
                    newsRepository.removeBookmark(articleId)
                    Timber.d("Bookmark removed: $articleId")
                } catch (e: Exception) {
                    Timber.e(e, "Failed to remove bookmark: $articleId")
                }
            }
        }

        /**
         * Toggles the favorite status of a bookmarked article.
         */
        fun toggleFavorite(articleId: String) {
            viewModelScope.launch(exceptionHandler) {
                try {
                    newsRepository.toggleFavorite(articleId)
                    Timber.d("Favorite toggled for: $articleId")
                } catch (e: Exception) {
                    Timber.e(e, "Failed to toggle favorite: $articleId")
                }
            }
        }
    }
