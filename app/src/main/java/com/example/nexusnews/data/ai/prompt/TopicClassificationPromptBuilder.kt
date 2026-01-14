package com.example.nexusnews.data.ai.prompt

/**
 * Prompt builder for topic classification.
 * Classifies the topic of articles.
 */
class TopicClassificationPromptBuilder(
    private val content: String,
    private val title: String?,
) : AiPromptBuilder {
    override fun build(): String =
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
}
