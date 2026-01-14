package com.example.nexusnews.data.ai

import com.example.nexusnews.data.local.dao.AiUsageDao
import com.example.nexusnews.data.local.datastore.ApiKeyDataStore
import com.example.nexusnews.data.remote.api.OpenRouterApi
import com.example.nexusnews.data.remote.model.ChatCompletionRequest
import com.example.nexusnews.data.remote.model.Message
import com.example.nexusnews.domain.ai.*
import com.squareup.moshi.JsonAdapter
import com.squareup.moshi.Moshi
import com.squareup.moshi.Types
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
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

        // ==================== Phase 4: Advanced AI Features ====================

        override suspend fun extractKeyPoints(
            articleContent: String,
            maxPoints: Int,
        ): Result<KeyPointsResult> =
            run {
                val apiKey = getApiKeyOrThrow()
                val primaryModel = FreeAiModel.getDefault()

                val prompt = buildKeyPointsPrompt(articleContent, maxPoints)

                val jsonResult =
                    tryModelWithFallback(
                        apiKey = apiKey,
                        model = primaryModel,
                        prompt = prompt,
                        maxTokens = 500,
                        temperature = 0.3f,
                        requestType = com.example.nexusnews.data.local.entity.AiRequestType.KEY_POINTS_EXTRACTION,
                        systemPrompt = "You are a news analysis expert. Extract key points from articles in valid JSON format.",
                    )

                jsonResult.mapCatching { parseKeyPointsResponse(it, articleContent.length) }
            }

        override suspend fun recognizeEntities(articleContent: String): Result<EntityRecognitionResult> =
            run {
                val apiKey = getApiKeyOrThrow()
                val primaryModel = FreeAiModel.getDefault()

                val prompt = buildEntityRecognitionPrompt(articleContent)

                val jsonResult =
                    tryModelWithFallback(
                        apiKey = apiKey,
                        model = primaryModel,
                        prompt = prompt,
                        maxTokens = 800,
                        temperature = 0.2f,
                        requestType = com.example.nexusnews.data.local.entity.AiRequestType.ENTITY_RECOGNITION,
                        systemPrompt = "You are an entity recognition expert. Identify entities in valid JSON format.",
                    )

                jsonResult.mapCatching { parseEntityRecognitionResponse(it) }
            }

        override suspend fun classifyTopic(
            articleContent: String,
            articleTitle: String?,
        ): Result<TopicClassificationResult> =
            run {
                val apiKey = getApiKeyOrThrow()
                val primaryModel = FreeAiModel.getDefault()

                val prompt = buildTopicClassificationPrompt(articleContent, articleTitle)

                val jsonResult =
                    tryModelWithFallback(
                        apiKey = apiKey,
                        model = primaryModel,
                        prompt = prompt,
                        maxTokens = 400,
                        temperature = 0.3f,
                        requestType = com.example.nexusnews.data.local.entity.AiRequestType.TOPIC_CLASSIFICATION,
                        systemPrompt = "You are a news classification expert. Classify topics in valid JSON format.",
                    )

                jsonResult.mapCatching { parseTopicClassificationResponse(it) }
            }

        override suspend fun detectBias(
            articleContent: String,
            articleTitle: String?,
        ): Result<BiasDetectionResult> =
            run {
                val apiKey = getApiKeyOrThrow()
                val primaryModel = FreeAiModel.getDefault()

                val prompt = buildBiasDetectionPrompt(articleContent, articleTitle)

                val jsonResult =
                    tryModelWithFallback(
                        apiKey = apiKey,
                        model = primaryModel,
                        prompt = prompt,
                        maxTokens = 600,
                        temperature = 0.3f,
                        requestType = com.example.nexusnews.data.local.entity.AiRequestType.BIAS_DETECTION,
                        systemPrompt = "You are a media literacy expert. Analyze bias in valid JSON format.",
                    )

                jsonResult.mapCatching { parseBiasDetectionResponse(it) }
            }

        override suspend fun generateRecommendations(
            userInterests: List<UserInterest>,
            availableArticles: Map<String, String>,
            limit: Int,
        ): Result<RecommendationResult> =
            run {
                val apiKey = getApiKeyOrThrow()
                val primaryModel = FreeAiModel.getDefault()

                val prompt = buildRecommendationsPrompt(userInterests, availableArticles, limit)

                val jsonResult =
                    tryModelWithFallback(
                        apiKey = apiKey,
                        model = primaryModel,
                        prompt = prompt,
                        maxTokens = 1000,
                        temperature = 0.4f,
                        requestType = com.example.nexusnews.data.local.entity.AiRequestType.RECOMMENDATION,
                        systemPrompt = "You are a recommendation system expert. Generate recommendations in valid JSON format.",
                    )

                jsonResult.mapCatching { parseRecommendationsResponse(it, userInterests) }
            }

        override suspend fun chatWithAssistant(
            conversationHistory: List<ChatMessage>,
            userMessage: String,
            articleContext: String?,
        ): Result<ChatResponse> =
            run {
                val apiKey = getApiKeyOrThrow()
                val primaryModel = FreeAiModel.getDefault()

                val systemPrompt = buildChatSystemPrompt(articleContext)
                val messages = buildChatMessages(conversationHistory, userMessage)

                val textResult =
                    tryModelWithFallback(
                        apiKey = apiKey,
                        model = primaryModel,
                        prompt = "", // Messages are built separately
                        maxTokens = 500,
                        temperature = 0.7f,
                        requestType = com.example.nexusnews.data.local.entity.AiRequestType.CHAT_ASSISTANT,
                        systemPrompt = systemPrompt,
                    )

                textResult.mapCatching { responseText ->
                    parseChatResponse(responseText, conversationHistory, userMessage)
                }
            }

        override suspend fun generateContent(
            articleContent: String,
            contentType: ContentType,
            customPrompt: String?,
        ): Result<ContentGenerationResult> =
            run {
                val apiKey = getApiKeyOrThrow()
                val primaryModel = FreeAiModel.getDefault()

                val prompt = buildContentGenerationPrompt(articleContent, contentType, customPrompt)

                val jsonResult =
                    tryModelWithFallback(
                        apiKey = apiKey,
                        model = primaryModel,
                        prompt = prompt,
                        maxTokens = 400,
                        temperature = 0.8f,
                        requestType = com.example.nexusnews.data.local.entity.AiRequestType.CONTENT_GENERATION,
                        systemPrompt = "You are a creative content generator. Generate content in valid JSON format.",
                    )

                jsonResult.mapCatching { parseContentGenerationResponse(it, contentType) }
            }

        // ==================== Prompt Builders ====================

        private fun buildKeyPointsPrompt(
            content: String,
            maxPoints: Int,
        ): String =
            """
            Extract the $maxPoints most important key points from this news article.
            For each key point, provide:
            - text: The key point text (concise, 1-2 sentences)
            - importance: A score from 0.0 to 1.0
            - position: Approximate position in the article (0-100)

            Also provide a brief summary of all key points.

            Return valid JSON in this format:
            {
                "keyPoints": [
                    {"text": "Key point text", "importance": 0.9, "position": 10}
                ],
                "summary": "Brief summary of key points"
            }

            Article:
            ${content.take(4000)}
            """.trimIndent()

        private fun buildEntityRecognitionPrompt(content: String): String =
            """
            Identify all important entities in this news article.
            For each entity, provide:
            - text: The exact entity text as it appears
            - type: One of: PERSON, ORGANIZATION, LOCATION, DATE, EVENT, PRODUCT, OTHER
            - confidence: A score from 0.0 to 1.0
            - mentions: List of approximate positions where it appears (0-100)

            Return valid JSON in this format:
            {
                "entities": [
                    {"text": "Entity name", "type": "PERSON", "confidence": 0.95, "mentions": [10, 45]}
                ]
            }

            Article:
            ${content.take(3000)}
            """.trimIndent()

        private fun buildTopicClassificationPrompt(
            content: String,
            title: String?,
        ): String =
            """
            Classify the topic of this news article.
            Provide:
            - primaryTopic: Main topic with confidence and subtopics
            - secondaryTopics: Up to 3 related topics with confidence
            - allTopics: List of all relevant topics

            Common topics: Politics, Technology, Business, Health, Sports, Entertainment, Science, Environment, World News, Crime, Education, etc.

            Return valid JSON in this format:
            {
                "primaryTopic": {"topic": "Technology", "confidence": 0.9, "subtopics": ["AI", "Software"]},
                "secondaryTopics": [{"topic": "Business", "confidence": 0.7, "subtopics": []}],
                "allTopics": ["Technology", "Business", "AI"]
            }

            ${title?.let { "Title: $it\n\n" } ?: ""}Article:
            ${content.take(3000)}
            """.trimIndent()

        private fun buildBiasDetectionPrompt(
            content: String,
            title: String?,
        ): String =
            """
            Analyze this news article for potential bias.
            Provide:
            - biasAnalysis: level (LOW/MEDIUM/HIGH), type (Political/Corporate/Confirmation/Other), explanation, and examples
            - objectivityScore: Score from 0.0 to 1.0 (1.0 = completely objective)
            - credibilityIndicators: List of positive credibility indicators

            Return valid JSON in this format:
            {
                "biasAnalysis": {
                    "level": "LOW",
                    "biasType": "Political",
                    "explanation": "Brief explanation",
                    "examples": ["Example 1", "Example 2"]
                },
                "objectivityScore": 0.85,
                "credibilityIndicators": ["Multiple sources", "Factual claims", "Balanced perspective"]
            }

            ${title?.let { "Title: $it\n\n" } ?: ""}Article:
            ${content.take(4000)}
            """.trimIndent()

        private fun buildRecommendationsPrompt(
            interests: List<UserInterest>,
            articles: Map<String, String>,
            limit: Int,
        ): String =
            """
            Generate personalized article recommendations based on user interests.
            
            User interests: ${interests.joinToString { "${it.topic}(${it.score})" }}
            
            Available articles:
            ${articles.entries.take(20).joinToString("\n") { (id, meta) -> "- $id: $meta" }}
            
            Return the top $limit recommendations with:
            - articleId: The article ID
            - score: Relevance score from 0.0 to 1.0
            - reason: Brief explanation
            - matchedInterests: List of matched interests

            Return valid JSON in this format:
            {
                "recommendations": [
                    {"articleId": "id1", "score": 0.9, "reason": "Matches interest in Technology", "matchedInterests": ["Technology"]}
                ]
            }
            """.trimIndent()

        private fun buildChatSystemPrompt(articleContext: String?): String =
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

        private fun buildChatMessages(
            history: List<ChatMessage>,
            userMessage: String,
        ): List<Message> =
            buildList {
                // Add conversation history (last 5 messages)
                history.takeLast(5).forEach { msg ->
                    add(Message(msg.role, msg.content))
                }
                // Add current user message
                add(Message("user", userMessage))
            }

        private fun buildContentGenerationPrompt(
            content: String,
            type: ContentType,
            customPrompt: String?,
        ): String =
            when (type) {
                ContentType.HEADLINE ->
                    """
                    Generate 3 engaging headlines for this article.
                    Headlines should be catchy, accurate, and concise (under 80 chars).
                    
                    Return valid JSON:
                    {
                        "content": "Best headline",
                        "variations": ["Headline 1", "Headline 2", "Headline 3"],
                        "metadata": {"type": "headline"}
                    }
                    
                    Article: ${content.take(1000)}
                    """.trimIndent()

                ContentType.SOCIAL_CAPTION ->
                    """
                    Generate 3 social media captions for this article.
                    Include relevant hashtags.
                    
                    Return valid JSON:
                    {
                        "content": "Best caption",
                        "variations": ["Caption 1", "Caption 2", "Caption 3"],
                        "metadata": {"type": "social_caption", "hashtags": "#news #trending"}
                    }
                    
                    Article: ${content.take(1000)}
                    """.trimIndent()

                ContentType.TAGS ->
                    """
                    Generate relevant tags for this article.
                    Include 5-10 tags.
                    
                    Return valid JSON:
                    {
                        "content": "tag1, tag2, tag3",
                        "variations": ["tag1, tag2", "tag1, tag2, tag3, tag4"],
                        "metadata": {"type": "tags", "count": 5}
                    }
                    
                    Article: ${content.take(1000)}
                    """.trimIndent()

                ContentType.READING_NOTOTE ->
                    """
                    Generate reading notes for this article.
                    Include key takeaways, important facts, and questions to consider.
                    
                    Return valid JSON:
                    {
                        "content": "Reading notes...",
                        "variations": ["Notes 1", "Notes 2"],
                        "metadata": {"type": "reading_notes"}
                    }
                    
                    Article: ${content.take(2000)}
                    """.trimIndent()

                ContentType.CUSTOM_QUERY ->
                    """
                    $customPrompt
                    
                    Return valid JSON:
                    {
                        "content": "Generated content",
                        "variations": ["Variation 1", "Variation 2"],
                        "metadata": {"type": "custom"}
                    }
                    
                    Article: ${content.take(2000)}
                    """.trimIndent()
            }

        // ==================== Response Parsers ====================

        private val moshi: Moshi =
            Moshi.Builder()
                .add(KotlinJsonAdapterFactory())
                .build()

        private fun parseKeyPointsResponse(
            response: String,
            articleLength: Int,
        ): KeyPointsResult {
            try {
                val jsonAdapter: JsonAdapter<Map<String, Any>> = moshi.adapter(Types.newParameterizedType(Map::class.java, String::class.java, Any::class.java))
                val jsonMap = jsonAdapter.fromJson(response) ?: emptyMap()

                val keyPointsList = (jsonMap["keyPoints"] as? List<*>) ?: emptyList<Any>()
                val keyPoints =
                    keyPointsList.mapNotNull { item ->
                        val map = item as? Map<String, Any?> ?: return@mapNotNull null
                        KeyPoint(
                            text = map["text"]?.toString() ?: "",
                            importance = (map["importance"]?.toString()?.toDoubleOrNull() ?: 0.5).toFloat(),
                            position = (map["position"]?.toString()?.toIntOrNull() ?: 0),
                        )
                    }

                val summary = jsonMap["summary"]?.toString() ?: ""

                return KeyPointsResult(
                    keyPoints = keyPoints,
                    summary = summary,
                    articleLength = articleLength,
                )
            } catch (e: Exception) {
                Timber.e(e, "Failed to parse key points response")
                return KeyPointsResult(emptyList(), "", articleLength)
            }
        }

        private fun parseEntityRecognitionResponse(response: String): EntityRecognitionResult {
            try {
                val jsonAdapter: JsonAdapter<Map<String, Any>> = moshi.adapter(Types.newParameterizedType(Map::class.java, String::class.java, Any::class.java))
                val jsonMap = jsonAdapter.fromJson(response) ?: emptyMap()

                val entitiesList = (jsonMap["entities"] as? List<*>) ?: emptyList<Any>()
                val entities =
                    entitiesList.mapNotNull { item ->
                        val map = item as? Map<String, Any?> ?: return@mapNotNull null
                        RecognizedEntity(
                            text = map["text"]?.toString() ?: "",
                            type =
                                try {
                                    EntityType.valueOf(map["type"]?.toString()?.uppercase() ?: "OTHER")
                                } catch (e: Exception) {
                                    EntityType.OTHER
                                },
                            confidence = (map["confidence"]?.toString()?.toDoubleOrNull() ?: 0.5).toFloat(),
                            mentions =
                                (map["mentions"] as? List<*>)?.mapNotNull {
                                    it?.toString()?.toIntOrNull()
                                } ?: emptyList(),
                        )
                    }

                return EntityRecognitionResult(
                    entities = entities,
                    totalEntities = entities.size,
                )
            } catch (e: Exception) {
                Timber.e(e, "Failed to parse entity recognition response")
                return EntityRecognitionResult(emptyList(), 0)
            }
        }

        private fun parseTopicClassificationResponse(response: String): TopicClassificationResult {
            try {
                val jsonAdapter: JsonAdapter<Map<String, Any>> = moshi.adapter(Types.newParameterizedType(Map::class.java, String::class.java, Any::class.java))
                val jsonMap = jsonAdapter.fromJson(response) ?: emptyMap()

                val primaryMap = (jsonMap["primaryTopic"] as? Map<String, Any?>) ?: emptyMap()
                val primaryTopic =
                    TopicClassification(
                        topic = primaryMap["topic"]?.toString() ?: "General",
                        confidence = (primaryMap["confidence"]?.toString()?.toDoubleOrNull() ?: 0.5).toFloat(),
                        subtopics =
                            (primaryMap["subtopics"] as? List<*>)?.mapNotNull {
                                it?.toString()
                            } ?: emptyList(),
                    )

                val secondaryList = (jsonMap["secondaryTopics"] as? List<*>) ?: emptyList<Any>()
                val secondaryTopics =
                    secondaryList.mapNotNull { item ->
                        val map = item as? Map<String, Any?> ?: return@mapNotNull null
                        TopicClassification(
                            topic = map["topic"]?.toString() ?: "",
                            confidence = (map["confidence"]?.toString()?.toDoubleOrNull() ?: 0.5).toFloat(),
                            subtopics =
                                (map["subtopics"] as? List<*>)?.mapNotNull {
                                    it?.toString()
                                } ?: emptyList(),
                        )
                    }

                val allTopics =
                    (jsonMap["allTopics"] as? List<*>)?.mapNotNull {
                        it?.toString()
                    } ?: emptyList()

                return TopicClassificationResult(
                    primaryTopic = primaryTopic,
                    secondaryTopics = secondaryTopics,
                    allTopics = allTopics,
                )
            } catch (e: Exception) {
                Timber.e(e, "Failed to parse topic classification response")
                return TopicClassificationResult(
                    TopicClassification("General", 0.5f, emptyList()),
                    emptyList(),
                    emptyList(),
                )
            }
        }

        private fun parseBiasDetectionResponse(response: String): BiasDetectionResult {
            try {
                val jsonAdapter: JsonAdapter<Map<String, Any>> = moshi.adapter(Types.newParameterizedType(Map::class.java, String::class.java, Any::class.java))
                val jsonMap = jsonAdapter.fromJson(response) ?: emptyMap()

                val biasMap = (jsonMap["biasAnalysis"] as? Map<String, Any?>) ?: emptyMap()
                val biasAnalysis =
                    BiasAnalysis(
                        level =
                            try {
                                BiasLevel.valueOf(biasMap["level"]?.toString()?.uppercase() ?: "UNKNOWN")
                            } catch (e: Exception) {
                                BiasLevel.UNKNOWN
                            },
                        biasType = biasMap["biasType"]?.toString(),
                        explanation = biasMap["explanation"]?.toString() ?: "",
                        examples =
                            (biasMap["examples"] as? List<*>)?.mapNotNull {
                                it?.toString()
                            } ?: emptyList(),
                    )

                val objectivityScore = (jsonMap["objectivityScore"]?.toString()?.toDoubleOrNull() ?: 0.5).toFloat()

                val credibilityIndicators =
                    (jsonMap["credibilityIndicators"] as? List<*>)?.mapNotNull {
                        it?.toString()
                    } ?: emptyList()

                return BiasDetectionResult(
                    biasAnalysis = biasAnalysis,
                    objectivityScore = objectivityScore,
                    credibilityIndicators = credibilityIndicators,
                )
            } catch (e: Exception) {
                Timber.e(e, "Failed to parse bias detection response")
                return BiasDetectionResult(
                    BiasAnalysis(BiasLevel.UNKNOWN, null, "", emptyList()),
                    0.5f,
                    emptyList(),
                )
            }
        }

        private fun parseRecommendationsResponse(
            response: String,
            userInterests: List<UserInterest>,
        ): RecommendationResult {
            try {
                val jsonAdapter: JsonAdapter<Map<String, Any>> = moshi.adapter(Types.newParameterizedType(Map::class.java, String::class.java, Any::class.java))
                val jsonMap = jsonAdapter.fromJson(response) ?: emptyMap()

                val recommendationsList = (jsonMap["recommendations"] as? List<*>) ?: emptyList<Any>()
                val recommendations =
                    recommendationsList.mapNotNull { item ->
                        val map = item as? Map<String, Any?> ?: return@mapNotNull null
                        ArticleRecommendation(
                            articleId = map["articleId"]?.toString() ?: "",
                            score = (map["score"]?.toString()?.toDoubleOrNull() ?: 0.5).toFloat(),
                            reason = map["reason"]?.toString() ?: "",
                            matchedInterests =
                                (map["matchedInterests"] as? List<*>)?.mapNotNull {
                                    it?.toString()
                                } ?: emptyList(),
                        )
                    }

                return RecommendationResult(
                    recommendations = recommendations,
                    userProfile = userInterests,
                )
            } catch (e: Exception) {
                Timber.e(e, "Failed to parse recommendations response")
                return RecommendationResult(emptyList(), userInterests)
            }
        }

        private fun parseChatResponse(
            response: String,
            history: List<ChatMessage>,
            userMessage: String,
        ): ChatResponse {
            // Generate suggested questions based on response
            val suggestedQuestions =
                listOf(
                    "Can you explain more about that?",
                    "What's the source of this information?",
                    "How does this relate to current events?",
                )

            // Create assistant message
            val assistantMessage =
                ChatMessage(
                    id = "msg_${System.currentTimeMillis()}",
                    role = "assistant",
                    content = response.trim(),
                    timestamp = System.currentTimeMillis(),
                    articleContext = null,
                )

            return ChatResponse(
                message = assistantMessage,
                suggestedQuestions = suggestedQuestions,
                relatedArticles = emptyList(), // Could be enhanced to find related articles
            )
        }

        private fun parseContentGenerationResponse(
            response: String,
            type: ContentType,
        ): ContentGenerationResult {
            try {
                val jsonAdapter: JsonAdapter<Map<String, Any>> = moshi.adapter(Types.newParameterizedType(Map::class.java, String::class.java, Any::class.java))
                val jsonMap = jsonAdapter.fromJson(response) ?: emptyMap()

                val content = jsonMap["content"]?.toString() ?: ""
                val variations =
                    (jsonMap["variations"] as? List<*>)?.mapNotNull {
                        it?.toString()
                    } ?: emptyList()

                val metadataMap = (jsonMap["metadata"] as? Map<String, Any?>) ?: emptyMap()
                val metadata =
                    metadataMap.mapValues { (_, value) ->
                        value?.toString() ?: ""
                    }

                return ContentGenerationResult(
                    type = type,
                    content = content,
                    variations = variations,
                    metadata = metadata,
                )
            } catch (e: Exception) {
                Timber.e(e, "Failed to parse content generation response")
                return ContentGenerationResult(type, "", emptyList(), emptyMap())
            }
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
