package com.example.nexusnews.data.scraper

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.jsoup.Jsoup
import org.jsoup.nodes.Document
import timber.log.Timber
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Service for scraping full article content from web pages.
 * Uses JSoup to extract article text from various news websites.
 */
@Singleton
class ArticleScraperService
    @Inject
    constructor() {
        /**
         * Fetches full article content from a URL.
         * @param url The article URL to scrape
         * @return The extracted article content, or null if scraping fails
         */
        suspend fun fetchFullContent(url: String): Result<String> =
            withContext(Dispatchers.IO) {
                try {
                    Timber.d("Fetching full content from: $url")

                    // Fetch the HTML document
                    val document: Document =
                        Jsoup
                            .connect(url)
                            .userAgent("Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36")
                            .timeout(10000)
                            .get()

                    // Extract article content using common selectors
                    val content = extractArticleContent(document)

                    if (content.isNotBlank()) {
                        Timber.d("Successfully extracted ${content.length} characters")
                        Result.success(content)
                    } else {
                        Timber.w("No content extracted from URL")
                        Result.failure(Exception("Could not extract article content"))
                    }
                } catch (e: Exception) {
                    Timber.e(e, "Failed to scrape article content")
                    Result.failure(e)
                }
            }

        /**
         * Extracts article content from HTML document using common patterns.
         */
        private fun extractArticleContent(document: Document): String {
            // Try common article content selectors in order of preference
            val selectors =
                listOf(
                    "article",
                    "[itemprop=articleBody]",
                    ".article-content",
                    ".article-body",
                    ".post-content",
                    ".entry-content",
                    ".content-body",
                    "main article",
                    "[role=article]",
                    ".story-body",
                )

            for (selector in selectors) {
                val element = document.select(selector).first()
                if (element != null) {
                    // Extract text, preserving paragraph breaks
                    val paragraphs = element.select("p")
                    if (paragraphs.isNotEmpty()) {
                        val text =
                            paragraphs
                                .joinToString("\n\n") { it.text() }
                                .trim()
                        if (text.length > 200) { // Minimum content length
                            Timber.d("Content extracted using selector: $selector")
                            return text
                        }
                    }
                }
            }

            // Fallback: get all paragraph text
            val allParagraphs = document.select("p")
            if (allParagraphs.size > 3) {
                val text =
                    allParagraphs
                        .joinToString("\n\n") { it.text() }
                        .trim()
                if (text.length > 200) {
                    Timber.d("Content extracted using fallback paragraph method")
                    return text
                }
            }

            return ""
        }
    }
