package com.example.nexusnews.data.remote.api

import com.example.nexusnews.data.remote.model.ChatCompletionRequest
import com.example.nexusnews.data.remote.model.ChatCompletionResponse
import retrofit2.http.Body
import retrofit2.http.Header
import retrofit2.http.POST

/**
 * Retrofit interface for OpenRouter API.
 * OpenRouter provides unified access to multiple AI models through a single API.
 *
 * Base URL: https://openrouter.ai/api/v1/
 */
interface OpenRouterApi {
    /**
     * Chat completion endpoint.
     * Compatible with OpenAI Chat API format.
     *
     * @param authorization Bearer token with OpenRouter API key
     * @param referer HTTP referer for tracking (optional but recommended)
     * @param appTitle Application title for tracking (optional but recommended)
     * @param request Chat completion request with model and messages
     * @return Chat completion response with generated text and usage stats
     */
    @POST("chat/completions")
    suspend fun chatCompletion(
        @Header("Authorization") authorization: String,
        @Header("HTTP-Referer") referer: String = "https://nexusnews.app",
        @Header("X-Title") appTitle: String = "NexusNews",
        @Body request: ChatCompletionRequest,
    ): ChatCompletionResponse
}
