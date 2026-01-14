package com.example.nexusnews.domain.ai

/**
 * Domain models for AI-powered article analysis results.
 */

/**
 * Represents a key point extracted from an article.
 */
data class KeyPoint(
    val text: String,
    val importance: Float, // 0.0 to 1.0
    val position: Int, // Position in the article
)

/**
 * Result of key points extraction.
 */
data class KeyPointsResult(
    val keyPoints: List<KeyPoint>,
    val summary: String,
    val articleLength: Int,
)

/**
 * Types of entities that can be recognized.
 */
enum class EntityType {
    PERSON,
    ORGANIZATION,
    LOCATION,
    DATE,
    EVENT,
    PRODUCT,
    OTHER,
}

/**
 * Represents a recognized entity in the article.
 */
data class RecognizedEntity(
    val text: String,
    val type: EntityType,
    val confidence: Float, // 0.0 to 1.0
    val mentions: List<Int>, // Positions in the article
)

/**
 * Result of entity recognition.
 */
data class EntityRecognitionResult(
    val entities: List<RecognizedEntity>,
    val totalEntities: Int,
)

/**
 * Represents a topic/category classification.
 */
data class TopicClassification(
    val topic: String,
    val confidence: Float, // 0.0 to 1.0
    val subtopics: List<String>,
)

/**
 * Result of topic classification.
 */
data class TopicClassificationResult(
    val primaryTopic: TopicClassification,
    val secondaryTopics: List<TopicClassification>,
    val allTopics: List<String>,
)

/**
 * Represents bias analysis result.
 */
enum class BiasLevel {
    LOW,
    MEDIUM,
    HIGH,
    UNKNOWN,
}

/**
 * Represents detected bias in an article.
 */
data class BiasAnalysis(
    val level: BiasLevel,
    val biasType: String?, // e.g., "Political", "Corporate", "Confirmation"
    val explanation: String,
    val examples: List<String>,
)

/**
 * Result of bias detection.
 */
data class BiasDetectionResult(
    val biasAnalysis: BiasAnalysis,
    val objectivityScore: Float, // 0.0 to 1.0
    val credibilityIndicators: List<String>,
)

/**
 * Represents a user interest for recommendation profiling.
 */
data class UserInterest(
    val topic: String,
    val score: Float, // 0.0 to 1.0
    val lastUpdated: Long,
)

/**
 * Represents an article recommendation.
 */
data class ArticleRecommendation(
    val articleId: String,
    val score: Float, // 0.0 to 1.0
    val reason: String,
    val matchedInterests: List<String>,
)

/**
 * Result of smart recommendations.
 */
data class RecommendationResult(
    val recommendations: List<ArticleRecommendation>,
    val userProfile: List<UserInterest>,
)

/**
 * Represents a chat message in the AI assistant.
 */
data class ChatMessage(
    val id: String,
    val role: String, // "user", "assistant", "system"
    val content: String,
    val timestamp: Long,
    val articleContext: String?, // Article ID if related to an article
)

/**
 * Result of AI chat interaction.
 */
data class ChatResponse(
    val message: ChatMessage,
    val suggestedQuestions: List<String>,
    val relatedArticles: List<String>,
)

/**
 * Represents generated content.
 */
enum class ContentType {
    HEADLINE,
    SOCIAL_CAPTION,
    TAGS,
    READING_NOTOTE,
    CUSTOM_QUERY,
}

/**
 * Result of content generation.
 */
data class ContentGenerationResult(
    val type: ContentType,
    val content: String,
    val variations: List<String>,
    val metadata: Map<String, String>,
)
