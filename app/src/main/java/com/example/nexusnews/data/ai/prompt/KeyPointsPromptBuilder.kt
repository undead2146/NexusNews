package com.example.nexusnews.data.ai.prompt

/**
 * Prompt builder for key points extraction.
 * Extracts the most important key points from articles.
 */
class KeyPointsPromptBuilder(
    private val content: String,
    private val maxPoints: Int,
) : AiPromptBuilder {
    override fun build(): String =
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
}
