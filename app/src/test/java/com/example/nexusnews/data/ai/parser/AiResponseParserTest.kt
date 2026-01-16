package com.example.nexusnews.data.ai.parser

import com.example.nexusnews.domain.ai.*
import org.junit.Assert.assertEquals
import org.junit.Test

class AiResponseParserTest {

    @Test
    fun `KeyPointsResponseParser parses valid JSON correctly`() {
        val parser = KeyPointsResponseParser(articleLength = 1000)
        val json = """
            {
                "keyPoints": [
                    { "text": "Point 1", "importance": 0.8, "position": 10 },
                    { "text": "Point 2", "importance": 0.4, "position": 50 }
                ],
                "summary": "This is a summary"
            }
        """.trimIndent()

        val result = parser.parse(json)

        assertEquals(2, result.keyPoints.size)
        assertEquals("Point 1", result.keyPoints[0].text)
        assertEquals(0.8f, result.keyPoints[0].importance)
        assertEquals(10, result.keyPoints[0].position)
        assertEquals("This is a summary", result.summary)
        assertEquals(1000, result.articleLength)
    }

    @Test
    fun `EntityRecognitionResponseParser parses valid JSON correctly`() {
        val parser = EntityRecognitionResponseParser()
        val json = """
            {
                "entities": [
                    { "text": "Google", "type": "ORGANIZATION", "confidence": 0.9, "mentions": [1, 2] },
                    { "text": "London", "type": "LOCATION", "confidence": 0.7, "mentions": [10] }
                ]
            }
        """.trimIndent()

        val result = parser.parse(json)

        assertEquals(2, result.entities.size)
        assertEquals("Google", result.entities[0].text)
        assertEquals(EntityType.ORGANIZATION, result.entities[0].type)
        assertEquals(0.9f, result.entities[0].confidence)
        assertEquals(listOf(1, 2), result.entities[0].mentions)
    }

    @Test
    fun `TopicClassificationResponseParser parses valid JSON correctly`() {
        val parser = TopicClassificationResponseParser()
        val json = """
            {
                "primaryTopic": {
                    "topic": "Technology",
                    "confidence": 0.95,
                    "subtopics": ["AI", "Cloud"]
                },
                "secondaryTopics": [
                    { "topic": "Business", "confidence": 0.4, "subtopics": [] }
                ],
                "allTopics": ["Tech", "Cloud", "Business"]
            }
        """.trimIndent()

        val result = parser.parse(json)

        assertEquals("Technology", result.primaryTopic.topic)
        assertEquals(0.95f, result.primaryTopic.confidence)
        assertEquals(listOf("AI", "Cloud"), result.primaryTopic.subtopics)
        assertEquals(1, result.secondaryTopics.size)
        assertEquals("Business", result.secondaryTopics[0].topic)
        assertEquals(listOf("Tech", "Cloud", "Business"), result.allTopics)
    }

    @Test
    fun `BiasDetectionResponseParser parses valid JSON correctly`() {
        val parser = BiasDetectionResponseParser()
        val json = """
            {
                "biasAnalysis": {
                    "level": "LOW",
                    "biasType": "None",
                    "explanation": "Objective reporting",
                    "examples": []
                },
                "objectivityScore": 0.9,
                "credibilityIndicators": ["Fact-based", "Multiple sources"]
            }
        """.trimIndent()

        val result = parser.parse(json)

        assertEquals(BiasLevel.LOW, result.biasAnalysis.level)
        assertEquals("Objective reporting", result.biasAnalysis.explanation)
        assertEquals(0.9f, result.objectivityScore)
        assertEquals(listOf("Fact-based", "Multiple sources"), result.credibilityIndicators)
    }

    @Test
    fun `BaseMoshiParser handles malformed JSON gracefully`() {
        val parser = KeyPointsResponseParser(articleLength = 500)
        val malformedJson = "{ invalid json }"

        val result = parser.parse(malformedJson)

        assertEquals(0, result.keyPoints.size)
        assertEquals("", result.summary)
        assertEquals(500, result.articleLength)
    }
}
