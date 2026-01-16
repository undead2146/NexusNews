package com.example.nexusnews.presentation.screens.bookmarks

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.outlined.FavoriteBorder
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.nexusnews.domain.model.Article
import com.example.nexusnews.presentation.common.UiState
import com.example.nexusnews.ui.components.ArticleItem

/**
 * Bookmarks screen displaying saved articles.
 *
 * @param onArticleClick Callback when an article is clicked
 * @param viewModel ViewModel for managing bookmarks
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BookmarksScreen(
    onArticleClick: (String) -> Unit,
    viewModel: BookmarksViewModel = hiltViewModel(),
) {
    val uiState by viewModel.bookmarks.collectAsState()
    val showFavoritesOnly by viewModel.showFavoritesOnly.collectAsState()

    Column(
        modifier = Modifier.fillMaxSize(),
    ) {
        Text(
            text = "Bookmarks",
            style = MaterialTheme.typography.titleMedium,
            fontWeight = androidx.compose.ui.text.font.FontWeight.Bold,
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 12.dp)
        )
        // Favorites filter chip
        FilterChip(
            selected = showFavoritesOnly,
            onClick = { viewModel.toggleFavoritesFilter() },
            label = { Text(if (showFavoritesOnly) "Favorites Only" else "All Bookmarks") },
            leadingIcon = {
                Icon(
                    imageVector =
                        if (showFavoritesOnly) {
                            Icons.Filled.Favorite
                        } else {
                            Icons.Outlined.FavoriteBorder
                        },
                    contentDescription = null,
                )
            },
            modifier =
                Modifier
                    .padding(horizontal = 16.dp, vertical = 8.dp),
        )

        // Content
        when (uiState) {
            is UiState.Loading -> {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center,
                ) {
                    CircularProgressIndicator()
                }
            }

            is UiState.Success -> {
                val articles = (uiState as UiState.Success<List<Article>>).data
                if (articles.isEmpty()) {
                    // Empty state
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center,
                    ) {
                        Text(
                            text =
                                if (showFavoritesOnly) {
                                    "No favorite articles yet.\nMark articles as favorites to see them here."
                                } else {
                                    "No bookmarks yet.\nSave articles to read them later."
                                },
                            style = MaterialTheme.typography.bodyLarge,
                            textAlign = TextAlign.Center,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.padding(32.dp),
                        )
                    }
                } else {
                    // Bookmarks list
                    LazyColumn(
                        contentPadding = PaddingValues(vertical = 8.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp),
                    ) {
                        items(articles, key = { it.id }) { article ->
                            ArticleItem(
                                article = article,
                                isBookmarked = true,
                                isFavorite = article.isFavorite,
                                onClick = { onArticleClick(article.id) },
                                onBookmarkClick = { viewModel.removeBookmark(article.id) },
                                onFavoriteClick = { viewModel.toggleFavorite(article.id) },
                            )
                        }
                    }
                }
            }

            is UiState.Error -> {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center,
                ) {
                    Text(
                        text = "Error loading bookmarks",
                        style = MaterialTheme.typography.bodyLarge,
                        color = MaterialTheme.colorScheme.error,
                    )
                }
            }

            is UiState.Idle -> {
                // Do nothing
            }
        }
    }
}
