package com.example.nexusnews.data.ai

import com.example.nexusnews.data.local.dao.AiUsageDao
import com.example.nexusnews.data.local.datastore.ApiKeyDataStore
import com.example.nexusnews.data.remote.api.OpenRouterApi
import com.example.nexusnews.data.remote.model.*
import com.example.nexusnews.domain.ai.*
import io.mockk.*
import timber.log.Timber
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

class OpenRouterAiServiceTest {

    private val openRouterApi = mockk<OpenRouterApi>()
    private val apiKeyDataStore = mockk<ApiKeyDataStore>()
    private val aiUsageDao = mockk<AiUsageDao>()
    private lateinit var service: OpenRouterAiService

    @Before
    fun setup() {
        MockKAnnotations.init(this)
        Timber.plant(object : Timber.Tree() {
            override fun log(priority: Int, tag: String?, message: String, t: Throwable?) {
                println("LOG: ${message}")
            }
        })
        every { apiKeyDataStore.getOpenRouterApiKey() } returns "test-api-key"
        service = OpenRouterAiService(openRouterApi, apiKeyDataStore, aiUsageDao)
    }

    @Test
    fun `summarizeArticle calls API and returns success result`() = runBlocking {
        // Arrange
        val articleContent = "Test content"
        val maxLength = 100
        val expectedSummary = "Test summary"

        val response = ChatCompletionResponse(
            id = "test-id",
            choices = listOf(Choice(Message("assistant", expectedSummary), "stop")),
            usage = Usage(10, 20, 30),
            model = "test-model"
        )

        coEvery {
            openRouterApi.chatCompletion(
                authorization = any(),
                referer = any(),
                appTitle = any(),
                request = any()
            )
        } returns response

        coEvery { aiUsageDao.insertUsage(any()) } just Runs

        // Act
        val result = service.summarizeArticle(articleContent, maxLength)

        // Assert
        assertTrue("Expected success but got error: ${result.exceptionOrNull()}", result.isSuccess)
        assertEquals(expectedSummary, result.getOrNull())

        coVerify {
            openRouterApi.chatCompletion(
                authorization = "Bearer test-api-key",
                request = match { it.messages.any { msg -> msg.content.contains(articleContent) } }
            )
        }
    }

    @Test
    fun `extractKeyPoints calls API and returns parsed success result`() = runBlocking {
        // Arrange
        val articleContent = "Test content"
        val jsonResponse = """
            {
                "keyPoints": [
                    { "text": "Point 1", "importance": 0.9, "position": 10 }
                ],
                "summary": "Summary"
            }
        """.trimIndent()

        val response = ChatCompletionResponse(
            id = "test-id",
            choices = listOf(Choice(Message("assistant", jsonResponse), "stop")),
            usage = Usage(10, 20, 30),
            model = "test-model"
        )

        coEvery { openRouterApi.chatCompletion(any(), any(), any(), any()) } returns response
        coEvery { aiUsageDao.insertUsage(any()) } just Runs

        // Act
        val result = service.extractKeyPoints(articleContent, 1)

        // Assert
        assertTrue(result.isSuccess)
        val data = result.getOrNull()!!
        assertEquals(1, data.keyPoints.size)
        assertEquals("Point 1", data.keyPoints[0].text)
        assertEquals(0.9f, data.keyPoints[0].importance)
    }

    @Test
    fun `executeAiRequest retries with fallback model on failure`() = runBlocking {
        // Arrange
        val articleContent = "Test content"
        val primaryModelId = FreeAiModel.getDefault().id

        // Mock first call failing, second succeeding
        coEvery {
            openRouterApi.chatCompletion(any(), any(), any(), match { it.model == primaryModelId })
        } throws Exception("API Error")

        val successResponse = ChatCompletionResponse(
            id = "test-id",
            choices = listOf(Choice(Message("assistant", "Success"), "stop")),
            usage = Usage(10, 20, 30),
            model = "fallback-model"
        )

        coEvery {
            openRouterApi.chatCompletion(any(), any(), any(), match { it.model != primaryModelId })
        } returns successResponse

        coEvery { aiUsageDao.insertUsage(any()) } just Runs

        // Act
        val result = service.summarizeArticle(articleContent, 100)

        // Assert
        assertTrue("Expected success but got error: ${result.exceptionOrNull()}", result.isSuccess)
        assertEquals("Success", result.getOrNull())

        coVerify(exactly = 2) { openRouterApi.chatCompletion(any(), any(), any(), any()) }
    }
}
