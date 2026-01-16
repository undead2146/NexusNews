package com.example.nexusnews.presentation.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.Face
import androidx.compose.material.icons.filled.FormatListBulleted
import androidx.compose.material.icons.filled.Label
import androidx.compose.material.icons.filled.People
import androidx.compose.material.icons.filled.Balance
import androidx.compose.material.icons.filled.Psychology
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.IconButton
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.example.nexusnews.R
import com.example.nexusnews.presentation.common.UiState
import com.example.nexusnews.ui.components.SummaryCard
import com.example.nexusnews.ui.components.KeyPointsSection
import com.example.nexusnews.ui.components.EntityRecognitionSection
import com.example.nexusnews.ui.components.TopicClassificationSection
import com.example.nexusnews.ui.components.BiasDetectionSection
import com.example.nexusnews.ui.components.SentimentAnalysisSection
import com.example.nexusnews.ui.components.formatPublishedDate
import com.example.nexusnews.domain.ai.KeyPointsResult
import com.example.nexusnews.domain.ai.EntityRecognitionResult
import com.example.nexusnews.domain.ai.TopicClassificationResult
import com.example.nexusnews.domain.ai.BiasDetectionResult
import com.example.nexusnews.domain.ai.Sentiment

/**
 * Screen displaying detailed view of a news article.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NewsDetailScreen(
    articleId: String,
    onBackClick: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: NewsDetailViewModel = hiltViewModel(),
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val summaryState by viewModel.summaryState.collectAsStateWithLifecycle()

    var showSummarySheet by remember { mutableStateOf(false) }
    var showMenu by remember { mutableStateOf(false) }

    // AI Analysis section visibility states
    var showSentiment by remember { mutableStateOf(true) }
    var showKeyPoints by remember { mutableStateOf(true) }
    var showEntities by remember { mutableStateOf(true) }
    var showTopics by remember { mutableStateOf(true) }
    var showBias by remember { mutableStateOf(true) }

    LaunchedEffect(summaryState) {
        if (summaryState is SummaryState.Success) {
            showSummarySheet = true
        }
    }

    Scaffold(
        modifier = modifier,
        contentWindowInsets = WindowInsets(0.dp),
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            // Content
            if (uiState.isLoading) {
                LoadingState()
            } else if (uiState.error != null) {
                ErrorState(
                    message = uiState.error!!,
                    onRetry = { viewModel.loadArticle(articleId) },
                    onBack = onBackClick,
                    modifier = Modifier.padding(top = 48.dp), // Add padding for overlay
                )
            } else if (uiState.article != null) {
                ArticleDetailContent(
                    article = uiState.article!!,
                    summaryState = summaryState,
                    showSummarySheet = showSummarySheet,
                    onDismissSummary = { showSummarySheet = false },
                    modifier = Modifier,
                    sentiment = uiState.sentiment,
                    isLoadingSentiment = uiState.isLoadingSentiment,
                    showSentiment = showSentiment,
                    onDismissSentiment = { showSentiment = false },
                    keyPoints = uiState.keyPoints,
                    isLoadingKeyPoints = uiState.isLoadingKeyPoints,
                    showKeyPoints = showKeyPoints,
                    onDismissKeyPoints = { showKeyPoints = false },
                    entities = uiState.entities,
                    isLoadingEntities = uiState.isLoadingEntities,
                    showEntities = showEntities,
                    onDismissEntities = { showEntities = false },
                    topics = uiState.topics,
                    isLoadingTopics = uiState.isLoadingTopics,
                    showTopics = showTopics,
                    onDismissTopics = { showTopics = false },
                    bias = uiState.bias,
                    isLoadingBias = uiState.isLoadingBias,
                    showBias = showBias,
                    onDismissBias = { showBias = false },
                    isAnalyzing = uiState.isAnalyzing,
                    onAnalyzeClick = { viewModel.analyzeArticle(uiState.article!!) },
                )
            } else {
                viewModel.loadArticle(articleId)
                LoadingState()
            }

            // Overlay Navigation Controls
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .statusBarsPadding()
                    .padding(8.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Back Button
                androidx.compose.material3.Surface(
                    shape = androidx.compose.foundation.shape.CircleShape,
                    color = Color.Black.copy(alpha = 0.4f),
                    contentColor = Color.White
                ) {
                    IconButton(onClick = onBackClick) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = stringResource(R.string.go_back)
                        )
                    }
                }

                // Menu Button
                 if (uiState.article != null) {
                    Box {
                        androidx.compose.material3.Surface(
                            shape = androidx.compose.foundation.shape.CircleShape,
                            color = Color.Black.copy(alpha = 0.4f),
                            contentColor = Color.White
                        ) {
                            IconButton(onClick = { showMenu = !showMenu }) {
                                Icon(
                                    imageVector = Icons.Filled.MoreVert,
                                    contentDescription = "More options"
                                )
                            }
                        }

                        // Dropdown Menu
                        DropdownMenu(
                            expanded = showMenu,
                            onDismissRequest = { showMenu = false }
                        ) {
                            // AI Summary
                            if (summaryState is SummaryState.Idle || summaryState is SummaryState.Error) {
                                DropdownMenuItem(
                                    text = {
                                        Column {
                                            Text(
                                                text = "AI Summary",
                                                style = MaterialTheme.typography.bodyMedium
                                            )
                                            Text(
                                                text = "Generate article summary",
                                                style = MaterialTheme.typography.bodySmall,
                                                color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f)
                                            )
                                        }
                                    },
                                    onClick = {
                                        showMenu = false
                                        if (uiState.article != null) {
                                            viewModel.generateSummary(uiState.article!!)
                                        }
                                    },
                                    leadingIcon = {
                                        Icon(Icons.Default.AutoAwesome, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
                                    }
                                )
                            }

                            // Divider
                            androidx.compose.material3.HorizontalDivider()

                            // Sentiment Analysis
                            DropdownMenuItem(
                                text = {
                                    Column {
                                        Text(
                                            text = "Sentiment Analysis",
                                            style = MaterialTheme.typography.bodyMedium
                                        )
                                        Text(
                                            text = "Analyze emotional tone",
                                            style = MaterialTheme.typography.bodySmall,
                                            color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f)
                                        )
                                    }
                                },
                                onClick = {
                                    showMenu = false
                                    if (uiState.article != null) {
                                        viewModel.analyzeSentiment(uiState.article!!)
                                    }
                                },
                                leadingIcon = {
                                    Icon(
                                        Icons.Default.Face,
                                        contentDescription = null,
                                        tint = Color(0xFF9C27B0)
                                    )
                                }
                            )

                            // Key Points
                            DropdownMenuItem(
                                text = {
                                    Column {
                                        Text(
                                            text = "Key Points",
                                            style = MaterialTheme.typography.bodyMedium
                                        )
                                        Text(
                                            text = "Extract main points",
                                            style = MaterialTheme.typography.bodySmall,
                                            color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f)
                                        )
                                    }
                                },
                                onClick = {
                                    showMenu = false
                                    if (uiState.article != null) {
                                        viewModel.extractKeyPoints(uiState.article!!)
                                    }
                                },
                                leadingIcon = {
                                    Icon(
                                        Icons.Default.FormatListBulleted,
                                        contentDescription = null,
                                        tint = Color(0xFF2196F3)
                                    )
                                }
                            )

                            // Entity Recognition
                            DropdownMenuItem(
                                text = {
                                    Column {
                                        Text(
                                            text = "Entities",
                                            style = MaterialTheme.typography.bodyMedium
                                        )
                                        Text(
                                            text = "People, places, organizations",
                                            style = MaterialTheme.typography.bodySmall,
                                            color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f)
                                        )
                                    }
                                },
                                onClick = {
                                    showMenu = false
                                    if (uiState.article != null) {
                                        viewModel.recognizeEntities(uiState.article!!)
                                    }
                                },
                                leadingIcon = {
                                    Icon(
                                        Icons.Default.People,
                                        contentDescription = null,
                                        tint = Color(0xFFFF9800)
                                    )
                                }
                            )

                            // Topic Classification
                            DropdownMenuItem(
                                text = {
                                    Column {
                                        Text(
                                            text = "Topics",
                                            style = MaterialTheme.typography.bodyMedium
                                        )
                                        Text(
                                            text = "Classify article topics",
                                            style = MaterialTheme.typography.bodySmall,
                                            color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f)
                                        )
                                    }
                                },
                                onClick = {
                                    showMenu = false
                                    if (uiState.article != null) {
                                        viewModel.classifyTopic(uiState.article!!)
                                    }
                                },
                                leadingIcon = {
                                    Icon(
                                        Icons.Default.Label,
                                        contentDescription = null,
                                        tint = Color(0xFF4CAF50)
                                    )
                                }
                            )

                            // Bias Detection
                            DropdownMenuItem(
                                text = {
                                    Column {
                                        Text(
                                            text = "Bias Analysis",
                                            style = MaterialTheme.typography.bodyMedium
                                        )
                                        Text(
                                            text = "Detect bias and objectivity",
                                            style = MaterialTheme.typography.bodySmall,
                                            color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f)
                                        )
                                    }
                                },
                                onClick = {
                                    showMenu = false
                                    if (uiState.article != null) {
                                        viewModel.detectBias(uiState.article!!)
                                    }
                                },
                                leadingIcon = {
                                    Icon(
                                        Icons.Default.Balance,
                                        contentDescription = null,
                                        tint = Color(0xFFF44336)
                                    )
                                }
                            )

                            // Divider
                            androidx.compose.material3.HorizontalDivider()

                            // Deep AI Analysis (All at once)
                            DropdownMenuItem(
                                text = {
                                    Column {
                                        Text(
                                            text = "Deep AI Analysis",
                                            style = MaterialTheme.typography.bodyMedium,
                                            fontWeight = androidx.compose.ui.text.font.FontWeight.Bold
                                        )
                                        Text(
                                            text = "Run all analyses at once",
                                            style = MaterialTheme.typography.bodySmall,
                                            color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f)
                                        )
                                    }
                                },
                                onClick = {
                                    showMenu = false
                                    if (uiState.article != null) {
                                        viewModel.analyzeArticle(uiState.article!!)
                                    }
                                },
                                leadingIcon = {
                                    Icon(
                                        Icons.Default.Psychology,
                                        contentDescription = null,
                                        tint = MaterialTheme.colorScheme.primary
                                    )
                                }
                            )
                        }
                    }
                }
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
    summaryState: SummaryState,
    showSummarySheet: Boolean,
    onDismissSummary: () -> Unit,
    modifier: Modifier = Modifier,
    sentiment: Sentiment? = null,
    isLoadingSentiment: Boolean = false,
    showSentiment: Boolean = true,
    onDismissSentiment: () -> Unit = {},
    keyPoints: KeyPointsResult? = null,
    isLoadingKeyPoints: Boolean = false,
    showKeyPoints: Boolean = true,
    onDismissKeyPoints: () -> Unit = {},
    entities: EntityRecognitionResult? = null,
    isLoadingEntities: Boolean = false,
    showEntities: Boolean = true,
    onDismissEntities: () -> Unit = {},
    topics: TopicClassificationResult? = null,
    isLoadingTopics: Boolean = false,
    showTopics: Boolean = true,
    onDismissTopics: () -> Unit = {},
    bias: BiasDetectionResult? = null,
    isLoadingBias: Boolean = false,
    showBias: Boolean = true,
    onDismissBias: () -> Unit = {},
    isAnalyzing: Boolean = false,
    onAnalyzeClick: () -> Unit = {},
) {
    val context = LocalContext.current
    Column(
        modifier =
            modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.spacedBy(16.dp),
    ) {
        // Show summary sheet
        if (showSummarySheet && summaryState is SummaryState.Success) {
            SummaryCard(
                summary = summaryState.summary,
                onDismiss = onDismissSummary,
            )

            Spacer(modifier = Modifier.height(8.dp))
        }

        // AI Analysis Section
        Column(
            modifier = Modifier.padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),
        ) {
            if (isAnalyzing) {
                    Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    CircularProgressIndicator(modifier = Modifier.size(24.dp))
                    Spacer(modifier = Modifier.width(12.dp))
                    Text("Analyzing article...", style = MaterialTheme.typography.bodyMedium)
                }
            }

            if (showSentiment && (sentiment != null || isLoadingSentiment)) {
                SentimentAnalysisSection(
                    sentiment = sentiment,
                    isLoading = isLoadingSentiment,
                    onDismiss = onDismissSentiment
                )
            }

            if (showKeyPoints && (keyPoints != null || isLoadingKeyPoints)) {
                KeyPointsSection(
                    keyPointsResult = keyPoints,
                    isLoading = isLoadingKeyPoints,
                    onDismiss = onDismissKeyPoints
                )
            }

            if (showTopics && (topics != null || isLoadingTopics)) {
                TopicClassificationSection(
                    topicResult = topics,
                    isLoading = isLoadingTopics,
                    onDismiss = onDismissTopics
                )
            }

            if (showEntities && (entities != null || isLoadingEntities)) {
                EntityRecognitionSection(
                    entityResult = entities,
                    isLoading = isLoadingEntities,
                    onDismiss = onDismissEntities
                )
            }

            if (showBias && (bias != null || isLoadingBias)) {
                BiasDetectionSection(
                    biasResult = bias,
                    isLoading = isLoadingBias,
                    onDismiss = onDismissBias
                )
            }
        }

        // Show loading indicator
        if (summaryState is SummaryState.Loading) {
            LoadingSummaryCard()
        }

        // Show error
        if (summaryState is SummaryState.Error) {
            ErrorSummaryCard(
                message = summaryState.message,
                onRetry = {}, // Handled by FAB
            )
        }

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

            Spacer(modifier = Modifier.height(16.dp))
        }
    }
}

/**
 * Loading state for summary generation.
 */
@Composable
private fun LoadingSummaryCard() {
    Box(
        modifier = Modifier.fillMaxWidth(),
        contentAlignment = Alignment.Center,
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(8.dp),
            modifier = Modifier.padding(24.dp),
        ) {
            CircularProgressIndicator()
            Text(
                text = "Generating summary...",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
        }
    }
}

/**
 * Error state for summary generation.
 */
@Composable
private fun ErrorSummaryCard(
    message: String,
    onRetry: () -> Unit,
) {
    androidx.compose.material3.Card(
        modifier = Modifier.fillMaxWidth(),
        colors =
            androidx.compose.material3.CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.errorContainer,
            ),
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            Text(
                text = "Summary Failed",
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.onErrorContainer,
            )
            Text(
                text = message,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onErrorContainer,
            )
            Button(
                onClick = onRetry,
                modifier = Modifier.fillMaxWidth(),
            ) {
                Text(text = "Retry")
            }
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
