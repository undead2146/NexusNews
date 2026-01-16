package com.example.nexusnews.presentation.screens.recommendations

import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Bookmark
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SmallFloatingActionButton
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.example.nexusnews.domain.ai.ArticleRecommendation
import com.example.nexusnews.domain.ai.RecommendationResult
import com.example.nexusnews.domain.ai.UserInterest

/**
 * Screen for displaying personalized article recommendations.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RecommendationsScreen(
    viewModel: RecommendationsViewModel,
    onNavigateBack: () -> Unit,
    onArticleClick: (String) -> Unit,
) {
    val uiState by viewModel.state.collectAsState()
    val listState = rememberLazyListState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("For You") },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.Filled.ArrowBack, contentDescription = "Back")
                    }
                },
                actions = {
                    IconButton(onClick = { viewModel.refreshRecommendations() }) {
                        Icon(Icons.Filled.Refresh, contentDescription = "Refresh")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface,
                ),
            )
        },
        floatingActionButton = {
            if (uiState.hasMoreRecommendations) {
                SmallFloatingActionButton(
                    onClick = { viewModel.loadMoreRecommendations() },
                    containerColor = MaterialTheme.colorScheme.primary,
                ) {
                    Text(
                        text = "Load More",
                        style = MaterialTheme.typography.labelMedium,
                        color = MaterialTheme.colorScheme.onPrimary,
                        modifier = Modifier.padding(horizontal = 12.dp),
                    )
                }
            }
        },
    ) { paddingValues ->
        Column(
            modifier =
                Modifier
                    .fillMaxSize()
                    .padding(paddingValues),
        ) {
            when {
                uiState.isLoading && uiState.recommendations.isEmpty() -> {
                    Column(
                        modifier = Modifier.fillMaxSize(),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center,
                    ) {
                        CircularProgressIndicator()
                        Spacer(modifier = Modifier.height(16.dp))
                        Text(
                            text = "Finding articles for you...",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f),
                        )
                    }
                }

                uiState.error != null -> {
                    Column(
                        modifier = Modifier.fillMaxSize(),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center,
                    ) {
                        Text(
                            text = uiState.error ?: "An error occurred",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.error,
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        IconButton(onClick = { viewModel.refreshRecommendations() }) {
                            Icon(Icons.Filled.Refresh, contentDescription = "Retry")
                        }
                    }
                }

                uiState.recommendations.isEmpty() -> {
                    Column(
                        modifier = Modifier.fillMaxSize(),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center,
                    ) {
                        Text(
                            text = "No recommendations available",
                            style = MaterialTheme.typography.bodyLarge,
                            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f),
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = "Read more articles to get personalized recommendations",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f),
                        )
                    }
                }

                else -> {
                    // User Interests Section
                    if (uiState.userInterests.isNotEmpty()) {
                        UserInterestsSection(uiState.userInterests)
                    }

                    // Recommendations List
                    LazyColumn(
                        state = listState,
                        modifier = Modifier.fillMaxSize(),
                        verticalArrangement = Arrangement.spacedBy(12.dp),
                    ) {
                        items(uiState.recommendations) { recommendation ->
                            RecommendationCard(
                                recommendation = recommendation,
                                onClick = { onArticleClick(recommendation.articleId) },
                                onBookmark = {
                                    viewModel.bookmarkArticle(recommendation.articleId)
                                },
                                isBookmarked = uiState.bookmarkedArticles.contains(recommendation.articleId),
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun UserInterestsSection(interests: List<UserInterest>) {
    Column(modifier = Modifier.padding(16.dp)) {
        Text(
            text = "Your Interests",
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold,
        )
        Spacer(modifier = Modifier.height(12.dp))
        Row(
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            modifier = Modifier.fillMaxWidth(),
        ) {
            interests.take(5).forEach { interest ->
                InterestChip(interest)
            }
        }
    }
}

@Composable
private fun InterestChip(interest: UserInterest) {
    Card(
        colors =
            CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.secondaryContainer,
            ),
        shape = RoundedCornerShape(16.dp),
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Icon(
                Icons.Filled.Star,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.onSecondaryContainer,
                modifier = Modifier.size(16.dp),
            )
            Column {
                Text(
                    text = interest.topic,
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.onSecondaryContainer,
                    fontWeight = FontWeight.Medium,
                )
                Text(
                    text = "${(interest.score * 100).toInt()}% match",
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSecondaryContainer.copy(alpha = 0.7f),
                )
            }
        }
    }
}

@Composable
private fun RecommendationCard(
    recommendation: ArticleRecommendation,
    onClick: () -> Unit,
    onBookmark: () -> Unit,
    isBookmarked: Boolean,
) {
    Card(
        onClick = onClick,
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
        shape = RoundedCornerShape(12.dp),
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            // Header with score
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                // Match Score Indicator
                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Text(
                        text = "${(recommendation.score * 100).toInt()}% match",
                        style = MaterialTheme.typography.labelLarge,
                        color = MaterialTheme.colorScheme.primary,
                        fontWeight = FontWeight.Bold,
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    LinearProgressIndicator(
                        progress = { recommendation.score },
                        modifier = Modifier.width(80.dp),
                        color = MaterialTheme.colorScheme.primary,
                    )
                }

                // Bookmark Button
                IconButton(onClick = onBookmark) {
                    Icon(
                        imageVector = Icons.Filled.Bookmark,
                        contentDescription = if (isBookmarked) "Remove bookmark" else "Add bookmark",
                        tint =
                            if (isBookmarked) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f),
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Reason
            Text(
                text = recommendation.reason,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )

            Spacer(modifier = Modifier.height(8.dp))

            // Matched Interests
            if (recommendation.matchedInterests.isNotEmpty()) {
                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                ) {
                    Text(
                        text = "Because you're interested in:",
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f),
                    )
                    recommendation.matchedInterests.forEach { interest ->
                        InterestBadge(interest)
                    }
                }
            }
        }
    }
}

@Composable
private fun InterestBadge(interest: String) {
    Card(
        colors =
            CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.tertiaryContainer,
            ),
        shape = RoundedCornerShape(12.dp),
    ) {
        Text(
            text = interest,
            style = MaterialTheme.typography.labelSmall,
            color = MaterialTheme.colorScheme.onTertiaryContainer,
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
        )
    }
}

/**
 * UI State for Recommendations Screen.
 */
data class RecommendationsUiState(
    val recommendations: List<ArticleRecommendation> = emptyList(),
    val userInterests: List<UserInterest> = emptyList(),
    val bookmarkedArticles: Set<String> = emptySet(),
    val isLoading: Boolean = false,
    val error: String? = null,
    val hasMoreRecommendations: Boolean = false,
)

/**
 * ViewModel for Recommendations Screen.
 */
class RecommendationsViewModel @javax.inject.Inject constructor(
    private val generateRecommendationsUseCase: com.example.nexusnews.domain.usecase.ai.GenerateRecommendationsUseCase,
    private val newsRepository: com.example.nexusnews.domain.repository.NewsRepository,
) : com.example.nexusnews.presentation.common.BaseViewModel<RecommendationsUiState>(RecommendationsUiState()) {
    fun refreshRecommendations() {
        loadRecommendations()
    }

    fun loadMoreRecommendations() {
        // Implementation would load more recommendations
        // For now, just refresh
        loadRecommendations()
    }

    fun bookmarkArticle(articleId: String) {
        viewModelScope.launch {
            try {
                newsRepository.getArticleById(articleId).collect { result ->
                     if (result is com.example.nexusnews.util.Result.Success<*>) {
                        val article = result.data as com.example.nexusnews.domain.model.Article
                        if (currentState.bookmarkedArticles.contains(articleId)) {
                            newsRepository.removeBookmark(articleId)
                            updateState {
                                it.copy(bookmarkedArticles = it.bookmarkedArticles - articleId)
                            }
                        } else {
                            newsRepository.addBookmark(article)
                            updateState {
                                it.copy(bookmarkedArticles = it.bookmarkedArticles + articleId)
                            }
                        }
                    }
                }
            } catch (e: Exception) {
                // Handle error
            }
        }
    }

    private fun loadRecommendations() {
        updateState { it.copy(isLoading = true, error = null) }

        viewModelScope.launch {
            try {
                // Get user interests (simulated for now)
                val userInterests =
                    listOf(
                        UserInterest("Technology", 0.9f, System.currentTimeMillis()),
                        UserInterest("Science", 0.7f, System.currentTimeMillis()),
                        UserInterest("Business", 0.6f, System.currentTimeMillis()),
                    )

                // Get available articles
                newsRepository.getArticles().collect { result ->
                    if (result is com.example.nexusnews.util.Result.Success<*>) {
                        @Suppress("UNCHECKED_CAST")
                        val articles = result.data as List<com.example.nexusnews.domain.model.Article>
                        val availableArticles =
                            articles.associate { article ->
                                article.id to "${article.title}\n${article.description}"
                            }

                        // Generate recommendations
                        generateRecommendationsUseCase(
                            com.example.nexusnews.domain.usecase.ai.GenerateRecommendationsUseCase.Params(
                                userInterests = userInterests,
                                availableArticles = availableArticles,
                                limit = 10
                            )
                        ).collect { result ->
                            when(result) {
                                is com.example.nexusnews.util.Result.Success -> {
                                    val data = result.data
                                    updateState {
                                        it.copy(
                                            recommendations = data.recommendations,
                                            userInterests = data.userProfile,
                                            isLoading = false,
                                            hasMoreRecommendations = data.recommendations.size >= 10,
                                        )
                                    }
                                }
                                is com.example.nexusnews.util.Result.Error -> {
                                    updateState {
                                        it.copy(
                                            error = result.exception.message ?: "Failed to load recommendations",
                                            isLoading = false,
                                        )
                                    }
                                }
                                is com.example.nexusnews.util.Result.Loading -> {
                                    // Already handled
                                }
                            }
                        }
                    } else if (result is com.example.nexusnews.util.Result.Error) {
                         updateState {
                            it.copy(
                                error = result.exception.message ?: "Failed to load articles",
                                isLoading = false,
                            )
                        }
                    }
                }
            } catch (e: Exception) {
                updateState {
                    it.copy(
                        error = e.message ?: "An error occurred",
                        isLoading = false,
                    )
                }
            }
        }
    }
}
