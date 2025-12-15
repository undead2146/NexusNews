package com.example.nexusnews.ui.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.outlined.FavoriteBorder
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.example.nexusnews.domain.model.Article
import java.time.format.DateTimeFormatter
import java.util.Locale

/**
 * Composable for displaying a news article item in a list.
 *
 * @param article The article to display
 * @param onClick Callback when article is clicked
 * @param isBookmarked Whether the article is bookmarked
 * @param onBookmarkClick Callback when bookmark icon is clicked
 * @param modifier Modifier for customization
 */
@Composable
fun ArticleItem(
    article: Article,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    isBookmarked: Boolean = false,
    onBookmarkClick: (() -> Unit)? = null,
) {
    Card(
        modifier =
            modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 8.dp)
                .clickable(
                    onClick = onClick,
                    onClickLabel = "Read article",
                ).semantics(mergeDescendants = true) {
                    contentDescription =
                        buildString {
                            append("Article: ${article.title}. ")
                            article.description?.let { append("$it. ") }
                            append(
                                com.example.nexusnews.ui.accessibility.AccessibilityUtils.formatArticleMetadata(
                                    article.source,
                                    formatPublishedDate(article.publishedAt),
                                ),
                            )
                            append(". ")
                            append(
                                com.example.nexusnews.ui.accessibility.AccessibilityUtils.formatBookmarkStatus(
                                    isBookmarked,
                                ),
                            )
                        }

                    // Add custom accessibility actions
                    if (onBookmarkClick != null) {
                        com.example.nexusnews.ui.accessibility.AccessibilityUtils.run {
                            addCustomActions(
                                (if (isBookmarked) "Remove bookmark" else "Add bookmark") to {
                                    onBookmarkClick()
                                    true
                                },
                            )
                        }
                    }
                },
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.Top,
        ) {
            // Article image
            article.imageUrl?.let { imageUrl ->
                AsyncImage(
                    model =
                        ImageRequest
                            .Builder(LocalContext.current)
                            .data(imageUrl)
                            .crossfade(true)
                            .build(),
                    contentDescription = null,
                    contentScale = ContentScale.Crop,
                    modifier =
                        Modifier
                            .size(80.dp)
                            .clip(MaterialTheme.shapes.medium),
                )
                Spacer(modifier = Modifier.width(16.dp))
            }

            // Article content
            Column(
                modifier = Modifier.weight(1f),
            ) {
                // Title
                Text(
                    text = article.title,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis,
                )

                Spacer(modifier = Modifier.height(8.dp))

                // Description
                article.description?.let { description ->
                    Text(
                        text = description,
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        maxLines = 3,
                        overflow = TextOverflow.Ellipsis,
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                }

                // Source and date
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Text(
                        text = article.source,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.primary,
                        fontWeight = FontWeight.Medium,
                    )

                    Text(
                        text = " • ",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                    )

                    Text(
                        text = formatPublishedDate(article.publishedAt),
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                }

                // Tags
                if (article.tags.isNotEmpty()) {
                    Spacer(modifier = Modifier.height(8.dp))
                    TagRow(
                        tags = article.tags,
                        modifier = Modifier.fillMaxWidth(),
                    )
                }
            }

            // Bookmark icon button
            if (onBookmarkClick != null) {
                IconButton(
                    onClick = onBookmarkClick,
                    modifier = Modifier.align(Alignment.Top),
                ) {
                    androidx.compose.animation.AnimatedVisibility(
                        visible = isBookmarked,
                        enter = com.example.nexusnews.ui.animations.NexusAnimations.bookmarkToggle,
                        exit = com.example.nexusnews.ui.animations.NexusAnimations.bookmarkExit,
                    ) {
                        Icon(
                            imageVector = Icons.Filled.Favorite,
                            contentDescription = "Remove bookmark",
                            tint = MaterialTheme.colorScheme.primary,
                        )
                    }
                    androidx.compose.animation.AnimatedVisibility(
                        visible = !isBookmarked,
                        enter = com.example.nexusnews.ui.animations.NexusAnimations.fadeIn,
                        exit = com.example.nexusnews.ui.animations.NexusAnimations.fadeOut,
                    ) {
                        Icon(
                            imageVector = Icons.Outlined.FavoriteBorder,
                            contentDescription = "Add bookmark",
                            tint = MaterialTheme.colorScheme.onSurfaceVariant,
                        )
                    }
                }
            }
        }
    }
}

/**
 * Format the published date for display.
 */
internal fun formatPublishedDate(publishedAt: java.time.LocalDateTime): String {
    val formatter = DateTimeFormatter.ofPattern("MMM dd, yyyy", Locale.getDefault())
    return publishedAt.format(formatter)
}
