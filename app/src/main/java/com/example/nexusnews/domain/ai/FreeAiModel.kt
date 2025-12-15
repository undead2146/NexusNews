package com.example.nexusnews.domain.ai

/**
 * Configuration for free AI models available on OpenRouter.
 */
enum class FreeAiModel(
    val id: String,
    val displayName: String,
    val description: String,
    val maxTokens: Int,
    val recommended: Boolean = false,
) {
    // Recommended free models
    LLAMA_3_3_70B(
        id = "meta-llama/llama-3.3-70b-instruct:free",
        displayName = "Llama 3.3 70B",
        description = "Meta's powerful multilingual model, great for summarization",
        maxTokens = 128000,
        recommended = true,
    ),
    GEMMA_2_27B(
        id = "google/gemma-2-27b-it:free",
        displayName = "Gemma 2 27B",
        description = "Google's efficient model with strong reasoning",
        maxTokens = 8192,
        recommended = true,
    ),
    MISTRAL_SMALL(
        id = "mistralai/mistral-small-3.1-24b-instruct:free",
        displayName = "Mistral Small 3.1",
        description = "Fast and efficient for quick tasks",
        maxTokens = 32768,
        recommended = true,
    ),

    // Additional free options
    GEMMA_2_9B(
        id = "google/gemma-2-9b-it:free",
        displayName = "Gemma 2 9B",
        description = "Lightweight and fast",
        maxTokens = 8192,
    ),
    LLAMA_3_1_8B(
        id = "meta-llama/llama-3.1-8b-instruct:free",
        displayName = "Llama 3.1 8B",
        description = "Compact and efficient",
        maxTokens = 128000,
    ),
    QWEN_3_235B(
        id = "qwen/qwen3-235b-a22b:free",
        displayName = "Qwen 3 235B",
        description = "Large context window",
        maxTokens = 32768,
    ),
    ;

    companion object {
        /**
         * Gets all recommended models.
         */
        fun getRecommended(): List<FreeAiModel> = entries.filter { it.recommended }

        /**
         * Gets the default model.
         */
        fun getDefault(): FreeAiModel = LLAMA_3_3_70B

        /**
         * Finds a model by ID.
         */
        fun findById(id: String): FreeAiModel? = entries.find { it.id == id }
    }
}
