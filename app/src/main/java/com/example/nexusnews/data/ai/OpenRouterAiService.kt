package com.example.nexusnews.data.ai

import com.example.nexusnews.data.ai.parser.*
import com.example.nexusnews.data.ai.prompt.*
import com.example.nexusnews.data.local.dao.AiUsageDao
import com.example.nexusnews.data.local.datastore.ApiKeyDataStore
import com.example.nexusnews.data.remote.api.OpenRouterApi
import com.example.nexusnews.data.remote.model.ChatCompletionRequest
import com.example.nexusnews.domain.ai.*
import timber.log.Timber
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Implementation of AiService using OpenRouter API.
 * Implements clean architecture with separated concerns:
 * - Prompt builders for AI prompt generation
 * - Response parsers for JSON parsing
 * - Configuration classes for request parameters
 * - Usage tracking for monitoring
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
         * Service configuration.
         */
        private val config: AiServiceConfig
            get() {
                val storedKey = apiKeyDataStore.getOpenRouterApiKey()
                val apiKey = if (storedKey.isNullOrBlank()) {
                    com.example.nexusnews.BuildConfig.OPENROUTER_API_KEY.takeIf { it.isNotBlank() }
                } else {
                    storedKey
                }
                return AiServiceConfig.create(apiKey)
            }

        override suspend fun summarizeArticle(
            articleContent: String,
            maxLength: Int,
        ): Result<String> =
            run {
                val prompt = buildSummarizationPrompt(articleContent, maxLength)
                val requestConfig =
                    AiRequestConfig(
                        maxTokens = maxLength + 50,
                        temperature = 0.3f,
                        systemPrompt = "You are a news summarization assistant. Summarize concisely and objectively.",
                        requestType = com.example.nexusnews.data.local.entity.AiRequestType.SUMMARIZATION,
                    )

                executeAiRequest(prompt, requestConfig)
            }

        override suspend fun analyzeSentiment(articleContent: String): Result<Sentiment> =
            run {
                val prompt = buildSentimentPrompt(articleContent)
                val requestConfig =
                    AiRequestConfig(
                        maxTokens = 10,
                        temperature = 0.1f,
                        systemPrompt = "You are a sentiment analysis assistant. Respond with only: POSITIVE, NEUTRAL, or NEGATIVE.",
                        requestType = com.example.nexusnews.data.local.entity.AiRequestType.SENTIMENT_ANALYSIS,
                    )

                executeAiRequest(prompt, requestConfig).map { sentimentText ->
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
                val prompt = buildTranslationPrompt(articleContent, targetLanguage)
                val requestConfig =
                    AiRequestConfig(
                        maxTokens = articleContent.length + 500,
                        temperature = 0.3f,
                        systemPrompt = "You are a professional translator. Translate accurately while preserving meaning.",
                        requestType = com.example.nexusnews.data.local.entity.AiRequestType.TRANSLATION,
                    )

                executeAiRequest(prompt, requestConfig)
            }

        // ==================== Phase 4: Advanced AI Features ====================

        override suspend fun extractKeyPoints(
            articleContent: String,
            maxPoints: Int,
        ): Result<KeyPointsResult> =
            run {
                val promptBuilder = KeyPointsPromptBuilder(articleContent, maxPoints)
                val requestConfig = AiRequestConfig.forKeyPoints()

                executeAiRequest(promptBuilder.build(), requestConfig).mapCatching {
                    KeyPointsResponseParser(articleContent.length).parse(it)
                }
            }

        override suspend fun recognizeEntities(articleContent: String): Result<EntityRecognitionResult> =
            run {
                val promptBuilder = EntityRecognitionPromptBuilder(articleContent)
                val requestConfig = AiRequestConfig.forEntityRecognition()

                executeAiRequest(promptBuilder.build(), requestConfig).mapCatching {
                    EntityRecognitionResponseParser().parse(it)
                }
            }

        override suspend fun classifyTopic(
            articleContent: String,
            articleTitle: String?,
        ): Result<TopicClassificationResult> =
            run {
                val promptBuilder = TopicClassificationPromptBuilder(articleContent, articleTitle)
                val requestConfig = AiRequestConfig.forTopicClassification()

                executeAiRequest(promptBuilder.build(), requestConfig).mapCatching {
                    TopicClassificationResponseParser().parse(it)
                }
            }

        override suspend fun detectBias(
            articleContent: String,
            articleTitle: String?,
        ): Result<BiasDetectionResult> =
            run {
                val promptBuilder = BiasDetectionPromptBuilder(articleContent, articleTitle)
                val requestConfig = AiRequestConfig.forBiasDetection()

                executeAiRequest(promptBuilder.build(), requestConfig).mapCatching {
                    BiasDetectionResponseParser().parse(it)
                }
            }

        override suspend fun generateRecommendations(
            userInterests: List<UserInterest>,
            availableArticles: Map<String, String>,
            limit: Int,
        ): Result<RecommendationResult> =
            run {
                val promptBuilder = RecommendationsPromptBuilder(userInterests, availableArticles, limit)
                val requestConfig = AiRequestConfig.forRecommendations()

                executeAiRequest(promptBuilder.build(), requestConfig).mapCatching {
                    RecommendationResponseParser(userInterests).parse(it)
                }
            }

        override suspend fun chatWithAssistant(
            conversationHistory: List<ChatMessage>,
            userMessage: String,
            articleContext: String?,
        ): Result<ChatResponse> =
            run {
                val systemPromptBuilder = ChatPromptBuilder(articleContext)
                val messageBuilder = ChatMessageBuilder(conversationHistory, userMessage)

                val requestConfig = AiRequestConfig.forChatAssistant()
                val messages = messageBuilder.build()

                executeAiRequest("", requestConfig, messages, systemPromptBuilder.build()).mapCatching {
                    ChatResponseParser(conversationHistory, userMessage).parse(it)
                }
            }

        override suspend fun generateContent(
            articleContent: String,
            contentType: ContentType,
            customPrompt: String?,
        ): Result<ContentGenerationResult> =
            run {
                val promptBuilder = ContentGenerationPromptBuilder(articleContent, contentType, customPrompt)
                val requestConfig = AiRequestConfig.forContentGeneration(contentType)

                executeAiRequest(promptBuilder.build(), requestConfig).mapCatching {
                    ContentGenerationResponseParser(contentType).parse(it)
                }
            }

        // ==================== Helper Methods ====================

        /**
         * Executes an AI request with fallback model support.
         *
         * @param prompt The user prompt
         * @param requestConfig The request configuration
         * @param messages Optional messages list for chat
         * @param systemPrompt Optional system prompt
         * @return Result with the AI response or error
         */
        private suspend fun executeAiRequest(
            prompt: String,
            requestConfig: AiRequestConfig,
            messages: List<com.example.nexusnews.data.remote.model.Message>? = null,
            systemPrompt: String? = requestConfig.systemPrompt,
        ): Result<String> {
            val modelsToTry = listOf(config.primaryModel) + config.fallbackModels.filter { it != config.primaryModel }

            for (currentModel in modelsToTry) {
                try {
                    val startTime = System.currentTimeMillis()
                    Timber.d("Trying model: ${currentModel.displayName} (${currentModel.id})")

                    val response =
                        openRouterApi.chatCompletion(
                            authorization = "Bearer ${config.apiKey}",
                            request =
                                ChatCompletionRequest(
                                    model = currentModel.id,
                                    messages = messages ?: buildMessages(prompt, systemPrompt),
                                    maxTokens = requestConfig.maxTokens,
                                    temperature = requestConfig.temperature.toDouble(),
                                ),
                        )

                    val content = response.choices.firstOrNull()?.message?.content
                    if (content.isNullOrBlank()) {
                        Timber.w("Model ${currentModel.displayName} returned empty response")
                        continue
                    }

                    Timber.d("Success with model: ${currentModel.displayName}, ${response.usage.totalTokens} tokens")

                    // Track AI usage
                    trackUsage(
                        requestType = requestConfig.requestType,
                        modelUsed = currentModel.id,
                        promptTokens = response.usage.promptTokens,
                        completionTokens = response.usage.completionTokens,
                        totalTokens = response.usage.totalTokens,
                        latencyMs = System.currentTimeMillis() - startTime,
                    )

                    return Result.success(content.trim())
                } catch (e: Exception) {
                    Timber.w(e, "Model ${currentModel.displayName} failed, trying next fallback")
                }
            }

            // All models failed
            val errorMessage =
                buildString {
                    append("All AI models failed. ")
                    append("Tried: ${modelsToTry.joinToString { it.displayName }}. ")
                    append("Last error: ${"Unknown error"}")
                }
            return Result.failure(Exception(errorMessage))
        }

        /**
         * Builds messages list for API calls.
         */
        private fun buildMessages(
            prompt: String,
            systemPrompt: String?,
        ): List<com.example.nexusnews.data.remote.model.Message> =
            buildList {
                if (systemPrompt != null) {
                    add(com.example.nexusnews.data.remote.model.Message("system", systemPrompt))
                }
                add(com.example.nexusnews.data.remote.model.Message("user", prompt))
            }

        /**
         * Tracks AI API usage in the database.
         */
        private suspend fun trackUsage(
            requestType: com.example.nexusnews.data.local.entity.AiRequestType,
            modelUsed: String,
            promptTokens: Int,
            completionTokens: Int,
            totalTokens: Int,
            latencyMs: Long,
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
                        latencyMs = latencyMs,
                        timestamp = java.time.LocalDateTime.now(),
                    )
                aiUsageDao.insertUsage(usageEntity)
                Timber.d("AI usage tracked: $requestType, $totalTokens tokens")
            } catch (e: Exception) {
                Timber.e(e, "Failed to track AI usage")
            }
        }

        // ==================== Phase 3 Prompt Builders ====================
        // These methods handle Phase 3 features (summarization, sentiment, translation)

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
}
