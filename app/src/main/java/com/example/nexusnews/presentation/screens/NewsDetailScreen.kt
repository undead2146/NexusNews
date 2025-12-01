package com.example.nexusnews.presentation.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.example.nexusnews.R
import com.example.nexusnews.presentation.common.UiState
import com.example.nexusnews.ui.components.formatPublishedDate

/**
 * Screen displaying detailed view of a news article.
 */
@Composable
fun NewsDetailScreen(
    articleId: String,
    onBackClick: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: NewsDetailViewModel = hiltViewModel(),
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    Scaffold(modifier = modifier) { paddingValues ->
        when (uiState) {
            is UiState.Idle -> {
                // Trigger loading when screen opens
                viewModel.loadArticle(articleId)
                LoadingState()
            }

            is UiState.Loading -> {
                LoadingState()
            }

            is UiState.Success -> {
                val article = (uiState as UiState.Success).data
                ArticleDetailContent(
                    article = article,
                    modifier = Modifier.padding(paddingValues),
                )
            }

            is UiState.Error -> {
                val error = uiState as UiState.Error
                ErrorState(
                    message = error.message,
                    onRetry = { viewModel.loadArticle(articleId) },
                    onBack = onBackClick,
                    modifier = Modifier.padding(paddingValues),
                )
            }
        }
    }
}

/**
 * Displays the detailed article content.
 */
@Composable
private fun ArticleDetailContent(
    article: com.example.nexusnews.domain.model.Article,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier =
            modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.spacedBy(16.dp),
    ) {
        // Article image
        if (article.imageUrl != null) {
            AsyncImage(
                model =
                    ImageRequest
                        .Builder(LocalContext.current)
                        .data(article.imageUrl)
                        .crossfade(true)
                        .build(),
                contentDescription = null,
                contentScale = ContentScale.Crop,
                modifier =
                    Modifier
                        .fillMaxWidth()
                        .height(250.dp),
            )
        }

        // Article content
        Column(
            modifier = Modifier.padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            // Title
            Text(
                text = article.title,
                style = MaterialTheme.typography.headlineMedium,
                color = MaterialTheme.colorScheme.onSurface,
            )

            // Author and date
            Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                if (article.author != null) {
                    Text(
                        text = stringResource(R.string.by_author, article.author),
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                }

                Text(
                    text = formatPublishedDate(article.publishedAt),
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            }

            // Description
            if (article.description != null) {
                Text(
                    text = article.description,
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.onSurface,
                )
            }

            // Content
            if (article.content != null) {
                Text(
                    text = article.content,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurface,
                    lineHeight = MaterialTheme.typography.bodyMedium.lineHeight * 1.5,
                )
            }

            // Source
            Text(
                text = stringResource(R.string.source_colon, article.source),
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.padding(top = 8.dp),
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
                text = stringResource(R.string.loading_article),
                style = MaterialTheme.typography.bodyLarge,
            )
        }
    }
}

/**
 * Displays error state with retry and back options.
 */
@Composable
private fun ErrorState(
    message: String,
    onRetry: () -> Unit,
    onBack: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Box(
        modifier = modifier.fillMaxSize(),
        contentAlignment = Alignment.Center,
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp),
            modifier = Modifier.padding(32.dp),
        ) {
            Text(
                text = stringResource(R.string.error_loading_article),
                style = MaterialTheme.typography.headlineSmall,
                color = MaterialTheme.colorScheme.error,
                textAlign = TextAlign.Center,
            )

            Text(
                text = message,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                textAlign = TextAlign.Center,
            )

            Column(
                verticalArrangement = Arrangement.spacedBy(8.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                Button(onClick = onRetry) {
                    Text(text = stringResource(R.string.retry))
                }

                Button(onClick = onBack) {
                    Text(text = stringResource(R.string.go_back))
                }
            }
        }
    }
}
