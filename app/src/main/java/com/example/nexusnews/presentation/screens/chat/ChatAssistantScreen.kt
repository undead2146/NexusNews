package com.example.nexusnews.presentation.screens.chat

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Send
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SmallFloatingActionButton
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.example.nexusnews.domain.ai.ChatMessage
import com.example.nexusnews.domain.ai.ChatResponse
import com.example.nexusnews.presentation.common.BaseViewModel
import kotlinx.coroutines.launch

/**
 * Screen for AI Chat Assistant.
 * Allows users to have conversations about news articles and topics.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ChatAssistantScreen(
    viewModel: ChatAssistantViewModel,
    onNavigateBack: () -> Unit,
) {
    val uiState by viewModel.uiState.collectAsState()
    val coroutineScope = rememberCoroutineScope()
    val listState = rememberLazyListState()

    // Auto-scroll to bottom when new message arrives
    LaunchedEffect(uiState.messages.size) {
        if (uiState.messages.isNotEmpty()) {
            listState.animateScrollToItem(uiState.messages.size - 1)
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("AI Assistant") },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.Filled.ArrowBack, contentDescription = "Back")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface,
                ),
            )
        },
    ) { paddingValues ->
        Column(
            modifier =
                Modifier
                    .fillMaxSize()
                    .padding(paddingValues),
        ) {
            // Chat Messages
            Box(modifier = Modifier.weight(1f)) {
                when {
                    uiState.isLoading && uiState.messages.isEmpty() -> {
                        Column(
                            modifier = Modifier.fillMaxSize(),
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.Center,
                        ) {
                            CircularProgressIndicator()
                            Spacer(modifier = Modifier.height(16.dp))
                            Text(
                                text = "Loading conversation...",
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
                        }
                    }

                    else -> {
                        LazyColumn(
                            state = listState,
                            modifier = Modifier.fillMaxSize(),
                            verticalArrangement = Arrangement.spacedBy(8.dp),
                        ) {
                            items(uiState.messages) { message ->
                                ChatMessageItem(message, isUser = message.role == "user")
                            }
                        }
                    }
                }
            }

            // Suggested Questions
            if (uiState.suggestedQuestions.isNotEmpty()) {
                Column(
                    modifier =
                        Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                ) {
                    Text(
                        text = "Suggested Questions",
                        style = MaterialTheme.typography.labelLarge,
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f),
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    uiState.suggestedQuestions.forEach { question ->
                        SuggestedQuestionChip(
                            question = question,
                            onClick = {
                                viewModel.sendMessage(question)
                            },
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                    }
                }
            }

            // Input Field
            Row(
                modifier =
                    Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                verticalAlignment = Alignment.Bottom,
            ) {
                OutlinedTextField(
                    value = uiState.inputText,
                    onValueChange = { viewModel.onInputTextChanged(it) },
                    placeholder = { Text("Ask about this article...") },
                    modifier =
                        Modifier
                            .weight(1f)
                            .padding(end = 8.dp),
                    enabled = !uiState.isSending,
                    singleLine = true,
                )

                SmallFloatingActionButton(
                    onClick = {
                        if (uiState.inputText.isNotBlank()) {
                            viewModel.sendMessage(uiState.inputText)
                        }
                    },
                    containerColor = MaterialTheme.colorScheme.primary,
                ) {
                    if (uiState.isSending) {
                        CircularProgressIndicator(
                            modifier = Modifier.padding(8.dp),
                            color = MaterialTheme.colorScheme.onPrimary,
                        )
                    } else {
                        Icon(
                            Icons.Filled.Send,
                            contentDescription = "Send",
                            tint = MaterialTheme.colorScheme.onPrimary,
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun ChatMessageItem(message: ChatMessage, isUser: Boolean) {
    Row(
        modifier =
            Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp),
        horizontalArrangement = if (isUser) Arrangement.End else Arrangement.Start,
    ) {
        Card(
            colors =
                CardDefaults.cardColors(
                    containerColor =
                        if (isUser) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.surfaceVariant,
                ),
            shape = RoundedCornerShape(16.dp),
        ) {
            Column(modifier = Modifier.padding(12.dp)) {
                Text(
                    text = if (isUser) "You" else "AI Assistant",
                    style = MaterialTheme.typography.labelSmall,
                    color =
                        if (isUser) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f),
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = message.content,
                    style = MaterialTheme.typography.bodyMedium,
                    color =
                        if (isUser) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onSurfaceVariant,
                )
            }
        }
    }
}

@Composable
private fun SuggestedQuestionChip(
    question: String,
    onClick: () -> Unit,
) {
    Card(
        onClick = onClick,
        colors =
            CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.secondaryContainer,
            ),
        shape = RoundedCornerShape(20.dp),
    ) {
        Text(
            text = question,
            style = MaterialTheme.typography.labelMedium,
            color = MaterialTheme.colorScheme.onSecondaryContainer,
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
        )
    }
}

/**
 * UI State for Chat Assistant.
 */
data class ChatAssistantUiState(
    val messages: List<ChatMessage> = emptyList(),
    val inputText: String = "",
    val isSending: Boolean = false,
    val isLoading: Boolean = false,
    val suggestedQuestions: List<String> = emptyList(),
    val error: String? = null,
)

/**
 * ViewModel for Chat Assistant.
 */
class ChatAssistantViewModel(
    private val aiService: com.example.nexusnews.domain.ai.AiService,
) : BaseViewModel<ChatAssistantUiState>(ChatAssistantUiState()) {
    fun onInputTextChanged(text: String) {
        updateState { it.copy(inputText = text) }
    }

    fun sendMessage(message: String) {
        if (message.isBlank()) return

        updateState { it.copy(isSending = true, error = null) }

        viewModelScope.launch {
            val userMessage =
                ChatMessage(
                    id = "msg_${System.currentTimeMillis()}_user",
                    role = "user",
                    content = message,
                    timestamp = System.currentTimeMillis(),
                    articleContext = null,
                )

            // Add user message immediately
            updateState { it.copy(messages = it.messages + userMessage) }

            val result =
                aiService.chatWithAssistant(
                    conversationHistory = currentState.messages,
                    userMessage = message,
                    articleContext = null,
                )

            result.onSuccess { response ->
                updateState {
                    it.copy(
                        messages = it.messages + response.message,
                        suggestedQuestions = response.suggestedQuestions,
                        isSending = false,
                    )
                }
            }

            result.onFailure { error ->
                updateState {
                    it.copy(
                        error = error.message ?: "Failed to send message",
                        isSending = false,
                    )
                }
            }
        }
    }

    fun clearError() {
        updateState { it.copy(error = null) }
    }
}
