package com.example.nexusnews.data.ai.prompt

/**
 * Prompt builder for AI chat assistant.
 * Builds system prompts for context-aware conversations.
 */
class ChatPromptBuilder(
    private val articleContext: String?,
) : AiPromptBuilder {
    override fun build(): String =
        if (articleContext != null) {
            """
                You are a helpful AI assistant for a news app.
                You can answer questions about articles, explain news topics, and provide context.
                The user is asking about this article:
                ${articleContext.take(2000)}
                
                Be helpful, accurate, and concise. If you don't know something, say so.
                """.trimIndent()
        } else {
            """
                You are a helpful AI assistant for a news app.
                You can answer questions about news topics, explain concepts, and provide context.
                Be helpful, accurate, and concise. If you don't know something, say so.
                """.trimIndent()
        }
}

/**
 * Builds message list for chat API calls.
 * Handles conversation history and current user message.
 */
class ChatMessageBuilder(
    private val history: List<com.example.nexusnews.domain.ai.ChatMessage>,
    private val userMessage: String,
) {
    fun build(): List<com.example.nexusnews.data.remote.model.Message> =
        buildList {
            // Add conversation history (last 5 messages)
            history.takeLast(5).forEach { msg ->
                add(com.example.nexusnews.data.remote.model.Message(msg.role, msg.content))
            }
            // Add current user message
            add(com.example.nexusnews.data.remote.model.Message("user", userMessage))
        }
}
