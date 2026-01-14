package com.example.nexusnews.data.ai.prompt

/**
 * Interface for AI prompt builders.
 * Provides a contract for building prompts for different AI features.
 */
interface AiPromptBuilder {
    /**
     * Builds the prompt for the AI service.
     *
     * @return The formatted prompt string
     */
    fun build(): String
}

/**
 * Configuration for AI prompt generation.
 * Contains common parameters used across prompt builders.
 */
data class PromptConfig(
    val maxContentLength: Int = 4000,
    val maxTokens: Int = 500,
    val temperature: Float = 0.3f,
)
