package com.example.nexusnews.data.ai.parser

import com.example.nexusnews.domain.ai.*
import com.squareup.moshi.JsonAdapter
import com.squareup.moshi.Moshi
import com.squareup.moshi.Types
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import timber.log.Timber

/**
 * Interface for AI response parsers.
 * Provides a contract for parsing AI responses.
 */
interface AiResponseParser<T> {
    /**
     * Parses the AI response string into a domain model.
     *
     * @param response The raw response string from the AI service
     * @return The parsed domain model or default on error
     */
    fun parse(response: String): T
}

/**
 * Base parser for AI responses using Moshi.
 * Provides common parsing logic with error handling.
 */
abstract class BaseMoshiParser<T>(
    private val defaultValue: T,
) : AiResponseParser<T> {
    protected val moshi: Moshi =
        Moshi.Builder()
            .add(KotlinJsonAdapterFactory())
            .build()

    protected val jsonAdapter: JsonAdapter<Map<String, Any>> =
        moshi.adapter(Types.newParameterizedType(Map::class.java, String::class.java, Any::class.java))

    override fun parse(response: String): T {
        return try {
            parseInternal(response)
        } catch (e: Exception) {
            Timber.e(e, "Failed to parse ${this::class.simpleName}")
            defaultValue
        }
    }

    /**
     * Internal parsing logic to be implemented by subclasses.
     */
    protected abstract fun parseInternal(response: String): T
}

/**
 * Parser for key points extraction responses.
 */
class KeyPointsResponseParser(
    private val articleLength: Int,
) : BaseMoshiParser<KeyPointsResult>(
    defaultValue = KeyPointsResult(emptyList(), "", articleLength),
) {
    override fun parseInternal(response: String): KeyPointsResult {
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
    }
}

/**
 * Parser for entity recognition responses.
 */
class EntityRecognitionResponseParser : BaseMoshiParser<EntityRecognitionResult>(
    defaultValue = EntityRecognitionResult(emptyList(), 0),
) {
    override fun parseInternal(response: String): EntityRecognitionResult {
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
    }
}

/**
 * Parser for topic classification responses.
 */
class TopicClassificationResponseParser : BaseMoshiParser<TopicClassificationResult>(
    defaultValue = TopicClassificationResult(
        TopicClassification("General", 0.5f, emptyList()),
        emptyList(),
        emptyList(),
    ),
) {
    override fun parseInternal(response: String): TopicClassificationResult {
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
    }
}

/**
 * Parser for bias detection responses.
 */
class BiasDetectionResponseParser : BaseMoshiParser<BiasDetectionResult>(
    defaultValue = BiasDetectionResult(
        BiasAnalysis(BiasLevel.UNKNOWN, null, "", emptyList()),
        0.5f,
        emptyList(),
    ),
) {
    override fun parseInternal(response: String): BiasDetectionResult {
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
    }
}

/**
 * Parser for recommendation responses.
 */
class RecommendationResponseParser(
    private val userInterests: List<UserInterest>,
) : BaseMoshiParser<RecommendationResult>(
    defaultValue = RecommendationResult(emptyList(), userInterests),
) {
    override fun parseInternal(response: String): RecommendationResult {
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
    }
}

/**
 * Parser for content generation responses.
 */
class ContentGenerationResponseParser(
    private val contentType: ContentType,
) : BaseMoshiParser<ContentGenerationResult>(
    defaultValue = ContentGenerationResult(contentType, "", emptyList(), emptyMap()),
) {
    override fun parseInternal(response: String): ContentGenerationResult {
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
            type = contentType,
            content = content,
            variations = variations,
            metadata = metadata,
        )
    }
}

/**
 * Parser for chat responses.
 */
class ChatResponseParser(
    private val history: List<ChatMessage>,
    private val userMessage: String,
) : AiResponseParser<ChatResponse> {
    override fun parse(response: String): ChatResponse {
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
}
