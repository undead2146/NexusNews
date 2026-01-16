package com.example.nexusnews.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Error
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Error
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.nexusnews.domain.ai.*
import com.example.nexusnews.presentation.common.UiState

/**
 * UI Component for displaying key points extracted from an article.
 */
@Composable
fun KeyPointsSection(
    keyPointsResult: KeyPointsResult?,
    isLoading: Boolean = false,
    error: String? = null,
    modifier: Modifier = Modifier,
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                text = "Key Points",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
            )

            when {
                isLoading -> {
                    Spacer(modifier = Modifier.height(16.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.Center,
                    ) {
                        CircularProgressIndicator()
                    }
                }

                error != null -> {
                    Spacer(modifier = Modifier.height(16.dp))
                    ErrorCard(error)
                }

                keyPointsResult != null -> {
                    Spacer(modifier = Modifier.height(16.dp))

                    // Summary
                    if (keyPointsResult.summary.isNotEmpty()) {
                        Text(
                            text = keyPointsResult.summary,
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                        )
                        Spacer(modifier = Modifier.height(12.dp))
                    }

                    // Key Points List
                    keyPointsResult.keyPoints.forEach { keyPoint ->
                        KeyPointItem(keyPoint)
                        Spacer(modifier = Modifier.height(8.dp))
                    }
                }

                else -> {
                    Spacer(modifier = Modifier.height(16.dp))
                    Text(
                        text = "No key points available",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f),
                    )
                }
            }
        }
    }
}

@Composable
private fun KeyPointItem(keyPoint: KeyPoint) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        // Importance indicator
        Icon(
            imageVector = Icons.Filled.CheckCircle,
            contentDescription = null,
            tint = when {
                keyPoint.importance > 0.7f -> Color(0xFF4CAF50) // Green
                keyPoint.importance > 0.4f -> Color(0xFFFFC107) // Amber
                else -> Color(0xFFFF9800) // Orange
            },
        )

        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = keyPoint.text,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
            Text(
                text = "Importance: ${(keyPoint.importance * 100).toInt()}% • Position: ${keyPoint.position}%",
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f),
            )
        }
    }
}

/**
 * UI Component for displaying recognized entities.
 */
@Composable
fun EntityRecognitionSection(
    entityResult: EntityRecognitionResult?,
    isLoading: Boolean = false,
    error: String? = null,
    modifier: Modifier = Modifier,
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                text = "Recognized Entities",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
            )

            when {
                isLoading -> {
                    Spacer(modifier = Modifier.height(16.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.Center,
                    ) {
                        CircularProgressIndicator()
                    }
                }

                error != null -> {
                    Spacer(modifier = Modifier.height(16.dp))
                    ErrorCard(error)
                }

                entityResult != null && entityResult.entities.isNotEmpty() -> {
                    Spacer(modifier = Modifier.height(16.dp))

                    // Group entities by type
                    val groupedEntities = entityResult.entities.groupBy { it.type }
                    groupedEntities.forEach { (type, entities) ->
                        EntityGroup(type, entities)
                        Spacer(modifier = Modifier.height(12.dp))
                    }
                }

                else -> {
                    Spacer(modifier = Modifier.height(16.dp))
                    Text(
                        text = "No entities found",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f),
                    )
                }
            }
        }
    }
}

@Composable
private fun EntityGroup(
    type: EntityType,
    entities: List<RecognizedEntity>,
) {
    Column {
        Text(
            text = type.name,
            style = MaterialTheme.typography.labelLarge,
            color = MaterialTheme.colorScheme.primary,
            fontWeight = FontWeight.Bold,
        )
        Spacer(modifier = Modifier.height(4.dp))
        entities.forEach { entity ->
            EntityItem(entity)
            Spacer(modifier = Modifier.height(4.dp))
        }
    }
}

@Composable
private fun EntityItem(entity: RecognizedEntity) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        Text(
            text = entity.text,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.weight(1f),
        )
        Text(
            text = "${(entity.confidence * 100).toInt()}%",
            style = MaterialTheme.typography.labelSmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f),
        )
    }
}

/**
 * UI Component for displaying topic classification.
 */
@Composable
fun TopicClassificationSection(
    topicResult: TopicClassificationResult?,
    isLoading: Boolean = false,
    error: String? = null,
    modifier: Modifier = Modifier,
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                text = "Topic Classification",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
            )

            when {
                isLoading -> {
                    Spacer(modifier = Modifier.height(16.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.Center,
                    ) {
                        CircularProgressIndicator()
                    }
                }

                error != null -> {
                    Spacer(modifier = Modifier.height(16.dp))
                    ErrorCard(error)
                }

                topicResult != null -> {
                    Spacer(modifier = Modifier.height(16.dp))

                    // Primary Topic
                    PrimaryTopicCard(topicResult.primaryTopic)

                    Spacer(modifier = Modifier.height(12.dp))

                    // Secondary Topics
                    if (topicResult.secondaryTopics.isNotEmpty()) {
                        Text(
                            text = "Related Topics",
                            style = MaterialTheme.typography.labelLarge,
                            color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.8f),
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        topicResult.secondaryTopics.forEach { topic ->
                            SecondaryTopicItem(topic)
                            Spacer(modifier = Modifier.height(4.dp))
                        }
                    }

                    // All Topics as tags
                    if (topicResult.allTopics.isNotEmpty()) {
                        Spacer(modifier = Modifier.height(12.dp))
                        TopicTags(topicResult.allTopics)
                    }
                }

                else -> {
                    Spacer(modifier = Modifier.height(16.dp))
                    Text(
                        text = "No topic classification available",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f),
                    )
                }
            }
        }
    }
}

@Composable
private fun PrimaryTopicCard(topic: TopicClassification) {
    Card(
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer),
        shape = RoundedCornerShape(8.dp),
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            Text(
                text = topic.topic,
                style = MaterialTheme.typography.titleLarge,
                color = MaterialTheme.colorScheme.onPrimaryContainer,
                fontWeight = FontWeight.Bold,
            )
            Text(
                text = "Confidence: ${(topic.confidence * 100).toInt()}%",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.8f),
            )
            if (topic.subtopics.isNotEmpty()) {
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "Subtopics: ${topic.subtopics.joinToString(", ")}",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.7f),
                )
            }
        }
    }
}

@Composable
private fun SecondaryTopicItem(topic: TopicClassification) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
    ) {
        Text(
            text = topic.topic,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.weight(1f),
        )
        Text(
            text = "${(topic.confidence * 100).toInt()}%",
            style = MaterialTheme.typography.labelSmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f),
        )
    }
}

@Composable
private fun TopicTags(topics: List<String>) {
    Row(
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        modifier = Modifier.fillMaxWidth(),
    ) {
        topics.forEach { topic ->
            TopicChip(topic)
        }
    }
}

@Composable
fun TopicChip(topic: String) {
    Surface(
        color = MaterialTheme.colorScheme.secondaryContainer,
        shape = RoundedCornerShape(16.dp),
    ) {
        Text(
            text = topic,
            style = MaterialTheme.typography.labelMedium,
            color = MaterialTheme.colorScheme.onSecondaryContainer,
            modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
        )
    }
}

/**
 * UI Component for displaying bias detection results.
 */
@Composable
fun BiasDetectionSection(
    biasResult: BiasDetectionResult?,
    isLoading: Boolean = false,
    error: String? = null,
    modifier: Modifier = Modifier,
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                text = "Bias Analysis",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
            )

            when {
                isLoading -> {
                    Spacer(modifier = Modifier.height(16.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.Center,
                    ) {
                        CircularProgressIndicator()
                    }
                }

                error != null -> {
                    Spacer(modifier = Modifier.height(16.dp))
                    ErrorCard(error)
                }

                biasResult != null -> {
                    Spacer(modifier = Modifier.height(16.dp))

                    // Bias Level Card
                    BiasLevelCard(biasResult.biasAnalysis, biasResult.objectivityScore)

                    Spacer(modifier = Modifier.height(12.dp))

                    // Explanation
                    if (biasResult.biasAnalysis.explanation.isNotEmpty()) {
                        Text(
                            text = biasResult.biasAnalysis.explanation,
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                        )
                        Spacer(modifier = Modifier.height(12.dp))
                    }

                    // Examples
                    if (biasResult.biasAnalysis.examples.isNotEmpty()) {
                        Text(
                            text = "Examples:",
                            style = MaterialTheme.typography.labelLarge,
                            color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.8f),
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        biasResult.biasAnalysis.examples.forEach { example ->
                            Text(
                                text = "• $example",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f),
                            )
                        }
                    }

                    // Credibility Indicators
                    if (biasResult.credibilityIndicators.isNotEmpty()) {
                        Spacer(modifier = Modifier.height(12.dp))
                        Text(
                            text = "Credibility Indicators:",
                            style = MaterialTheme.typography.labelLarge,
                            color = Color(0xFF4CAF50),
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        biasResult.credibilityIndicators.forEach { indicator ->
                            Row(
                                horizontalArrangement = Arrangement.spacedBy(8.dp),
                                verticalAlignment = Alignment.CenterVertically,
                            ) {
                                Icon(
                                    imageVector = Icons.Filled.CheckCircle,
                                    contentDescription = null,
                                    tint = Color(0xFF4CAF50),
                                    modifier = Modifier.width(16.dp),
                                )
                                Text(
                                    text = indicator,
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                                )
                            }
                        }
                    }
                }

                else -> {
                    Spacer(modifier = Modifier.height(16.dp))
                    Text(
                        text = "No bias analysis available",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f),
                    )
                }
            }
        }
    }
}

@Composable
private fun BiasLevelCard(biasAnalysis: BiasAnalysis, objectivityScore: Float) {
    val (iconColor, bgColor) =
        when (biasAnalysis.level) {
            BiasLevel.LOW -> Color(0xFF4CAF50) to MaterialTheme.colorScheme.surface
            BiasLevel.MEDIUM -> Color(0xFFFFC107) to MaterialTheme.colorScheme.surface
            BiasLevel.HIGH -> Color(0xFFF44336) to MaterialTheme.colorScheme.surface
            BiasLevel.UNKNOWN -> Color(0xFF9E9E9E) to MaterialTheme.colorScheme.surface
        }

    Card(
        colors = CardDefaults.cardColors(containerColor = bgColor),
        shape = RoundedCornerShape(8.dp),
    ) {
        Row(
            modifier = Modifier.padding(12.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Icon(
                imageVector =
                    when (biasAnalysis.level) {
                        BiasLevel.LOW -> Icons.Filled.CheckCircle
                        BiasLevel.MEDIUM -> Icons.Filled.Warning
                        BiasLevel.HIGH -> Icons.Filled.Error
                        BiasLevel.UNKNOWN -> Icons.Filled.Info
                    },
                contentDescription = null,
                tint = iconColor,
            )

            Column {
                Text(
                    text = "Bias Level: ${biasAnalysis.level.name}",
                    style = MaterialTheme.typography.titleMedium,
                    color = iconColor,
                    fontWeight = FontWeight.Bold,
                )
                if (biasAnalysis.biasType != null) {
                    Text(
                        text = "Type: ${biasAnalysis.biasType}",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                }
                Text(
                    text = "Objectivity Score: ${(objectivityScore * 100).toInt()}%",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f),
                )
            }
        }
    }
}

/**
 * Generic error card component.
 */
@Composable
private fun ErrorCard(message: String) {
    Card(
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.errorContainer),
        shape = RoundedCornerShape(8.dp),
    ) {
        Row(
            modifier = Modifier.padding(12.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Icon(
                imageVector = Icons.Filled.Error,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.error,
            )
            Text(
                text = message,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onErrorContainer,
            )
        }
    }
}
