package com.example.nexusnews.data.remote.model

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

/**
 * Request model for OpenRouter chat completion API.
 */
@JsonClass(generateAdapter = true)
data class ChatCompletionRequest(
    val model: String,
    val messages: List<Message>,
    @Json(name = "max_tokens")
    val maxTokens: Int? = null,
    val temperature: Double? = null,
)

/**
 * Message in a chat completion request/response.
 */
@JsonClass(generateAdapter = true)
data class Message(
    val role: String, // "system", "user", "assistant"
    val content: String,
)

/**
 * Response from OpenRouter chat completion API.
 */
@JsonClass(generateAdapter = true)
data class ChatCompletionResponse(
    val id: String,
    val choices: List<Choice>,
    val usage: Usage,
    val model: String,
)

/**
 * Choice in a chat completion response.
 */
@JsonClass(generateAdapter = true)
data class Choice(
    val message: Message,
    @Json(name = "finish_reason")
    val finishReason: String,
)

/**
 * Token usage information.
 */
@JsonClass(generateAdapter = true)
data class Usage(
    @Json(name = "prompt_tokens")
    val promptTokens: Int,
    @Json(name = "completion_tokens")
    val completionTokens: Int,
    @Json(name = "total_tokens")
    val totalTokens: Int,
)
