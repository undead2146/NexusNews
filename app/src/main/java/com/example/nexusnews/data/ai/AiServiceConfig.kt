package com.example.nexusnews.data.ai

import com.example.nexusnews.domain.ai.ContentType
import com.example.nexusnews.domain.ai.FreeAiModel

/**
 * Configuration for AI service operations.
 * Encapsulates common parameters used across AI features.
 */
data class AiServiceConfig(
    val apiKey: String,
    val primaryModel: FreeAiModel = FreeAiModel.getDefault(),
    val fallbackModels: List<FreeAiModel> = listOf(
        FreeAiModel.LLAMA_3_3_70B,
        FreeAiModel.GEMMA_2_27B,
        FreeAiModel.MISTRAL_SMALL,
        FreeAiModel.GEMMA_2_9B,
        FreeAiModel.LLAMA_3_1_8B,
    ),
) {
    companion object {
        /**
         * Creates a configuration with API key validation.
         *
         * @param apiKey The OpenRouter API key
         * @return Valid configuration or throws exception
         */
        fun create(apiKey: String?): AiServiceConfig =
            AiServiceConfig(
                apiKey = apiKey ?: throw IllegalStateException("OpenRouter API key not configured"),
            )
    }
}

/**
 * Configuration for individual AI requests.
 * Contains parameters specific to a single AI operation.
 */
data class AiRequestConfig(
    val maxTokens: Int,
    val temperature: Float,
    val systemPrompt: String? = null,
    val requestType: com.example.nexusnews.data.local.entity.AiRequestType,
) {
    companion object {
        /**
         * Creates configuration for key points extraction.
         */
        fun forKeyPoints(): AiRequestConfig =
            AiRequestConfig(
                maxTokens = 500,
                temperature = 0.3f,
                systemPrompt = "You are a news analysis expert. Extract key points from articles in valid JSON format.",
                requestType = com.example.nexusnews.data.local.entity.AiRequestType.KEY_POINTS_EXTRACTION,
            )

        /**
         * Creates configuration for entity recognition.
         */
        fun forEntityRecognition(): AiRequestConfig =
            AiRequestConfig(
                maxTokens = 800,
                temperature = 0.2f,
                systemPrompt = "You are an entity recognition expert. Identify entities in valid JSON format.",
                requestType = com.example.nexusnews.data.local.entity.AiRequestType.ENTITY_RECOGNITION,
            )

        /**
         * Creates configuration for topic classification.
         */
        fun forTopicClassification(): AiRequestConfig =
            AiRequestConfig(
                maxTokens = 400,
                temperature = 0.3f,
                systemPrompt = "You are a news classification expert. Classify topics in valid JSON format.",
                requestType = com.example.nexusnews.data.local.entity.AiRequestType.TOPIC_CLASSIFICATION,
            )

        /**
         * Creates configuration for bias detection.
         */
        fun forBiasDetection(): AiRequestConfig =
            AiRequestConfig(
                maxTokens = 600,
                temperature = 0.3f,
                systemPrompt = "You are a media literacy expert. Analyze bias in valid JSON format.",
                requestType = com.example.nexusnews.data.local.entity.AiRequestType.BIAS_DETECTION,
            )

        /**
         * Creates configuration for recommendations.
         */
        fun forRecommendations(): AiRequestConfig =
            AiRequestConfig(
                maxTokens = 1000,
                temperature = 0.4f,
                systemPrompt = "You are a recommendation system expert. Generate recommendations in valid JSON format.",
                requestType = com.example.nexusnews.data.local.entity.AiRequestType.RECOMMENDATION,
            )

        /**
         * Creates configuration for chat assistant.
         */
        fun forChatAssistant(): AiRequestConfig =
            AiRequestConfig(
                maxTokens = 500,
                temperature = 0.7f,
                systemPrompt = null, // Built separately
                requestType = com.example.nexusnews.data.local.entity.AiRequestType.CHAT_ASSISTANT,
            )

        /**
         * Creates configuration for content generation.
         */
        fun forContentGeneration(type: ContentType): AiRequestConfig =
            AiRequestConfig(
                maxTokens = 400,
                temperature = 0.8f,
                systemPrompt = "You are a creative content generator. Generate content in valid JSON format.",
                requestType = com.example.nexusnews.data.local.entity.AiRequestType.CONTENT_GENERATION,
            )
    }
}
