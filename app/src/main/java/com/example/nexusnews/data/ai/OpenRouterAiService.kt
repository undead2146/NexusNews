package com.example.nexusnews.data.ai

import com.example.nexusnews.data.local.datastore.ApiKeyDataStore
import com.example.nexusnews.data.remote.api.OpenRouterApi
import com.example.nexusnews.data.remote.model.ChatCompletionRequest
import com.example.nexusnews.data.remote.model.Message
import com.example.nexusnews.domain.ai.AiService
import com.example.nexusnews.domain.ai.FreeAiModel
import com.example.nexusnews.domain.ai.Sentiment
import timber.log.Timber
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Implementation of AiService using OpenRouter API.
 */
@Singleton
class OpenRouterAiService
    @Inject
    constructor(
        private val openRouterApi: OpenRouterApi,
        private val apiKeyDataStore: ApiKeyDataStore,
    ) : AiService {
        override suspend fun summarizeArticle(
            articleContent: String,
            maxLength: Int,
        ): Result<String> =
            try {
                val apiKey = getApiKeyOrThrow()
                val model = FreeAiModel.getDefault()

                val prompt = buildSummarizationPrompt(articleContent, maxLength)
                val response =
                    openRouterApi.chatCompletion(
                        authorization = "Bearer $apiKey",
                        request =
                            ChatCompletionRequest(
                                model = model.id,
                                messages =
                                    listOf(
                                        Message("system", "You are a news summarization assistant. Provide concise, objective summaries."),
                                        Message("user", prompt),
                                    ),
                                maxTokens = maxLength + 50, // Buffer for completion
                                temperature = 0.3, // Low temperature for consistency
                            ),
                    )

                val summary = response.choices.firstOrNull()?.message?.content
                    ?: return Result.failure(Exception("No summary generated"))

                Timber.d("Summary generated: ${response.usage.totalTokens} tokens used")
                Result.success(summary.trim())
            } catch (e: Exception) {
                Timber.e(e, "Failed to generate summary")
                Result.failure(e)
            }

        override suspend fun analyzeSentiment(articleContent: String): Result<Sentiment> =
            try {
                val apiKey = getApiKeyOrThrow()
                val model = FreeAiModel.getDefault()

                val prompt = buildSentimentPrompt(articleContent)
                val response =
                    openRouterApi.chatCompletion(
                        authorization = "Bearer $apiKey",
                        request =
                            ChatCompletionRequest(
                                model = model.id,
                                messages =
                                    listOf(
                                        Message("system", "You are a sentiment analysis assistant. Respond with only: POSITIVE, NEUTRAL, or NEGATIVE."),
                                        Message("user", prompt),
                                    ),
                                maxTokens = 10,
                                temperature = 0.1,
                            ),
                    )

                val sentimentText = response.choices.firstOrNull()?.message?.content?.trim()?.uppercase()
                    ?: return Result.failure(Exception("No sentiment generated"))

                val sentiment =
                    when (sentimentText) {
                        "POSITIVE" -> Sentiment.POSITIVE
                        "NEGATIVE" -> Sentiment.NEGATIVE
                        else -> Sentiment.NEUTRAL
                    }

                Result.success(sentiment)
            } catch (e: Exception) {
                Timber.e(e, "Failed to analyze sentiment")
                Result.failure(e)
            }

        override suspend fun translateArticle(
            articleContent: String,
            targetLanguage: String,
        ): Result<String> =
            try {
                val apiKey = getApiKeyOrThrow()
                val model = FreeAiModel.getDefault()

                val prompt = buildTranslationPrompt(articleContent, targetLanguage)
                val response =
                    openRouterApi.chatCompletion(
                        authorization = "Bearer $apiKey",
                        request =
                            ChatCompletionRequest(
                                model = model.id,
                                messages =
                                    listOf(
                                        Message("system", "You are a professional translator. Translate accurately while preserving meaning."),
                                        Message("user", prompt),
                                    ),
                                maxTokens = articleContent.length + 500,
                                temperature = 0.3,
                            ),
                    )

                val translation = response.choices.firstOrNull()?.message?.content
                    ?: return Result.failure(Exception("No translation generated"))

                Result.success(translation.trim())
            } catch (e: Exception) {
                Timber.e(e, "Failed to translate article")
                Result.failure(e)
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
    }
