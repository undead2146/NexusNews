package com.example.nexusnews.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.AssistChip
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

/**
 * A chip component for displaying article tags.
 *
 * @param tag The tag text to display
 * @param onClick Optional click handler. If null, chip is not clickable.
 * @param modifier Modifier for customization
 */
@Composable
fun TagChip(
    tag: String,
    onClick: (() -> Unit)? = null,
    modifier: Modifier = Modifier,
) {
    AssistChip(
        onClick = onClick ?: {},
        label = { Text(tag) },
        modifier = modifier,
        enabled = onClick != null,
    )
}

/**
 * A horizontal row of tag chips.
 *
 * @param tags List of tags to display
 * @param onTagClick Optional click handler for tags
 * @param modifier Modifier for customization
 */
@Composable
fun TagRow(
    tags: List<String>,
    onTagClick: ((String) -> Unit)? = null,
    modifier: Modifier = Modifier,
) {
    if (tags.isEmpty()) return

    LazyRow(
        modifier = modifier,
        horizontalArrangement = Arrangement.spacedBy(4.dp),
        contentPadding = PaddingValues(horizontal = 16.dp, vertical = 4.dp),
    ) {
        items(tags) { tag ->
            TagChip(
                tag = tag,
                onClick = onTagClick?.let { { it(tag) } },
            )
        }
    }
}
