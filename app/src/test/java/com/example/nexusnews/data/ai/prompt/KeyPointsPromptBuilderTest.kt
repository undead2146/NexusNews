package com.example.nexusnews.data.ai.prompt

import org.junit.Assert.assertTrue
import org.junit.Test

class KeyPointsPromptBuilderTest {

    @Test
    fun `KeyPointsPromptBuilder generates correct prompt structure`() {
        val articleContent = "This is a long article about AI and its future impact on news."
        val maxPoints = 5
        val builder = KeyPointsPromptBuilder(articleContent, maxPoints)

        val prompt = builder.build()

        assertTrue(prompt.contains("Extract the 5 most important key points"))
        assertTrue(prompt.contains("JSON"))
        assertTrue(prompt.contains(articleContent))
        assertTrue(prompt.contains("importance"))
        assertTrue(prompt.contains("position"))
    }
}
