package com.example.nexusnews.data.ai.prompt

/**
 * Prompt builder for bias detection.
 * Analyzes articles for potential bias.
 */
class BiasDetectionPromptBuilder(
    private val content: String,
    private val title: String?,
) : AiPromptBuilder {
    override fun build(): String =
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
}
