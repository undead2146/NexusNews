package com.example.nexusnews.presentation.screens

import androidx.lifecycle.viewModelScope
import com.example.nexusnews.domain.model.Article
import com.example.nexusnews.domain.repository.NewsRepository
import com.example.nexusnews.presentation.common.BaseViewModel
import com.example.nexusnews.presentation.common.UiState
import com.example.nexusnews.util.Result
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * ViewModel for the News Detail screen.
 * Handles loading and displaying individual article details.
 */
@HiltViewModel
class NewsDetailViewModel
    @Inject
    constructor(
        private val newsRepository: NewsRepository,
    ) : BaseViewModel() {
        private val _uiState = MutableStateFlow<UiState<Article>>(UiState.Idle)
        val uiState: StateFlow<UiState<Article>> = _uiState.asStateFlow()

        /**
         * Loads the article with the given ID.
         */
        fun loadArticle(articleId: String) {
            viewModelScope.launch {
                _uiState.value = UiState.Loading

                try {
                    newsRepository.getArticleById(articleId).collect { result ->
                        when (result) {
                            is com.example.nexusnews.util.Result.Loading -> {
                                // Keep loading state
                            }
                            is com.example.nexusnews.util.Result.Success -> {
                                _uiState.value = UiState.Success(result.data)
                            }
                            is com.example.nexusnews.util.Result.Error -> {
                                _uiState.value = UiState.Error(result.exception.localizedMessage ?: "Failed to load article")
                            }
                        }
                    }
                } catch (e: Exception) {
                    _uiState.value =
                        UiState.Error(
                            e.localizedMessage ?: "Failed to load article",
                        )
                }
            }
        }

        /**
         * Retries loading the article.
         */
        fun retryLoadArticle(articleId: String) {
            loadArticle(articleId)
        }
    }
