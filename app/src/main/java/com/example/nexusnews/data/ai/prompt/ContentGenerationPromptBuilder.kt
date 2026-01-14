package com.example.nexusnews.data.ai.prompt

import com.example.nexusnews.domain.ai.ContentType

/**
 * Prompt builder for content generation.
 * Generates various types of content based on articles.
 */
class ContentGenerationPromptBuilder(
    private val content: String,
    private val type: ContentType,
    private val customPrompt: String?,
) : AiPromptBuilder {
    override fun build(): String =
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
}
