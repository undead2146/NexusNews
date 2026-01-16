package com.example.nexusnews.presentation.screens

import androidx.lifecycle.viewModelScope
import com.example.nexusnews.data.local.dao.ArticleSummaryDao
import com.example.nexusnews.data.local.entity.ArticleSummaryEntity
import com.example.nexusnews.domain.ai.AiService
import com.example.nexusnews.domain.ai.FreeAiModel
import com.example.nexusnews.domain.model.Article
import com.example.nexusnews.domain.repository.NewsRepository
import com.example.nexusnews.presentation.common.BaseViewModel
import com.example.nexusnews.util.Result
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import timber.log.Timber
import java.time.LocalDateTime
import javax.inject.Inject

/**
 * UI State for News Detail screen.
 */
data class NewsDetailUiState(
    val isLoading: Boolean = false,
    val article: Article? = null,
    val error: String? = null,
)

/**
 * State for article summary generation.
 */
sealed class SummaryState {
    data object Idle : SummaryState()

    data object Loading : SummaryState()

    data class Success(
        val summary: ArticleSummaryEntity,
    ) : SummaryState()

    data class Error(
        val message: String,
    ) : SummaryState()
}

/**
 * ViewModel for the News Detail screen.
 * Handles loading and displaying individual article details.
 */
@HiltViewModel
class NewsDetailViewModel
    @Inject
    constructor(
        private val newsRepository: NewsRepository,
        private val aiService: AiService,
        private val articleSummaryDao: ArticleSummaryDao,
    ) : BaseViewModel<NewsDetailUiState>(NewsDetailUiState()) {
        private val _uiState = MutableStateFlow<NewsDetailUiState>(NewsDetailUiState())
        val uiState: StateFlow<NewsDetailUiState> = _uiState.asStateFlow()

        private val _summaryState = MutableStateFlow<SummaryState>(SummaryState.Idle)
        val summaryState: StateFlow<SummaryState> = _summaryState.asStateFlow()

        /**
         * Loads article with given ID.
         */
        fun loadArticle(articleId: String) {
            viewModelScope.launch {
                updateState { it.copy(isLoading = true, error = null) }

                try {
                    newsRepository.getArticleById(articleId).collect { result ->
                        when (result) {
                            is com.example.nexusnews.util.Result.Loading -> {
                                // Keep loading state
                            }
                            is com.example.nexusnews.util.Result.Success -> {
                                updateState { it.copy(isLoading = false, article = result.data) }
                                // Load cached summary if available
                                loadCachedSummary(articleId)
                            }
                            is com.example.nexusnews.util.Result.Error -> {
                                updateState { it.copy(isLoading = false, error = result.exception.localizedMessage ?: "Failed to load article") }
                            }
                        }
                    }
                } catch (e: Exception) {
                    updateState {
                        it.copy(
                            isLoading = false,
                            error = e.localizedMessage ?: "Failed to load article",
                        )
                    }
                }
            }
        }

        /**
         * Loads cached summary for the article.
         */
        private suspend fun loadCachedSummary(articleId: String) {
            val cachedSummary = articleSummaryDao.getSummaryByArticleIdSync(articleId)
            if (cachedSummary != null) {
                _summaryState.value = SummaryState.Success(cachedSummary)
            } else {
                _summaryState.value = SummaryState.Idle
            }
        }

        /**
         * Generates an AI summary for the current article.
         */
        fun generateSummary(article: Article) {
            viewModelScope.launch {
                _summaryState.value = SummaryState.Loading

                try {
                    val content = article.content ?: article.description ?: ""
                    if (content.isBlank()) {
                        _summaryState.value = SummaryState.Error("No content to summarize")
                        return@launch
                    }

                    val result = aiService.summarizeArticle(content, maxLength = 150)
                    result.onSuccess { summaryText ->
                        val model = FreeAiModel.getDefault()

                        // Create and cache the summary
                        val summaryEntity =
                            ArticleSummaryEntity(
                                id = ArticleSummaryEntity.generateId(article.id, model.id),
                                articleId = article.id,
                                summary = summaryText,
                                modelUsed = model.displayName,
                                promptTokens = 0, // TODO: Track actual tokens
                                completionTokens = 0,
                                totalTokens = 0,
                                generatedAt = LocalDateTime.now(),
                            )

                        articleSummaryDao.insertSummary(summaryEntity)
                        _summaryState.value = SummaryState.Success(summaryEntity)

                        Timber.d("Summary generated successfully for article: ${article.id}")
                    }.onFailure { exception ->
                        _summaryState.value = SummaryState.Error(exception.localizedMessage ?: "Failed to generate summary")
                        Timber.e(exception, "Failed to generate summary")
                    }
                } catch (e: Exception) {
                    _summaryState.value = SummaryState.Error(e.localizedMessage ?: "Failed to generate summary")
                    Timber.e(e, "Failed to generate summary")
                }
            }
        }

        /**
         * Retries generating the summary.
         */
        fun retryGenerateSummary(article: Article) {
            generateSummary(article)
        }

        /**
         * Retries loading the article.
         */
        fun retryLoadArticle(articleId: String) {
            loadArticle(articleId)
        }
    }
