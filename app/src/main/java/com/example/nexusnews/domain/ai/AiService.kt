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
}

/**
 * Sentiment classification for articles.
 */
enum class Sentiment {
    POSITIVE,
    NEUTRAL,
    NEGATIVE,
}
