package com.example.nexusnews.presentation.screens

import androidx.lifecycle.viewModelScope
import com.example.nexusnews.data.local.dao.ArticleSummaryDao
import com.example.nexusnews.data.local.entity.ArticleSummaryEntity
import com.example.nexusnews.data.scraper.ArticleScraperService
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
    val isLoadingFullContent: Boolean = false,
    val isLoadingSentiment: Boolean = false,
    val isLoadingKeyPoints: Boolean = false,
    val isLoadingEntities: Boolean = false,
    val isLoadingTopics: Boolean = false,
    val isLoadingBias: Boolean = false,
    val sentiment: com.example.nexusnews.domain.ai.Sentiment? = null,
    val keyPoints: com.example.nexusnews.domain.ai.KeyPointsResult? = null,
    val entities: com.example.nexusnews.domain.ai.EntityRecognitionResult? = null,
    val topics: com.example.nexusnews.domain.ai.TopicClassificationResult? = null,
    val bias: com.example.nexusnews.domain.ai.BiasDetectionResult? = null,
    val isAnalyzing: Boolean = false,
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
        private val summarizeArticleUseCase: com.example.nexusnews.domain.usecase.ai.SummarizeArticleUseCase,
        private val analyzeSentimentUseCase: com.example.nexusnews.domain.usecase.ai.AnalyzeSentimentUseCase,
        private val extractKeyPointsUseCase: com.example.nexusnews.domain.usecase.ai.ExtractKeyPointsUseCase,
        private val recognizeEntitiesUseCase: com.example.nexusnews.domain.usecase.ai.RecognizeEntitiesUseCase,
        private val classifyTopicUseCase: com.example.nexusnews.domain.usecase.ai.ClassifyTopicUseCase,
        private val detectBiasUseCase: com.example.nexusnews.domain.usecase.ai.DetectBiasUseCase,
        private val articleSummaryDao: ArticleSummaryDao,
        private val articleScraperService: ArticleScraperService,
    ) : BaseViewModel<NewsDetailUiState>(NewsDetailUiState()) {
        // uiState is now provided by BaseViewModel
        // Alias state to uiState for compatibility with View
        val uiState: StateFlow<NewsDetailUiState> = state

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
                                val article = result.data
                                updateState { it.copy(isLoading = false, article = article) }
                                // Load cached summary if available
                                loadCachedSummary(articleId)

                                // Fetch full content if article content is truncated
                                if (article.content == null || (article.content.contains("[+") && article.content.contains("chars]"))) {
                                    fetchFullArticleContent(article)
                                }
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
         * Fetches full article content from the web.
         */
        private fun fetchFullArticleContent(article: Article) {
            viewModelScope.launch {
                updateState { it.copy(isLoadingFullContent = true) }

                val result = articleScraperService.fetchFullContent(article.url)
                result.onSuccess { fullContent ->
                    val updatedArticle = article.copy(content = fullContent)
                    updateState { it.copy(article = updatedArticle, isLoadingFullContent = false) }
                    Timber.d("Full content loaded: ${fullContent.length} characters")
                }.onFailure { exception ->
                    Timber.w(exception, "Failed to fetch full content, keeping preview")
                    updateState { it.copy(isLoadingFullContent = false) }
                }
            }
        }

        /**
         * Ensures full article content is available for AI analysis.
         * Returns the content to use, fetching full content if needed.
         */
        private suspend fun ensureFullContentForAI(article: Article): String {
            val currentContent = article.content ?: article.description ?: ""

            // Check if content is truncated or empty
            val isTruncated = currentContent.contains("[+") && currentContent.contains("chars]")
            val isEmpty = currentContent.isBlank()

            if (isEmpty || isTruncated) {
                Timber.d("Content is empty or truncated, fetching full content for AI analysis")
                updateState { it.copy(isLoadingFullContent = true) }

                val result = articleScraperService.fetchFullContent(article.url)
                result.onSuccess { fullContent ->
                    val updatedArticle = article.copy(content = fullContent)
                    updateState { it.copy(article = updatedArticle, isLoadingFullContent = false) }
                    Timber.d("Full content loaded for AI: ${fullContent.length} characters")
                }.onFailure { exception ->
                    Timber.w(exception, "Failed to fetch full content for AI, using available content")
                    updateState { it.copy(isLoadingFullContent = false) }
                }

                // Wait for the fetch to complete and return the updated content
                // We need to wait for the state to update
                kotlinx.coroutines.delay(500) // Brief delay to allow state update

                // Return the updated content from state
                val updatedArticle = state.value.article
                return updatedArticle?.content ?: currentContent
            }

            return currentContent
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
                    val content = ensureFullContentForAI(article)
                    if (content.isBlank()) {
                        _summaryState.value = SummaryState.Error("No content to summarize")
                        return@launch
                    }

                    summarizeArticleUseCase(
                        com.example.nexusnews.domain.usecase.ai.SummarizeArticleUseCase.Params(
                            articleContent = content,
                            maxLength = 150
                        )
                    ).collect { result ->
                        when(result) {
                            is com.example.nexusnews.util.Result.Success -> {
                                val summaryText = result.data
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
                            }
                            is com.example.nexusnews.util.Result.Error -> {
                                _summaryState.value = SummaryState.Error(result.exception.localizedMessage ?: "Failed to generate summary")
                                Timber.e(result.exception, "Failed to generate summary")
                            }
                            is com.example.nexusnews.util.Result.Loading -> {
                                // Already handled by initial state
                            }
                        }
                    }
                } catch (e: Exception) {
                    _summaryState.value = SummaryState.Error(e.localizedMessage ?: "Failed to generate summary")
                    Timber.e(e, "Failed to generate summary")
                }
            }
        }

        fun analyzeSentiment(article: Article) {
            viewModelScope.launch {
                val content = ensureFullContentForAI(article)
                if (content.isBlank()) return@launch

                updateState { it.copy(isLoadingSentiment = true) }

                analyzeSentimentUseCase(com.example.nexusnews.domain.usecase.ai.AnalyzeSentimentUseCase.Params(content))
                    .collect { result ->
                        if (result is com.example.nexusnews.util.Result.Success) {
                            updateState { it.copy(sentiment = result.data, isLoadingSentiment = false) }
                        } else if (result is com.example.nexusnews.util.Result.Error) {
                            updateState { it.copy(isLoadingSentiment = false) }
                        }
                    }
            }
        }

        fun extractKeyPoints(article: Article) {
            viewModelScope.launch {
                val content = ensureFullContentForAI(article)
                if (content.isBlank()) return@launch

                updateState { it.copy(isLoadingKeyPoints = true) }

                extractKeyPointsUseCase(com.example.nexusnews.domain.usecase.ai.ExtractKeyPointsUseCase.Params(content))
                    .collect { result ->
                        if (result is com.example.nexusnews.util.Result.Success) {
                            updateState { it.copy(keyPoints = result.data, isLoadingKeyPoints = false) }
                        } else if (result is com.example.nexusnews.util.Result.Error) {
                            updateState { it.copy(isLoadingKeyPoints = false) }
                        }
                    }
            }
        }

        fun recognizeEntities(article: Article) {
            viewModelScope.launch {
                val content = ensureFullContentForAI(article)
                if (content.isBlank()) return@launch

                updateState { it.copy(isLoadingEntities = true) }

                recognizeEntitiesUseCase(com.example.nexusnews.domain.usecase.ai.RecognizeEntitiesUseCase.Params(content))
                    .collect { result ->
                        if (result is com.example.nexusnews.util.Result.Success) {
                            updateState { it.copy(entities = result.data, isLoadingEntities = false) }
                        } else if (result is com.example.nexusnews.util.Result.Error) {
                            updateState { it.copy(isLoadingEntities = false) }
                        }
                    }
            }
        }

        fun classifyTopic(article: Article) {
            viewModelScope.launch {
                val content = ensureFullContentForAI(article)
                if (content.isBlank()) return@launch

                updateState { it.copy(isLoadingTopics = true) }

                classifyTopicUseCase(com.example.nexusnews.domain.usecase.ai.ClassifyTopicUseCase.Params(content, article.title))
                    .collect { result ->
                        if (result is com.example.nexusnews.util.Result.Success) {
                            updateState { it.copy(topics = result.data, isLoadingTopics = false) }
                        } else if (result is com.example.nexusnews.util.Result.Error) {
                            updateState { it.copy(isLoadingTopics = false) }
                        }
                    }
            }
        }

        fun detectBias(article: Article) {
            viewModelScope.launch {
                val content = ensureFullContentForAI(article)
                if (content.isBlank()) return@launch

                updateState { it.copy(isLoadingBias = true) }

                detectBiasUseCase(com.example.nexusnews.domain.usecase.ai.DetectBiasUseCase.Params(content, article.title))
                    .collect { result ->
                        if (result is com.example.nexusnews.util.Result.Success) {
                            updateState { it.copy(bias = result.data, isLoadingBias = false) }
                        } else if (result is com.example.nexusnews.util.Result.Error) {
                            updateState { it.copy(isLoadingBias = false) }
                        }
                    }
            }
        }

        fun analyzeArticle(article: Article) {
            viewModelScope.launch {
                updateState { it.copy(isAnalyzing = true) }

                val content = ensureFullContentForAI(article)

                if (content.isBlank()) {
                    updateState { it.copy(isAnalyzing = false) }
                    return@launch
                }

                // Launch parallel analysis
                launch {
                    analyzeSentimentUseCase(com.example.nexusnews.domain.usecase.ai.AnalyzeSentimentUseCase.Params(content))
                        .collect { result ->
                            if (result is com.example.nexusnews.util.Result.Success) {
                                updateState { it.copy(sentiment = result.data) }
                            }
                        }
                }

                launch {
                    extractKeyPointsUseCase(com.example.nexusnews.domain.usecase.ai.ExtractKeyPointsUseCase.Params(content))
                        .collect { result ->
                            if (result is com.example.nexusnews.util.Result.Success) {
                                updateState { it.copy(keyPoints = result.data) }
                            }
                        }
                }

                launch {
                    recognizeEntitiesUseCase(com.example.nexusnews.domain.usecase.ai.RecognizeEntitiesUseCase.Params(content))
                        .collect { result ->
                            if (result is com.example.nexusnews.util.Result.Success) {
                                updateState { it.copy(entities = result.data) }
                            }
                        }
                }

                launch {
                    classifyTopicUseCase(com.example.nexusnews.domain.usecase.ai.ClassifyTopicUseCase.Params(content, article.title))
                        .collect { result ->
                            if (result is com.example.nexusnews.util.Result.Success) {
                                updateState { it.copy(topics = result.data) }
                            }
                        }
                }

                launch {
                    detectBiasUseCase(com.example.nexusnews.domain.usecase.ai.DetectBiasUseCase.Params(content, article.title))
                        .collect { result ->
                            if (result is com.example.nexusnews.util.Result.Success) {
                                updateState { it.copy(bias = result.data) }
                            }
                        }
                }
                updateState { it.copy(isAnalyzing = false) }
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
