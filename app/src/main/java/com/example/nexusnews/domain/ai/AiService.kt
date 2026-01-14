package com.example.nexusnews.domain.ai

/**
 * Domain interface for AI-powered features.
 * Provides abstraction over AI service implementations.
 */
interface AiService {
    /**
     * Generates a summary of an article.
     *
     * @param articleContent Full article text to summarize
     * @param maxLength Maximum length of summary in characters
     * @return Result with generated summary or error
     */
    suspend fun summarizeArticle(
        articleContent: String,
        maxLength: Int = 150,
    ): Result<String>

    /**
     * Analyzes the sentiment of an article.
     *
     * @param articleContent Article text to analyze
     * @return Result with sentiment or error
     */
    suspend fun analyzeSentiment(articleContent: String): Result<Sentiment>

    /**
     * Translates an article to a target language.
     *
     * @param articleContent Article text to translate
     * @param targetLanguage Target language code (e.g., "es", "fr", "de")
     * @return Result with translated text or error
     */
    suspend fun translateArticle(
        articleContent: String,
        targetLanguage: String,
    ): Result<String>

    // ==================== Phase 4: Advanced AI Features ====================

    /**
     * Extracts key points from an article.
     *
     * @param articleContent Article text to analyze
     * @param maxPoints Maximum number of key points to extract
     * @return Result with key points or error
     */
    suspend fun extractKeyPoints(
        articleContent: String,
        maxPoints: Int = 5,
    ): Result<KeyPointsResult>

    /**
     * Recognizes entities (people, places, organizations) in an article.
     *
     * @param articleContent Article text to analyze
     * @return Result with recognized entities or error
     */
    suspend fun recognizeEntities(articleContent: String): Result<EntityRecognitionResult>

    /**
     * Classifies the topic of an article.
     *
     * @param articleContent Article text to analyze
     * @param articleTitle Optional title for better classification
     * @return Result with topic classification or error
     */
    suspend fun classifyTopic(
        articleContent: String,
        articleTitle: String? = null,
    ): Result<TopicClassificationResult>

    /**
     * Detects bias in an article.
     *
     * @param articleContent Article text to analyze
     * @param articleTitle Optional title for better analysis
     * @return Result with bias detection or error
     */
    suspend fun detectBias(
        articleContent: String,
        articleTitle: String? = null,
    ): Result<BiasDetectionResult>

    /**
     * Generates personalized article recommendations.
     *
     * @param userInterests List of user interests with scores
     * @param availableArticles List of available article IDs and metadata
     * @param limit Maximum number of recommendations
     * @return Result with recommendations or error
     */
    suspend fun generateRecommendations(
        userInterests: List<UserInterest>,
        availableArticles: Map<String, String>, // articleId -> articleMetadata
        limit: Int = 10,
    ): Result<RecommendationResult>

    /**
     * Chat with AI assistant about an article.
     *
     * @param conversationHistory Previous messages in the conversation
     * @param userMessage Current user message
     * @param articleContext Optional article content for context
     * @return Result with AI response or error
     */
    suspend fun chatWithAssistant(
        conversationHistory: List<ChatMessage>,
        userMessage: String,
        articleContext: String? = null,
    ): Result<ChatResponse>

    /**
     * Generates content based on article.
     *
     * @param articleContent Article content to base generation on
     * @param contentType Type of content to generate
     * @param customPrompt Optional custom prompt for generation
     * @return Result with generated content or error
     */
    suspend fun generateContent(
        articleContent: String,
        contentType: ContentType,
        customPrompt: String? = null,
    ): Result<ContentGenerationResult>
}

/**
 * Sentiment classification for articles.
 */
enum class Sentiment {
    POSITIVE,
    NEUTRAL,
    NEGATIVE,
}
