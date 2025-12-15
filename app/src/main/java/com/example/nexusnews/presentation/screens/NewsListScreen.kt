package com.example.nexusnews.presentation.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Info
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.nexusnews.R
import com.example.nexusnews.presentation.common.UiState
import com.example.nexusnews.ui.components.ArticleItem
import com.example.nexusnews.ui.components.SwipeableArticleItem

/**
 * Screen displaying a list of news articles.
 */
@OptIn(androidx.compose.material3.ExperimentalMaterial3Api::class)
@Composable
fun NewsListScreen(
    onArticleClick: (String) -> Unit,
    modifier: Modifier = Modifier,
    viewModel: NewsListViewModel = hiltViewModel(),
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val isRefreshing by viewModel.isRefreshing.collectAsStateWithLifecycle()
    val selectedCategory by viewModel.selectedCategory.collectAsStateWithLifecycle()

    Scaffold(modifier = modifier) { paddingValues ->
        Column(modifier = Modifier.padding(paddingValues)) {
            // Category selector
            CategorySelector(
                selectedCategory = selectedCategory,
                onCategorySelected = { viewModel.selectCategory(it) },
            )

            // News content with pull-to-refresh
            PullToRefreshBox(
                isRefreshing = isRefreshing,
                onRefresh = { viewModel.refreshNews() },
            ) {
                when (uiState) {
                    is UiState.Idle -> {
                        // Initial state - could show welcome message
                        IdleState()
                    }

                    is UiState.Loading -> {
                        LoadingState()
                    }

                    is UiState.Success -> {
                        val articles = (uiState as UiState.Success).data
                        if (articles.isEmpty()) {
                            EmptyState()
                        } else {
                            ArticleList(
                                articles = articles,
                                onArticleClick = onArticleClick,
                            )
                        }
                    }

                    is UiState.Error -> {
                        val error = uiState as UiState.Error
                        ErrorState(
                            message = error.message,
                            onRetry = { viewModel.retryLoadNews() },
                        )
                    }
                }
            }
        }
    }
}

/**
 * Displays the article list with swipeable items.
 */
@Composable
private fun ArticleList(
    articles: List<com.example.nexusnews.domain.model.Article>,
    onArticleClick: (String) -> Unit,
    viewModel: NewsListViewModel = hiltViewModel(),
) {
    LazyColumn(
        contentPadding = PaddingValues(vertical = 8.dp),
        verticalArrangement = Arrangement.spacedBy(4.dp),
    ) {
        items(
            items = articles,
            key = { it.id },
        ) { article ->
            val isBookmarked by viewModel.isBookmarked(article.id).collectAsStateWithLifecycle(initialValue = false)

            SwipeableArticleItem(
                article = article,
                isBookmarked = isBookmarked,
                isFavorite = false, // TODO: Track favorite state
                onBookmarkToggle = { viewModel.toggleBookmark(article) },
                onFavoriteToggle = { viewModel.toggleFavorite(article.id) },
                onClick = { onArticleClick(article.id) },
                modifier = Modifier.animateItem(),
            )
        }
    }
}

/**
 * Horizontal category selector with FilterChips.
 */
@Composable
private fun CategorySelector(
    selectedCategory: com.example.nexusnews.domain.model.NewsCategory?,
    onCategorySelected: (com.example.nexusnews.domain.model.NewsCategory?) -> Unit,
) {
    androidx.compose.foundation.lazy.LazyRow(
        contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        // "All" option
        item {
            androidx.compose.material3.FilterChip(
                selected = selectedCategory == null,
                onClick = { onCategorySelected(null) },
                label = { Text("All") },
                leadingIcon = {
                    if (selectedCategory == null) {
                        Icon(
                            imageVector = androidx.compose.material.icons.Icons.Filled.Check,
                            contentDescription = null,
                            modifier = Modifier.size(18.dp),
                        )
                    }
                },
            )
        }

        // Category options
        items(com.example.nexusnews.domain.model.NewsCategory.entries) { category ->
            androidx.compose.material3.FilterChip(
                selected = selectedCategory == category,
                onClick = { onCategorySelected(category) },
                label = { Text(category.displayName) },
                leadingIcon = {
                    Icon(
                        imageVector = category.icon,
                        contentDescription = null,
                        modifier = Modifier.size(18.dp),
                        tint =
                            if (selectedCategory == category) {
                                category.accentColor
                            } else {
                                MaterialTheme.colorScheme.onSurfaceVariant
                            },
                    )
                },
            )
        }
    }
}

/**
 * Displays loading state.
 */
@Composable
private fun LoadingState() {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center,
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp),
        ) {
            CircularProgressIndicator()
            Text(
                text = stringResource(R.string.loading_news),
                style = MaterialTheme.typography.bodyLarge,
            )
        }
    }
}

/**
 * Displays error state with retry option.
 */
@Composable
private fun ErrorState(
    message: String,
    onRetry: () -> Unit,
) {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center,
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp),
            modifier = Modifier.padding(32.dp),
        ) {
            Text(
                text = stringResource(R.string.error_fetching_news),
                style = MaterialTheme.typography.headlineSmall,
                color = MaterialTheme.colorScheme.error,
            )

            Text(
                text = message,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )

            Button(onClick = onRetry) {
                Text(text = stringResource(R.string.retry))
            }
        }
    }
}

/**
 * Displays empty state.
 */
@Composable
private fun EmptyState() {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center,
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
        ) {
            Icon(
                imageVector = Icons.Default.Info,
                contentDescription = null,
                modifier = Modifier.size(48.dp),
                tint = MaterialTheme.colorScheme.secondary,
            )
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                text = stringResource(R.string.no_articles_found),
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.onSurface,
            )
            Text(
                text = stringResource(R.string.check_connection_and_retry),
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
        }
    }
}

/**
 * Displays idle state.
 */
@Composable
private fun IdleState() {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center,
    ) {
        Text(
            text = stringResource(R.string.welcome_message),
            style = MaterialTheme.typography.headlineMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
        )
    }
}
