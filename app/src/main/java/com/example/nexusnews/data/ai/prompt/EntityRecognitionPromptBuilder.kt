package com.example.nexusnews.data.ai.prompt

/**
 * Prompt builder for entity recognition.
 * Identifies entities (people, places, organizations) in articles.
 */
class EntityRecognitionPromptBuilder(
    private val content: String,
) : AiPromptBuilder {
    override fun build(): String =
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
}
