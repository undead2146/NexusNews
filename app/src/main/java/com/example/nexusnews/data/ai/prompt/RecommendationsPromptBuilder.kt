package com.example.nexusnews.data.ai.prompt

import com.example.nexusnews.domain.ai.UserInterest

/**
 * Prompt builder for smart recommendations.
 * Generates personalized article recommendations based on user interests.
 */
class RecommendationsPromptBuilder(
    private val interests: List<UserInterest>,
    private val articles: Map<String, String>,
    private val limit: Int,
) : AiPromptBuilder {
    override fun build(): String =
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
}
