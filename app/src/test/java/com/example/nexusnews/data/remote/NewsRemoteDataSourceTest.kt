package com.example.nexusnews.data.remote

import com.example.nexusnews.data.remote.api.NewsApiService
import com.example.nexusnews.data.remote.dto.NewsApiArticle
import com.example.nexusnews.data.remote.dto.NewsApiResponse
import com.example.nexusnews.data.remote.dto.NewsApiSource
import com.example.nexusnews.data.util.NetworkMonitor
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import retrofit2.Response

class NewsRemoteDataSourceTest {
    private lateinit var newsApiService: NewsApiService
    private lateinit var networkMonitor: NetworkMonitor
    private lateinit var newsRemoteDataSource: NewsRemoteDataSource

    @Before
    fun setup() {
        newsApiService = mockk()
        networkMonitor = mockk()

        // Mock network monitor to always return connected
        coEvery { networkMonitor.isCurrentlyConnected() } returns true

        newsRemoteDataSource = NewsRemoteDataSource(newsApiService, networkMonitor)
    }

    @Test
    fun `getTopHeadlines returns mapped articles on success`() =
        runTest {
            // Given
            val mockApiResponse =
                NewsApiResponse(
                    status = "ok",
                    totalResults = 1,
                    articles =
                        listOf(
                            NewsApiArticle(
                                source = NewsApiSource(id = "cnn", name = "CNN"),
                                author = "John Doe",
                                title = "Test Article",
                                description = "Test description",
                                url = "https://example.com/article",
                                urlToImage = "https://example.com/image.jpg",
                                publishedAt = "2025-11-14T12:00:00Z",
                                content = "Test content",
                            ),
                        ),
                )

            val mockResponse = Response.success(mockApiResponse)
            coEvery {
                newsApiService.getTopHeadlines(
                    country = "us",
                    category = null,
                    query = null,
                    pageSize = 20,
                    page = 1,
                )
            } returns mockResponse

            // When
            val result = newsRemoteDataSource.getTopHeadlines(country = "us")

            // Then
            assertEquals(1, result.size)
            val article = result[0]
            assertEquals("Test Article", article.title)
            assertEquals("Test description", article.description)
            assertEquals("https://example.com/article", article.url)
            assertEquals("CNN", article.source)
            assertEquals("John Doe", article.author)

            coVerify {
                newsApiService.getTopHeadlines(
                    country = "us",
                    category = null,
                    query = null,
                    pageSize = 20,
                    page = 1,
                )
            }
        }

    @Test
    fun `getTopHeadlines handles empty response`() =
        runTest {
            // Given
            val mockApiResponse =
                NewsApiResponse(
                    status = "ok",
                    totalResults = 0,
                    articles = emptyList(),
                )

            val mockResponse = Response.success(mockApiResponse)
            coEvery {
                newsApiService.getTopHeadlines(
                    country = "us",
                    category = null,
                    query = null,
                    pageSize = 20,
                    page = 1,
                )
            } returns mockResponse

            // When
            val result = newsRemoteDataSource.getTopHeadlines(country = "us")

            // Then
            assertTrue(result.isEmpty())

            coVerify {
                newsApiService.getTopHeadlines(
                    country = "us",
                    category = null,
                    query = null,
                    pageSize = 20,
                    page = 1,
                )
            }
        }

    @Test
    fun `searchArticles returns mapped articles on success`() =
        runTest {
            // Given
            val mockApiResponse =
                NewsApiResponse(
                    status = "ok",
                    totalResults = 1,
                    articles =
                        listOf(
                            NewsApiArticle(
                                source = NewsApiSource(id = null, name = "BBC News"),
                                author = null,
                                title = "Search Result Article",
                                description = "Search result description",
                                url = "https://bbc.com/article",
                                urlToImage = null,
                                publishedAt = "2025-11-14T10:00:00Z",
                                content = "Search content",
                            ),
                        ),
                )

            val mockResponse = Response.success(mockApiResponse)
            coEvery {
                newsApiService.getEverything(
                    query = "bitcoin",
                    sources = null,
                    from = null,
                    to = null,
                    language = null,
                    sortBy = "relevancy",
                    pageSize = 20,
                    page = 1,
                )
            } returns mockResponse

            // When
            val result = newsRemoteDataSource.searchArticles(query = "bitcoin", sortBy = "relevancy")

            // Then
            assertEquals(1, result.size)
            val article = result[0]
            assertEquals("Search Result Article", article.title)
            assertEquals("BBC News", article.source)

            coVerify {
                newsApiService.getEverything(
                    query = "bitcoin",
                    sources = null,
                    from = null,
                    to = null,
                    language = null,
                    sortBy = "relevancy",
                    pageSize = 20,
                    page = 1,
                )
            }
        }
}
