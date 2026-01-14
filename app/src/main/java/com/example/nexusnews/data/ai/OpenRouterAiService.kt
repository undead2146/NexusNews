package com.example.nexusnews.data.ai

import com.example.nexusnews.data.local.dao.AiUsageDao
import com.example.nexusnews.data.local.datastore.ApiKeyDataStore
import com.example.nexusnews.data.remote.api.OpenRouterApi
import com.example.nexusnews.data.remote.model.ChatCompletionRequest
import com.example.nexusnews.data.remote.model.Message
import com.example.nexusnews.domain.ai.AiService
import com.example.nexusnews.domain.ai.FreeAiModel
import com.example.nexusnews.domain.ai.Sentiment
import timber.log.Timber
import java.time.LocalDateTime
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Implementation of AiService using OpenRouter API.
 * Implements model fallback logic for improved reliability.
 */
@Singleton
class OpenRouterAiService
    @Inject
    constructor(
        private val openRouterApi: OpenRouterApi,
        private val apiKeyDataStore: ApiKeyDataStore,
        private val aiUsageDao: AiUsageDao,
    ) : AiService {
        /**
         * Fallback models to try in order if primary model fails.
         */
        private val fallbackModels =
            listOf(
                FreeAiModel.LLAMA_3_3_70B,
                FreeAiModel.GEMMA_2_27B,
                FreeAiModel.MISTRAL_SMALL,
                FreeAiModel.GEMMA_2_9B,
                FreeAiModel.LLAMA_3_1_8B,
            )

        override suspend fun summarizeArticle(
            articleContent: String,
            maxLength: Int,
        ): Result<String> =
            run {
                val apiKey = getApiKeyOrThrow()
                val primaryModel = FreeAiModel.getDefault()

                val prompt = buildSummarizationPrompt(articleContent, maxLength)

                // Try primary model first, then fallback models
                val result =
                    tryModelWithFallback(
                        apiKey = apiKey,
                        model = primaryModel,
                        prompt = prompt,
                        maxTokens = maxLength + 50,
                        temperature = 0.3f,
                        requestType = com.example.nexusnews.data.local.entity.AiRequestType.SUMMARIZATION,
                    )

                result
            }

        override suspend fun analyzeSentiment(articleContent: String): Result<Sentiment> =
            run {
                val apiKey = getApiKeyOrThrow()
                val primaryModel = FreeAiModel.getDefault()

                val prompt = buildSentimentPrompt(articleContent)

                // Try primary model first, then fallback models
                val textResult =
                    tryModelWithFallback(
                        apiKey = apiKey,
                        model = primaryModel,
                        prompt = prompt,
                        maxTokens = 10,
                        temperature = 0.1f,
                        requestType = com.example.nexusnews.data.local.entity.AiRequestType.SENTIMENT_ANALYSIS,
                        systemPrompt = "You are a sentiment analysis assistant. Respond with only: POSITIVE, NEUTRAL, or NEGATIVE.",
                    )

                textResult.map { sentimentText ->
                    val sentiment =
                        when (sentimentText.trim().uppercase()) {
                            "POSITIVE" -> Sentiment.POSITIVE
                            "NEGATIVE" -> Sentiment.NEGATIVE
                            else -> Sentiment.NEUTRAL
                        }
                    sentiment
                }
            }

        override suspend fun translateArticle(
            articleContent: String,
            targetLanguage: String,
        ): Result<String> =
            run {
                val apiKey = getApiKeyOrThrow()
                val primaryModel = FreeAiModel.getDefault()

                val prompt = buildTranslationPrompt(articleContent, targetLanguage)

                // Try primary model first, then fallback models
                tryModelWithFallback(
                    apiKey = apiKey,
                    model = primaryModel,
                    prompt = prompt,
                    maxTokens = articleContent.length + 500,
                    temperature = 0.3f,
                    requestType = com.example.nexusnews.data.local.entity.AiRequestType.TRANSLATION,
                    systemPrompt = "You are a professional translator. Translate accurately while preserving meaning.",
                )
            }

        private fun getApiKeyOrThrow(): String =
            apiKeyDataStore.getOpenRouterApiKey()
                ?: throw IllegalStateException("OpenRouter API key not configured")

        private fun buildSummarizationPrompt(
            content: String,
            maxLength: Int,
        ): String =
            """
            Summarize the following news article in approximately $maxLength characters.
            Focus on the key facts and main points. Be concise and objective.
            Do not include phrases like "This article discusses" or "The article is about".
            Just provide the summary directly.

            Article:
            ${content.take(4000)}
            """.trimIndent()

        private fun buildSentimentPrompt(content: String): String =
            """
            Analyze the sentiment of this news article.
            Respond with only one word: POSITIVE, NEUTRAL, or NEGATIVE.

            Article:
            ${content.take(2000)}
            """.trimIndent()

        private fun buildTranslationPrompt(
            content: String,
            targetLanguage: String,
        ): String =
            """
            Translate the following news article to $targetLanguage.
            Preserve the meaning and tone. Provide only the translation.

            Article:
            ${content.take(3000)}
            """.trimIndent()

        /**
         * Tracks AI API usage in the database.
         */
        private suspend fun trackUsage(
            requestType: com.example.nexusnews.data.local.entity.AiRequestType,
            modelUsed: String,
            promptTokens: Int,
            completionTokens: Int,
            totalTokens: Int,
        ) {
            try {
                val usageEntity =
                    com.example.nexusnews.data.local.entity.AiUsageEntity(
                        id = com.example.nexusnews.data.local.entity.AiUsageEntity.generateId(),
                        requestType = requestType,
                        modelUsed = modelUsed,
                        promptTokens = promptTokens,
                        completionTokens = completionTokens,
                        totalTokens = totalTokens,
                        requestCount = 1,
                        timestamp = LocalDateTime.now(),
                    )
                aiUsageDao.insertUsage(usageEntity)
                Timber.d("AI usage tracked: $requestType, $totalTokens tokens")
            } catch (e: Exception) {
                Timber.e(e, "Failed to track AI usage")
            }
        }

        /**
         * Tries to make an AI request with fallback models.
         * Attempts primary model first, then tries fallback models in order.
         *
         * @param apiKey The OpenRouter API key
         * @param model The primary model to try
         * @param prompt The user prompt
         * @param maxTokens Maximum tokens for the response
         * @param temperature Temperature for generation
         * @param requestType Type of AI request for tracking
         * @param systemPrompt Optional system prompt
         * @return Result with the AI response or error
         */
        private suspend fun tryModelWithFallback(
            apiKey: String,
            model: FreeAiModel,
            prompt: String,
            maxTokens: Int,
            temperature: Float,
            requestType: com.example.nexusnews.data.local.entity.AiRequestType,
            systemPrompt: String? = null,
        ): Result<String> {
            // Build messages list
            val messages =
                buildList {
                    if (systemPrompt != null) {
                        add(Message("system", systemPrompt))
                    }
                    add(Message("user", prompt))
                }

            // Try primary model first
            var lastException: Exception? = null
            val modelsToTry = listOf(model) + fallbackModels.filter { it != model }

            for (currentModel in modelsToTry) {
                try {
                    Timber.d("Trying model: ${currentModel.displayName} (${currentModel.id})")

                    val response =
                        openRouterApi.chatCompletion(
                            authorization = "Bearer $apiKey",
                            request =
                                ChatCompletionRequest(
                                    model = currentModel.id,
                                    messages = messages,
                                    maxTokens = maxTokens,
                                    temperature = temperature.toDouble(),
                                ),
                        )

                    val content = response.choices.firstOrNull()?.message?.content
                    if (content.isNullOrBlank()) {
                        lastException = Exception("No content generated by ${currentModel.displayName}")
                        Timber.w("Model ${currentModel.displayName} returned empty response")
                        continue
                    }

                    Timber.d("Success with model: ${currentModel.displayName}, ${response.usage.totalTokens} tokens")

                    // Track AI usage
                    trackUsage(
                        requestType = requestType,
                        modelUsed = currentModel.id,
                        promptTokens = response.usage.promptTokens,
                        completionTokens = response.usage.completionTokens,
                        totalTokens = response.usage.totalTokens,
                    )

                    return Result.success(content.trim())
                } catch (e: Exception) {
                    lastException = e
                    Timber.w(e, "Model ${currentModel.displayName} failed, trying next fallback")
                }
            }

            // All models failed
            val errorMessage =
                buildString {
                    append("All AI models failed. ")
                    append("Tried: ${modelsToTry.joinToString { it.displayName }}. ")
                    append("Last error: ${lastException?.localizedMessage ?: "Unknown error"}")
                }
            return Result.failure(Exception(errorMessage))
        }
    }
