This is the revised **NexusNews AI PRD v2.0** with all major sections encapsulated in `<details>` and `<summary>` tags as requested.

---

# NexusNews AI - Comprehensive PRD v2.0

## Multi-Source & International News Aggregator with OpenRouter AI

---

<details>
<summary>1. Executive Summary (Revised)</summary>

**Project Name**: NexusNews AI
**Duration**: 10 weeks (50-60 hours)
**Tech Stack**: Kotlin, Jetpack Compose, MVVM, Clean Architecture, OpenRouter API, Room, Retrofit, Jsoup (web scraping)

### Key Differentiators from NexusChat

1. **Multi-Source News Aggregation**: Unified feed from API-based (NewsAPI, Guardian) + scraper-based (HBVL, GvA) sources
2. **OpenRouter Integration**: Single service with 100+ model selection (GPT-4, Claude, Gemini, Llama, etc.) vs. multi-provider management
3. **Localization**: Dutch/English language detection, multi-lingual summaries
4. **Domain-Specific Architecture**: News source adapters, article deduplication, source credibility scoring

### Primary Learning Goals

1. **Kotlin MVVM Mastery**: Clean Architecture with complex data flows (multiple sources → unified repository)
2. **OpenRouter AI**: Model selection strategies, cost-aware routing, streaming responses
3. **Web Scraping**: Jsoup for parsing news sites without APIs
4. **Production Patterns**: Rate limiting, caching strategies, error recovery, source health monitoring
</details>

<details>
<summary>2. Technical Architecture (Revised)</summary>

### 2.1 System Architecture Overview

```text
┌────────────────────────────────────────────────────────────────┐
│                      PRESENTATION LAYER                         │
│  ┌─────────────┐  ┌─────────────┐  ┌─────────────────────────┐│
│  │   Home Feed │  │  AI Settings│  │  Source Management      ││
│  │   Screen    │  │  Screen     │  │  Screen                 ││
│  └──────┬──────┘  └──────┬──────┘  └────────┬────────────────┘│
│         │                 │                   │                 │
│  ┌──────▼─────────────────▼───────────────────▼──────────────┐ │
│  │              ViewModels (MVVM)                             │ │
│  │  NewsViewModel │ AIViewModel │ SourceViewModel            │ │
│  └────────────────────────────────────────────────────────────┘ │
└────────────────────────────────────────────────────────────────┘
                              ↕ Use Cases
┌────────────────────────────────────────────────────────────────┐
│                       DOMAIN LAYER                              │
│  ┌───────────────────────────────────────────────────────────┐ │
│  │  Use Cases                                                 │ │
│  │  • GetAggregatedNewsUseCase                               │ │
│  │  • SummarizeWithOpenRouterUseCase                         │ │
│  │  • DetectLanguageUseCase                                  │ │
│  │  • DeduplicateArticlesUseCase                             │ │
│  └───────────────────────────────────────────────────────────┘ │
│  ┌───────────────────────────────────────────────────────────┐ │
│  │  Domain Models (Entities)                                 │ │
│  │  Article │ NewsSource │ AIModel │ Summary                 │ │
│  └───────────────────────────────────────────────────────────┘ │
│  ┌───────────────────────────────────────────────────────────┐ │
│  │  Repository Interfaces                                     │ │
│  │  NewsRepository │ AIRepository │ SourceRepository         │ │
│  └───────────────────────────────────────────────────────────┘ │
└────────────────────────────────────────────────────────────────┘
                              ↕ Data Sources
┌────────────────────────────────────────────────────────────────┐
│                        DATA LAYER                               │
│  ┌───────────────────────────────────────────────────────────┐ │
│  │  Repository Implementations                                │ │
│  │  NewsRepositoryImpl (aggregates all sources)              │ │
│  └───────────────────────────────────────────────────────────┘ │
│                                                                 │
│  ┌─────────────────┐  ┌──────────────────────────────────┐   │
│  │  Remote Sources │  │   Local Data Source              │   │
│  │  ┌────────────┐ │  │  ┌────────────┐  ┌────────────┐ │   │
│  │  │ NewsAPI    │ │  │  │ ArticleDao │  │ SourceDao  │ │   │
│  │  │ (Retrofit) │ │  │  │ (Room)     │  │ (Room)     │ │   │
│  │  └────────────┘ │  │  └────────────┘  └────────────┘ │   │
│  │  ┌────────────┐ │  │  ┌────────────┐  ┌────────────┐ │   │
│  │  │ HBVL       │ │  │  │ SummaryDao │  │ SourceCache│ │   │
│  │  │ Scraper    │ │  │  │ (Room)     │  │ (DataStore)│ │   │
│  │  └────────────┘ │  │  └────────────┘  └────────────┘ │   │
│  │  ┌────────────┐ │  └──────────────────────────────────┘   │
│  │  │ GvA        │ │                                          │
│  │  │ (Retrofit) │ │  ┌──────────────────────────────────┐   │
│  │  └────────────┘ │  │  OpenRouter Service Layer        │   │
│  │  ┌────────────┐ │  │  ┌────────────┐  ┌────────────┐ │   │
│  │  │ Guardian   │ │  │  │ OpenRouter │  │ Model      │ │   │
│  │  │ (Retrofit) │ │  │  │ API Client │  │ Selector   │ │   │
│  │  └────────────┘ │  │  │ (Retrofit) │  │ Strategy   │ │   │
│  └─────────────────┘  │  └────────────┘  └────────────┘ │   │
│                       │  ┌────────────┐  ┌────────────┐ │   │
│  ┌─────────────────┐  │  │ Prompt     │  │ Streaming  │ │   │
│  │  Source Adapter │  │  │ Builder    │  │ Handler    │ │   │
│  │  Factory        │  │  └────────────┘  └────────────┘ │   │
│  │  (Strategy)     │  └──────────────────────────────────┘   │
│  └─────────────────┘                                          │
└────────────────────────────────────────────────────────────────┘
```

### 2.2 News Source Architecture (Multi-Adapter Pattern)

```kotlin
// domain/model/NewsSource.kt
data class NewsSource(
    val id: String,              // "newsapi", "hbvl", "gva"
    val displayName: String,     // "Het Belang van Limburg"
    val type: SourceType,        // API or SCRAPER
    val language: Language,      // NL, EN, FR
    val country: String,         // "BE", "US"
    val category: List<Category>,
    val isEnabled: Boolean = true,
    val healthStatus: HealthStatus = HealthStatus.UNKNOWN,
    val logo: String? = null,    // URL to source logo
    val baseUrl: String
)

enum class SourceType { API, SCRAPER }
enum class Language { NL, EN, FR, MULTI }
enum class HealthStatus { HEALTHY, DEGRADED, DOWN, UNKNOWN }

// domain/repository/NewsSourceRepository.kt
interface NewsSourceRepository {
    fun getAvailableSources(): Flow<List<NewsSource>>
    suspend fun toggleSource(sourceId: String, enabled: Boolean)
    suspend fun checkSourceHealth(sourceId: String): HealthStatus
    fun getEnabledSources(): Flow<List<NewsSource>>
}

// data/source/NewsSourceAdapter.kt (Strategy Pattern)
interface NewsSourceAdapter {
    val sourceId: String
    val source: NewsSource

    suspend fun fetchArticles(
        category: String? = null,
        page: Int = 1,
        limit: Int = 20
    ): Result<List<Article>>

    suspend fun fetchArticleDetail(url: String): Result<ArticleDetail>
    suspend fun healthCheck(): HealthStatus
}

// data/source/adapters/NewsApiAdapter.kt
class NewsApiAdapter @Inject constructor(
    private val apiService: NewsApiService,
    @Named("newsapi_key") private val apiKey: String
) : NewsSourceAdapter {

    override val sourceId = "newsapi"
    override val source = NewsSource(
        id = "newsapi",
        displayName = "NewsAPI (International)",
        type = SourceType.API,
        language = Language.EN,
        country = "US",
        category = listOf(Category.GENERAL, Category.TECH, Category.BUSINESS),
        baseUrl = "https://newsapi.org"
    )

    override suspend fun fetchArticles(
        category: String?,
        page: Int,
        limit: Int
    ): Result<List<Article>> = withContext(Dispatchers.IO) {
        try {
            val response = apiService.getTopHeadlines(
                country = "us",
                category = category,
                page = page,
                pageSize = limit,
                apiKey = apiKey
            )

            val articles = response.articles.map { dto ->
                Article(
                    id = dto.url.hashCode().toString(),
                    title = dto.title,
                    description = dto.description,
                    content = dto.content,
                    url = dto.url,
                    imageUrl = dto.urlToImage,
                    sourceId = sourceId,
                    sourceName = dto.source.name,
                    author = dto.author,
                    publishedAt = dto.publishedAt.toInstant(),
                    language = Language.EN,
                    category = Category.fromString(category)
                )
            }
            Result.success(articles)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun healthCheck(): HealthStatus {
        return try {
            apiService.getTopHeadlines(country = "us", pageSize = 1, apiKey = apiKey)
            HealthStatus.HEALTHY
        } catch (e: Exception) {
            HealthStatus.DOWN
        }
    }
}

// data/source/adapters/HBVLScraperAdapter.kt
class HBVLScraperAdapter @Inject constructor(
    private val httpClient: OkHttpClient,
    private val htmlParser: HtmlParser
) : NewsSourceAdapter {

    override val sourceId = "hbvl"
    override val source = NewsSource(
        id = "hbvl",
        displayName = "Het Belang van Limburg",
        type = SourceType.SCRAPER,
        language = Language.NL,
        country = "BE",
        category = listOf(Category.LOCAL, Category.SPORTS),
        logo = "https://www.hbvl.be/static/images/logo.png",
        baseUrl = "https://www.hbvl.be"
    )

    override suspend fun fetchArticles(
        category: String?,
        page: Int,
        limit: Int
    ): Result<List<Article>> = withContext(Dispatchers.IO) {
        try {
            val url = when (category) {
                "tech" -> "https://www.hbvl.be/tech"
                "sports" -> "https://www.hbvl.be/sport"
                else -> "https://www.hbvl.be/nieuws"
            }

            val request = Request.Builder().url(url).build()
            val response = httpClient.newCall(request).execute()

            if (!response.isSuccessful) {
                return@withContext Result.failure(Exception("HTTP ${response.code}"))
            }

            val html = response.body?.string() ?: ""
            val document = Jsoup.parse(html)

            val articles = document.select("article.article-teaser").take(limit).map { element ->
                Article(
                    id = element.select("a").attr("href").hashCode().toString(),
                    title = element.select("h2, h3").text(),
                    description = element.select(".article-teaser__intro").text(),
                    content = "", // Fetched on detail view
                    url = source.baseUrl + element.select("a").attr("href"),
                    imageUrl = element.select("img").attr("src"),
                    sourceId = sourceId,
                    sourceName = "HBVL",
                    author = element.select(".article-teaser__author").text(),
                    publishedAt = parseHBVLDate(element.select("time").attr("datetime")),
                    language = Language.NL,
                    category = Category.fromString(category)
                )
            }

            Result.success(articles)
        } catch (e: Exception) {
            Timber.e(e, "HBVL scraping failed")
            Result.failure(e)
        }
    }

    override suspend fun fetchArticleDetail(url: String): Result<ArticleDetail> = withContext(Dispatchers.IO) {
        try {
            val request = Request.Builder().url(url).build()
            val response = httpClient.newCall(request).execute()
            val html = response.body?.string() ?: ""
            val document = Jsoup.parse(html)

            val content = document.select("div.article__body p")
                .joinToString("\n\n") { it.text() }

            val detail = ArticleDetail(
                fullContent = content,
                images = document.select("div.article__body img")
                    .map { it.attr("src") },
                tags = document.select("a.tag").map { it.text() }
            )

            Result.success(detail)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun healthCheck(): HealthStatus {
        return try {
            val request = Request.Builder()
                .url(source.baseUrl)
                .head()
                .build()
            val response = httpClient.newCall(request).execute()
            if (response.isSuccessful) HealthStatus.HEALTHY else HealthStatus.DEGRADED
        } catch (e: Exception) {
            HealthStatus.DOWN
        }
    }

    private fun parseHBVLDate(datetime: String): Instant {
        // Parse ISO date or custom format
        return try {
            Instant.parse(datetime)
        } catch (e: Exception) {
            Instant.now()
        }
    }
}

// data/source/adapters/GvaScraperAdapter.kt
class GvaScraperAdapter @Inject constructor(
    private val httpClient: OkHttpClient
) : NewsSourceAdapter {

    override val sourceId = "gva"
    override val source = NewsSource(
        id = "gva",
        displayName = "Gazet van Antwerpen",
        type = SourceType.SCRAPER,
        language = Language.NL,
        country = "BE",
        category = listOf(Category.LOCAL, Category.BUSINESS),
        logo = "https://www.gva.be/static/logo.png",
        baseUrl = "https://www.gva.be"
    )

    override suspend fun fetchArticles(
        category: String?,
        page: Int,
        limit: Int
    ): Result<List<Article>> = withContext(Dispatchers.IO) {
        try {
            val url = "https://www.gva.be/${category ?: "nieuws"}"
            val request = Request.Builder().url(url).build()
            val response = httpClient.newCall(request).execute()
            val html = response.body?.string() ?: ""
            val document = Jsoup.parse(html)

            // GvA uses different HTML structure
            val articles = document.select("div.teaser").take(limit).map { element ->
                Article(
                    id = element.select("a").attr("href").hashCode().toString(),
                    title = element.select("h3.teaser__title").text(),
                    description = element.select("p.teaser__intro").text(),
                    content = "",
                    url = source.baseUrl + element.select("a").attr("href"),
                    imageUrl = element.select("img.teaser__image").attr("src"),
                    sourceId = sourceId,
                    sourceName = "GvA",
                    author = null,
                    publishedAt = Instant.now(), // GvA may not expose dates in list
                    language = Language.NL,
                    category = Category.fromString(category)
                )
            }

            Result.success(articles)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun fetchArticleDetail(url: String): Result<ArticleDetail> {
        // Similar to HBVL but with GvA's HTML selectors
        // Implementation follows same pattern
        TODO("Implement GvA detail scraping")
    }

    override suspend fun healthCheck(): HealthStatus {
        return try {
            val request = Request.Builder().url(source.baseUrl).head().build()
            val response = httpClient.newCall(request).execute()
            if (response.isSuccessful) HealthStatus.HEALTHY else HealthStatus.DOWN
        } catch (e: Exception) {
            HealthStatus.DOWN
        }
    }
}

// data/source/NewsSourceAdapterFactory.kt
class NewsSourceAdapterFactory @Inject constructor(
    private val newsApiAdapter: Provider<NewsApiAdapter>,
    private val hbvlAdapter: Provider<HBVLScraperAdapter>,
    private val gvaAdapter: Provider<GvaScraperAdapter>,
    // Add Guardian, other sources...
) {
    fun create(sourceId: String): NewsSourceAdapter {
        return when (sourceId) {
            "newsapi" -> newsApiAdapter.get()
            "hbvl" -> hbvlAdapter.get()
            "gva" -> gvaAdapter.get()
            else -> throw IllegalArgumentException("Unknown source: $sourceId")
        }
    }

    fun getAllAdapters(): List<NewsSourceAdapter> {
        return listOf(
            newsApiAdapter.get(),
            hbvlAdapter.get(),
            gvaAdapter.get()
        )
    }
}
```

### 2.3 OpenRouter AI Architecture (Simplified, Model-Centric)

```kotlin
// domain/model/AIModel.kt
data class AIModel(
    val id: String,                    // "openai/gpt-4o-mini"
    val displayName: String,           // "GPT-4o Mini"
    val provider: String,              // "OpenAI"
    val contextLength: Int,            // 128000
    val costPer1kTokens: CostInfo,
    val supportsStreaming: Boolean = true,
    val bestForTask: List<AITask> = emptyList()
)

data class CostInfo(
    val inputCostUSD: Double,   // 0.00015
    val outputCostUSD: Double   // 0.0006
)

enum class AITask {
    SUMMARIZATION,      // Best: GPT-4o-mini, Claude Haiku
    SENTIMENT,          // Best: Gemini Flash
    TRANSLATION,        // Best: GPT-4
    EXTRACTION          // Best: Claude Sonnet
}

// data/ai/OpenRouterService.kt
interface OpenRouterService {
    @POST("api/v1/chat/completions")
    suspend fun createChatCompletion(
        @Header("Authorization") authorization: String,
        @Header("HTTP-Referer") referer: String = "https://NexusNews.app",
        @Header("X-Title") appTitle: String = "NexusNews AI",
        @Body request: OpenRouterRequest
    ): OpenRouterResponse

    @GET("api/v1/models")
    suspend fun getAvailableModels(
        @Header("Authorization") authorization: String
    ): ModelsResponse
}

data class OpenRouterRequest(
    val model: String,              // "openai/gpt-4o-mini"
    val messages: List<Message>,
    val max_tokens: Int? = null,
    val temperature: Double = 0.3,
    val stream: Boolean = false,
    val top_p: Double? = null
)

data class Message(
    val role: String,       // "system", "user", "assistant"
    val content: String
)

data class OpenRouterResponse(
    val id: String,
    val model: String,
    val choices: List<Choice>,
    val usage: Usage
)

data class Choice(
    val message: Message,
    val finish_reason: String
)

data class Usage(
    val prompt_tokens: Int,
    val completion_tokens: Int,
    val total_tokens: Int
)

// domain/repository/AIRepository.kt
interface AIRepository {
    suspend fun setApiKey(key: String): Result<Unit>
    suspend fun getApiKey(): String?
    suspend fun validateApiKey(key: String): Result<Boolean>
    suspend fun getAvailableModels(): Flow<List<AIModel>>
    suspend fun getSelectedModel(task: AITask): AIModel
    suspend fun setSelectedModel(task: AITask, modelId: String)
    fun getUsageStats(): Flow<UsageStats>
}

data class UsageStats(
    val totalTokensUsed: Long,
    val estimatedCostUSD: Double,
    val requestsToday: Int,
    val lastResetDate: Instant
)

// data/ai/OpenRouterClient.kt
class OpenRouterClient @Inject constructor(
    private val service: OpenRouterService,
    private val aiRepository: AIRepository,
    private val usageTracker: UsageTracker
) {

    suspend fun summarize(
        text: String,
        language: Language,
        maxTokens: Int = 150,
        modelOverride: String? = null
    ): Result<Summary> {
        return try {
            val apiKey = aiRepository.getApiKey()
                ?: return Result.failure(Exception("No API key configured"))

            val model = modelOverride
                ?: aiRepository.getSelectedModel(AITask.SUMMARIZATION).id

            val systemPrompt = buildSummarizationPrompt(language)
            val request = OpenRouterRequest(
                model = model,
                messages = listOf(
                    Message("system", systemPrompt),
                    Message("user", "Summarize this article:\n\n$text")
                ),
                max_tokens = maxTokens,
                temperature = 0.3
            )

            val response = service.createChatCompletion(
                authorization = "Bearer $apiKey",
                request = request
            )

            // Track usage
            usageTracker.recordUsage(response.usage, model)

            val summaryText = response.choices.first().message.content
            Result.success(Summary(
                text = summaryText,
                modelUsed = model,
                tokensUsed = response.usage.total_tokens,
                language = language
            ))
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun analyzeSentiment(
        text: String,
        language: Language
    ): Result<Sentiment> {
        val apiKey = aiRepository.getApiKey() ?: return Result.failure(Exception("No API key"))
        val model = aiRepository.getSelectedModel(AITask.SENTIMENT).id

        val systemPrompt = """
            Analyze the sentiment of news articles.
            Respond with ONLY one word: POSITIVE, NEUTRAL, or NEGATIVE.
            ${if (language == Language.NL) "The article is in Dutch." else ""}
        """.trimIndent()

        val request = OpenRouterRequest(
            model = model,
            messages = listOf(
                Message("system", systemPrompt),
                Message("user", text)
            ),
            max_tokens = 10,
            temperature = 0.1
        )

        return try {
            val response = service.createChatCompletion(
                authorization = "Bearer $apiKey",
                request = request
            )

            usageTracker.recordUsage(response.usage, model)

            val sentimentText = response.choices.first().message.content.trim().uppercase()
            val sentiment = when {
                sentimentText.contains("POSITIVE") -> Sentiment.POSITIVE
                sentimentText.contains("NEGATIVE") -> Sentiment.NEGATIVE
                else -> Sentiment.NEUTRAL
            }

            Result.success(sentiment)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun translate(
        text: String,
        fromLanguage: Language,
        toLanguage: Language
    ): Result<String> {
        val apiKey = aiRepository.getApiKey() ?: return Result.failure(Exception("No API key"))
        val model = aiRepository.getSelectedModel(AITask.TRANSLATION).id

        val systemPrompt = """
            You are a professional translator.
            Translate from ${fromLanguage.name} to ${toLanguage.name}.
            Preserve the tone and meaning. Output ONLY the translation.
        """.trimIndent()

        val request = OpenRouterRequest(
            model = model,
            messages = listOf(
                Message("system", systemPrompt),
                Message("user", text)
            ),
            max_tokens = (text.length * 1.5).toInt(),
            temperature = 0.2
        )

        return try {
            val response = service.createChatCompletion(
                authorization = "Bearer $apiKey",
                request = request
            )

            usageTracker.recordUsage(response.usage, model)
            Result.success(response.choices.first().message.content)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    private fun buildSummarizationPrompt(language: Language): String {
        return when (language) {
            Language.NL -> """
                Je bent een Nederlandse nieuwssamenvatting expert.
                Creëer een beknopte samenvatting van 2-3 zinnen die de kernpunten vastlegt.
                Wees objectief en feitelijk. Schrijf in het Nederlands.
            """.trimIndent()
            Language.EN -> """
                You are a news summarization expert.
                Create a concise 2-3 sentence summary capturing key points.
                Be objective and factual.
            """.trimIndent()
            else -> """
                Create a concise 2-3 sentence summary in the original language.
                Be objective and factual.
            """.trimIndent()
        }
    }
}

// data/ai/UsageTracker.kt
class UsageTracker @Inject constructor(
    private val usageDao: UsageDao,
    private val modelsRepository: ModelsRepository
) {
    suspend fun recordUsage(usage: Usage, modelId: String) = withContext(Dispatchers.IO) {
        val model = modelsRepository.getModel(modelId)
        val cost = calculateCost(usage, model)

        val record = UsageRecord(
            timestamp = Instant.now(),
            modelId = modelId,
            promptTokens = usage.prompt_tokens,
            completionTokens = usage.completion_tokens,
            totalTokens = usage.total_tokens,
            costUSD = cost
        )

        usageDao.insertUsage(record)
    }

    private fun calculateCost(usage: Usage, model: AIModel?): Double {
        if (model == null) return 0.0

        val inputCost = (usage.prompt_tokens / 1000.0) * model.costPer1kTokens.inputCostUSD
        val outputCost = (usage.completion_tokens / 1000.0) * model.costPer1kTokens.outputCostUSD

        return inputCost + outputCost
    }

    suspend fun getUsageStats(): UsageStats {
        val today = Instant.now().truncatedTo(ChronoUnit.DAYS)
        val records = usageDao.getUsageSince(today)

        return UsageStats(
            totalTokensUsed = records.sumOf { it.totalTokens.toLong() },
            estimatedCostUSD = records.sumOf { it.costUSD },
            requestsToday = records.size,
            lastResetDate = today
        )
    }
}

// data/ai/ModelSelector.kt
class ModelSelector @Inject constructor(
    private val modelsRepository: ModelsRepository,
    private val preferencesRepository: PreferencesRepository
) {
    suspend fun selectBestModel(
        task: AITask,
        prioritizeCost: Boolean = false
    ): AIModel {
        val userPreference = preferencesRepository.getPreferredModel(task)
        if (userPreference != null) {
            return modelsRepository.getModel(userPreference)!!
        }

        val availableModels = modelsRepository.getModelsForTask(task)

        return if (prioritizeCost) {
            // Select cheapest model
            availableModels.minByOrNull {
                it.costPer1kTokens.inputCostUSD + it.costPer1kTokens.outputCostUSD
            }!!
        } else {
            // Select balanced model (good quality/cost ratio)
            when (task) {
                AITask.SUMMARIZATION -> availableModels.first { it.id.contains("gpt-4o-mini") }
                AITask.SENTIMENT -> availableModels.first { it.id.contains("gemini-flash") }
                AITask.TRANSLATION -> availableModels.first { it.id.contains("gpt-4") }
                AITask.EXTRACTION -> availableModels.first { it.id.contains("claude") }
            }
        }
    }
}
```
</details>

<details>
<summary>3. Project Structure (Complete File Tree)</summary>

```text
app/
├── src/
│   ├── main/
│   │   ├── java/com/yourname/NexusNews/
│   │   │   ├── NexusNewsApplication.kt
│   │   │   │
│   │   │   ├── di/                          # Dependency Injection
│   │   │   │   ├── AppModule.kt
│   │   │   │   ├── DatabaseModule.kt
│   │   │   │   ├── NetworkModule.kt
│   │   │   │   ├── RepositoryModule.kt
│   │   │   │   ├── SourceModule.kt          # Binds all news adapters
│   │   │   │   └── AIModule.kt
│   │   │   │
│   │   │   ├── data/
│   │   │   │   ├── local/
│   │   │   │   │   ├── AppDatabase.kt
│   │   │   │   │   ├── dao/
│   │   │   │   │   │   ├── ArticleDao.kt
│   │   │   │   │   │   ├── SourceDao.kt
│   │   │   │   │   │   ├── SummaryDao.kt
│   │   │   │   │   │   └── UsageDao.kt
│   │   │   │   │   ├── entity/
│   │   │   │   │   │   ├── ArticleEntity.kt
│   │   │   │   │   │   ├── SourceEntity.kt
│   │   │   │   │   │   ├── SummaryEntity.kt
│   │   │   │   │   │   └── UsageEntity.kt
│   │   │   │   │   └── datastore/
│   │   │   │   │       ├── PreferencesDataStore.kt
│   │   │   │   │       └── EncryptedKeyStore.kt
│   │   │   │   │
│   │   │   │   ├── remote/
│   │   │   │   │   ├── api/
│   │   │   │   │   │   ├── NewsApiService.kt
│   │   │   │   │   │   ├── GuardianApiService.kt
│   │   │   │   │   │   └── OpenRouterService.kt
│   │   │   │   │   ├── dto/
│   │   │   │   │   │   ├── NewsApiResponse.kt
│   │   │   │   │   │   ├── GuardianResponse.kt
│   │   │   │   │   │   └── OpenRouterDto.kt
│   │   │   │   │   └── interceptor/
│   │   │   │   │       ├── AuthInterceptor.kt
│   │   │   │   │       ├── RateLimitInterceptor.kt
│   │   │   │   │       └── LoggingInterceptor.kt
│   │   │   │   │
│   │   │   │   ├── scraper/
│   │   │   │   │   ├── HtmlParser.kt
│   │   │   │   │   ├── ScraperUtils.kt
│   │   │   │   │   └── DateParser.kt
│   │   │   │   │
│   │   │   │   ├── source/
│   │   │   │   │   ├── NewsSourceAdapter.kt        # Interface
│   │   │   │   │   ├── NewsSourceAdapterFactory.kt
│   │   │   │   │   └── adapters/
│   │   │   │   │       ├── NewsApiAdapter.kt
│   │   │   │   │       ├── GuardianAdapter.kt
│   │   │   │   │       ├── HBVLScraperAdapter.kt
│   │   │   │   │       ├── GvaScraperAdapter.kt
│   │   │   │   │       └── NieuwsbladScraperAdapter.kt
│   │   │   │   │
│   │   │   │   ├── ai/
│   │   │   │   │   ├── OpenRouterClient.kt
│   │   │   │   │   ├── PromptBuilder.kt
│   │   │   │   │   ├── ModelSelector.kt
│   │   │   │   │   ├── UsageTracker.kt
│   │   │   │   │   └── StreamingHandler.kt         # For future streaming
│   │   │   │   │
│   │   │   │   ├── repository/
│   │   │   │   │   ├── NewsRepositoryImpl.kt
│   │   │   │   │   ├── AIRepositoryImpl.kt
│   │   │   │   │   ├── SourceRepositoryImpl.kt
│   │   │   │   │   └── UserPreferencesRepositoryImpl.kt
│   │   │   │   │
│   │   │   │   ├── worker/
│   │   │   │   │   ├── SyncNewsWorker.kt
│   │   │   │   │   ├── SourceHealthWorker.kt
│   │   │   │   │   └── PrefetchSummariesWorker.kt
│   │   │   │   │
│   │   │   │   └── mapper/
│   │   │   │       ├── ArticleMapper.kt
│   │   │   │       ├── SourceMapper.kt
│   │   │   │       └── DomainMapper.kt
│   │   │   │
│   │   │   ├── domain/
│   │   │   │   ├── model/
│   │   │   │   │   ├── Article.kt
│   │   │   │   │   ├── ArticleDetail.kt
│   │   │   │   │   ├── NewsSource.kt
│   │   │   │   │   ├── Category.kt
│   │   │   │   │   ├── Language.kt
│   │   │   │   │   ├── Summary.kt
│   │   │   │   │   ├── Sentiment.kt
│   │   │   │   │   ├── AIModel.kt
│   │   │   │   │   ├── UsageStats.kt
│   │   │   │   │   └── UserPreferences.kt
│   │   │   │   │
│   │   │   │   ├── repository/                  # Interfaces only
│   │   │   │   │   ├── NewsRepository.kt
│   │   │   │   │   ├── AIRepository.kt
│   │   │   │   │   ├── SourceRepository.kt
│   │   │   │   │   └── UserPreferencesRepository.kt
│   │   │   │   │
│   │   │   │   └── usecase/
│   │   │   │       ├── news/
│   │   │   │       │   ├── GetAggregatedNewsUseCase.kt
│   │   │   │       │   ├── GetArticleDetailUseCase.kt
│   │   │   │       │   ├── SearchArticlesUseCase.kt
│   │   │   │       │   ├── ToggleFavoriteUseCase.kt
│   │   │   │       │   └── DeduplicateArticlesUseCase.kt
│   │   │   │       ├── ai/
│   │   │   │       │   ├── SummarizeArticleUseCase.kt
│   │   │   │       │   ├── AnalyzeSentimentUseCase.kt
│   │   │   │       │   ├── TranslateArticleUseCase.kt
│   │   │   │       │   └── ExtractTopicsUseCase.kt
│   │   │   │       ├── source/
│   │   │   │       │   ├── GetAvailableSourcesUseCase.kt
│   │   │   │       │   ├── ToggleSourceUseCase.kt
│   │   │   │       │   └── CheckSourceHealthUseCase.kt
│   │   │   │       └── preferences/
│   │   │   │           ├── SaveUserPreferencesUseCase.kt
│   │   │   │           └── GetUserPreferencesUseCase.kt
│   │   │   │
│   │   │   ├── presentation/
│   │   │   │   ├── MainActivity.kt
│   │   │   │   ├── navigation/
│   │   │   │   │   ├── NavGraph.kt
│   │   │   │   │   ├── Screen.kt
│   │   │   │   │   └── NavigationActions.kt
│   │   │   │   │
│   │   │   │   ├── theme/
│   │   │   │   │   ├── Color.kt
│   │   │   │   │   ├── Type.kt
│   │   │   │   │   └── Theme.kt
│   │   │   │   │
│   │   │   │   ├── common/
│   │   │   │   │   ├── composables/
│   │   │   │   │   │   ├── ErrorView.kt
│   │   │   │   │   │   ├── LoadingIndicator.kt
│   │   │   │   │   │   ├── EmptyState.kt
│   │   │   │   │   │   ├── ArticleCard.kt
│   │   │   │   │   │   ├── SourceBadge.kt
│   │   │   │   │   │   ├── SentimentIndicator.kt
│   │   │   │   │   │   └── AIGeneratedBadge.kt
│   │   │   │   │   └── util/
│   │   │   │   │       ├── DateFormatter.kt
│   │   │   │   │       └── TextUtils.kt
│   │   │   │   │
│   │   │   │   ├── home/
│   │   │   │   │   ├── HomeScreen.kt
│   │   │   │   │   ├── HomeViewModel.kt
│   │   │   │   │   ├── HomeState.kt
│   │   │   │   │   ├── HomeEvent.kt
│   │   │   │   │   └── components/
│   │   │   │   │       ├── NewsFeed.kt
│   │   │   │   │       ├── CategoryTabs.kt
│   │   │   │   │       └── SourceFilter.kt
│   │   │   │   │
│   │   │   │   ├── article/
│   │   │   │   │   ├── ArticleDetailScreen.kt
│   │   │   │   │   ├── ArticleDetailViewModel.kt
│   │   │   │   │   ├── ArticleDetailState.kt
│   │   │   │   │   └── components/
│   │   │   │   │       ├── ArticleHeader.kt
│   │   │   │   │       ├── ArticleContent.kt
│   │   │   │   │       ├── AISummarySection.kt
│   │   │   │   │       ├── TranslationSection.kt
│   │   │   │   │       └── RelatedArticles.kt
│   │   │   │   │
│   │   │   │   ├── search/
│   │   │   │   │   ├── SearchScreen.kt
│   │   │   │   │   ├── SearchViewModel.kt
│   │   │   │   │   └── SearchState.kt
│   │   │   │   │
│   │   │   │   ├── favorites/
│   │   │   │   │   ├── FavoritesScreen.kt
│   │   │   │   │   ├── FavoritesViewModel.kt
│   │   │   │   │   └── FavoritesState.kt
│   │   │   │   │
│   │   │   │   ├── sources/
│   │   │   │   │   ├── SourcesScreen.kt
│   │   │   │   │   ├── SourcesViewModel.kt
│   │   │   │   │   ├── SourcesState.kt
│   │   │   │   │   └── components/
│   │   │   │   │       ├── SourceCard.kt
│   │   │   │   │       └── HealthIndicator.kt
│   │   │   │   │
│   │   │   │   ├── settings/
│   │   │   │   │   ├── SettingsScreen.kt
│   │   │   │   │   ├── SettingsViewModel.kt
│   │   │   │   │   └── sections/
│   │   │   │   │       ├── AISettingsSection.kt
│   │   │   │   │       ├── ModelSelectionSection.kt
│   │   │   │   │       ├── UsageStatsSection.kt
│   │   │   │   │       └── PreferencesSection.kt
│   │   │   │   │
│   │   │   │   └── onboarding/
│   │   │   │       ├── OnboardingScreen.kt
│   │   │   │       ├── OnboardingViewModel.kt
│   │   │   │       └── pages/
│   │   │   │           ├── WelcomePage.kt
│   │   │   │           ├── SourceSelectionPage.kt
│   │   │   │           ├── CategorySelectionPage.kt
│   │   │   │           └── AISetupPage.kt
│   │   │   │
│   │   │   └── util/
│   │   │       ├── Constants.kt
│   │   │       ├── Extensions.kt
│   │   │       ├── NetworkMonitor.kt
│   │   │       └── Logger.kt
│   │   │
│   │   ├── res/
│   │   │   ├── values/
│   │   │   │   ├── strings.xml
│   │   │   │   ├── strings_nl.xml            # Dutch translations
│   │   │   │   └── colors.xml
│   │   │   ├── drawable/
│   │   │   └── mipmap/
│   │   │
│   │   └── AndroidManifest.xml
│   │
│   ├── test/                                  # Unit tests
│   │   └── java/com/yourname/NexusNews/
│   │       ├── domain/
│   │       │   └── usecase/
│   │       │       ├── GetAggregatedNewsUseCaseTest.kt
│   │       │       └── SummarizeArticleUseCaseTest.kt
│   │       ├── data/
│   │       │   ├── repository/
│   │       │   │   └── NewsRepositoryImplTest.kt
│   │       │   └── source/
│   │       │       ├── HBVLScraperTest.kt
│   │       │       └── NewsApiAdapterTest.kt
│   │       └── presentation/
│   │           └── home/
│   │               └── HomeViewModelTest.kt
│   │
│   └── androidTest/                           # Instrumentation tests
│       └── java/com/yourname/NexusNews/
│           ├── data/
│           │   └── local/
│           │       └── ArticleDaoTest.kt
│           └── ui/
│               └── HomeScreenTest.kt
│
├── build.gradle.kts
├── gradle.properties
├── local.properties                           # API keys (gitignored)
├── proguard-rules.pro
└── README.md
```
</details>

<details>
<summary>4. Detailed Implementation Plan (Week by Week)</summary>

### **Week 1: Foundation & Multi-Source Setup (10 hrs)** ✅ COMPLETED

#### Goals

- Project scaffolding with Clean Architecture
- Implement 2 news sources (NewsAPI + HBVL scraper)
- Basic feed UI with Compose
- Room database setup

#### Tasks

**Day 1-2: Project Setup (3 hrs)** ✅

1. Create Android Studio project with Kotlin & Compose
2. Add dependencies in `build.gradle.kts`:

```kotlin
dependencies {
    // Core
    implementation("androidx.core:core-ktx:1.12.0")
    implementation("androidx.lifecycle:lifecycle-runtime-ktx:2.7.0")

    // Compose
    implementation(platform("androidx.compose:compose-bom:2024.01.00"))
    implementation("androidx.compose.ui:ui")
    implementation("androidx.compose.material3:material3")
    implementation("androidx.compose.ui:ui-tooling-preview")
    implementation("androidx.activity:activity-compose:1.8.2")

    // Navigation
    implementation("androidx.navigation:navigation-compose:2.7.6")

    // Hilt (DI)
    implementation("com.google.dagger:hilt-android:2.50")
    kapt("com.google.dagger:hilt-compiler:2.50")
    implementation("androidx.hilt:hilt-navigation-compose:1.1.0")

    // Room
    implementation("androidx.room:room-runtime:2.6.1")
    implementation("androidx.room:room-ktx:2.6.1")
    kapt("androidx.room:room-compiler:2.6.1")

    // Retrofit + OkHttp
    implementation("com.squareup.retrofit2:retrofit:2.9.0")
    implementation("com.squareup.retrofit2:converter-moshi:2.9.0")
    implementation("com.squareup.okhttp3:okhttp:4.12.0")
    implementation("com.squareup.okhttp3:logging-interceptor:4.12.0")

    // Moshi (JSON)
    implementation("com.squareup.moshi:moshi:1.15.0")
    implementation("com.squareup.moshi:moshi-kotlin:1.15.0")
    kapt("com.squareup.moshi:moshi-kotlin-codegen:1.15.0")

    // Jsoup (Web scraping)
    implementation("org.jsoup:jsoup:1.17.2")

    // Coil (Image loading)
    implementation("io.coil-kt:coil-compose:2.5.0")

    // DataStore
    implementation("androidx.datastore:datastore-preferences:1.0.0")

    // Encrypted SharedPreferences
    implementation("androidx.security:security-crypto:1.1.0-alpha06")

    // WorkManager
    implementation("androidx.work:work-runtime-ktx:2.9.0")

    // Timber (Logging)
    implementation("com.jakewharton.timber:timber:5.0.1")

    // Testing
    testImplementation("junit:junit:4.13.2")
    testImplementation("org.jetbrains.kotlinx:kotlinx-coroutines-test:1.7.3")
    testImplementation("io.mockk:mockk:1.13.9")
    testImplementation("app.cash.turbine:turbine:1.0.0")
    androidTestImplementation("androidx.test.ext:junit:1.1.5")
    androidTestImplementation("androidx.compose.ui:ui-test-junit4")
}
```

3. Create folder structure (as per file tree above)
4. Setup Hilt in `NexusNewsApplication.kt`:

```kotlin
@HiltAndroidApp
class NexusNewsApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        if (BuildConfig.DEBUG) {
            Timber.plant(Timber.DebugTree())
        }
    }
}
```

**Day 3: NewsAPI Integration (2 hrs)** ✅ COMPLETED

1. Create `NewsApiService.kt` interface
2. Implement `NewsApiAdapter.kt`
3. Create DTO models for NewsAPI response
4. Setup Retrofit in `NetworkModule.kt`
5. Test API calls in a unit test

**Day 4: HBVL Scraper (3 hrs)**

1. Research HBVL HTML structure (use browser DevTools)
2. Implement `HBVLScraperAdapter.kt` with Jsoup
3. Create `HtmlParser.kt` utility class
4. Handle edge cases (missing images, dates)
5. Add rate limiting to avoid being blocked

**Day 5: Repository & Database (2 hrs)**

1. Create `ArticleEntity.kt` and `ArticleDao.kt`
2. Setup `AppDatabase.kt`
3. Implement `NewsRepositoryImpl.kt` that:
   - Aggregates results from both sources
   - Removes duplicates (by URL or title similarity)
   - Caches in Room
4. Create `GetAggregatedNewsUseCase.kt`

**Day 6: Basic UI (2 hrs)**

1. Create `HomeScreen.kt` with LazyColumn
2. Implement `ArticleCard.kt` composable
3. Build `HomeViewModel.kt`:

```kotlin
@HiltViewModel
class HomeViewModel @Inject constructor(
    private val getAggregatedNewsUseCase: GetAggregatedNewsUseCase
) : ViewModel() {

    private val _state = MutableStateFlow<HomeState>(HomeState.Loading)
    val state: StateFlow<HomeState> = _state.asStateFlow()

    private val _selectedSources = MutableStateFlow<Set<String>>(setOf("newsapi", "hbvl"))
    val selectedSources: StateFlow<Set<String>> = _selectedSources.asStateFlow()

    init {
        loadNews()
    }

    fun loadNews(refresh: Boolean = false) {
        viewModelScope.launch {
            _state.value = HomeState.Loading

            getAggregatedNewsUseCase(
                sourceIds = _selectedSources.value.toList(),
                forceRefresh = refresh
            )
                .catch { e ->
                    _state.value = HomeState.Error(e.message ?: "Unknown error")
                }
                .collect { result ->
                    _state.value = result.fold(
                        onSuccess = { articles -> HomeState.Success(articles) },
                        onFailure = { error -> HomeState.Error(error.message ?: "Failed") }
                    )
                }
        }
    }

    fun toggleSource(sourceId: String) {
        _selectedSources.value = if (sourceId in _selectedSources.value) {
            _selectedSources.value - sourceId
        } else {
            _selectedSources.value + sourceId
        }
        loadNews(refresh = true)
    }
}

sealed interface HomeState {
    data object Loading : HomeState
    data class Success(val articles: List<Article>) : HomeState
    data class Error(val message: String) : HomeState
}
```

4. Wire up navigation in `MainActivity.kt`

**Deliverables:**

- App displays articles from NewsAPI and HBVL
- Pull-to-refresh functionality
- Source toggle chips (e.g., "NewsAPI", "HBVL")
- Offline support (cached articles from Room)

---

### **Week 2: OpenRouter Integration & AI Settings (8 hrs)**

#### Goals

- Integrate OpenRouter API
- Build AI key management UI
- Implement basic summarization
- Add model selection

#### Tasks

**Day 1: OpenRouter Setup (2 hrs)**

1. Create OpenRouter account and get API key
2. Implement `OpenRouterService.kt` Retrofit interface
3. Create `OpenRouterClient.kt` wrapper class
4. Test API call with a simple summary request
5. Add error handling for common issues (401, 429, 500)

**Day 2: Key Management (2 hrs)**

1. Implement `EncryptedKeyStore.kt` using Android Keystore
2. Create `AIRepository.kt` and `AIRepositoryImpl.kt`
3. Build key validation logic (test API call to verify key)
4. Setup DataStore for storing selected models per task

**Day 3: AI Settings UI (2 hrs)**

1. Create `SettingsScreen.kt` with sections:
   - API Key input field (password-protected)
   - "Validate Key" button with loading state
   - Current usage stats display
2. Implement `SettingsViewModel.kt`:

```kotlin
@HiltViewModel
class SettingsViewModel @Inject constructor(
    private val aiRepository: AIRepository,
    private val usageTracker: UsageTracker
) : ViewModel() {

    val usageStats = usageTracker.getUsageStats()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), UsageStats.empty())

    private val _keyValidationState = MutableStateFlow<KeyValidationState>(KeyValidationState.Idle)
    val keyValidationState = _keyValidationState.asStateFlow()

    fun saveApiKey(key: String) {
        viewModelScope.launch {
            _keyValidationState.value = KeyValidationState.Validating

            aiRepository.validateApiKey(key)
                .onSuccess {
                    aiRepository.setApiKey(key)
                    _keyValidationState.value = KeyValidationState.Valid
                }
                .onFailure { error ->
                    _keyValidationState.value = KeyValidationState.Invalid(error.message ?: "Failed")
                }
        }
    }
}

sealed interface KeyValidationState {
    data object Idle : KeyValidationState
    data object Validating : KeyValidationState
    data object Valid : KeyValidationState
    data class Invalid(val message: String) : KeyValidationState
}
```

3. Add animations for validation success/failure

**Day 4: Model Selection UI (2 hrs)**

1. Fetch available models from OpenRouter API
2. Create `ModelSelectionSection.kt` with grouped lists:
   - Fast & Cheap (GPT-4o-mini, Gemini Flash)
   - Balanced (Claude Haiku, Mixtral)
   - Premium (GPT-4, Claude Sonnet)
3. Show cost per 1K tokens for each model
4. Allow users to set different models for different tasks:
   - Summarization → Fast model
   - Translation → Premium model
5. Implement `ModelSelector.kt` logic

**Deliverables:**

- Working OpenRouter integration
- Secure API key storage
- Model selection UI with cost info
- Key validation with user feedback

---

### **Week 3: AI Summarization & Language Detection (8 hrs)**

#### Goals

- Implement article summarization
- Add language detection for Dutch/English
- Build multilingual prompts
- Cache AI summaries

#### Tasks

**Day 1-2: Summarization Core (3 hrs)**

1. Create `SummarizeArticleUseCase.kt`:

```kotlin
class SummarizeArticleUseCase @Inject constructor(
    private val openRouterClient: OpenRouterClient,
    private val summaryDao: SummaryDao,
    private val languageDetector: LanguageDetector
) {
    suspend operator fun invoke(article: Article): Result<Summary> {
        // Check cache
        summaryDao.getSummary(article.id)?.let {
            return Result.success(it.toDomainModel())
        }

        // Detect language
        val language = languageDetector.detect(article.title + " " + article.description)

        // Generate summary with OpenRouter
        return openRouterClient.summarize(
            text = article.content ?: article.description,
            language = language,
            maxTokens = 150
        ).onSuccess { summary ->
            // Cache result
            summaryDao.insertSummary(summary.toEntity(article.id))
        }
    }
}
```

2. Implement `LanguageDetector.kt`:

```kotlin
class LanguageDetector {
    private val dutchKeywords = setOf(
        "het", "de", "een", "van", "in", "op", "voor", "met", "zijn", "als"
    )

    fun detect(text: String): Language {
        val words = text.lowercase().split("\\s+".toRegex()).take(50)
        val dutchCount = words.count { it in dutchKeywords }

        return if (dutchCount > 5) Language.NL else Language.EN
    }
}
```

3. Build `PromptBuilder.kt` with templates for NL/EN

**Day 3: UI Integration (2 hrs)**

1. Add "AI Summary" button to `ArticleCard.kt`
2. Implement expandable summary section with animation
3. Show loading skeleton while generating
4. Display model used and token count

**Day 4: Batch Summarization (2 hrs)**

1. Add "Summarize All" feature for feed
2. Implement batch processing with progress indicator
3. Add rate limiting (max 5 requests/minute to avoid OpenRouter limits)
4. Show cost estimate before batch operation

**Day 5: Error Handling & Fallback (1 hr)**

1. Handle OpenRouter errors gracefully:
   - 401: Show "Invalid API Key" prompt
   - 429: Rate limit → Show wait time
   - 500: Retry with exponential backoff
2. Implement local fallback (extractive summarization) if API fails

**Deliverables:**

- Working summarization with NL/EN support
- Cached summaries for cost efficiency
- Batch processing capability
- Robust error handling

---

### **Week 4: Additional News Sources & Deduplication (7 hrs)**

#### Goals

- Add GvA and Guardian adapters
- Implement article deduplication
- Build source management UI
- Add source health monitoring

#### Tasks

**Day 1: GvA Scraper (2 hrs)**

1. Analyze GvA website structure
2. Implement `GvaScraperAdapter.kt`
3. Handle Flemish-specific date formats
4. Test scraping with different categories

**Day 2: Guardian API (1 hr)**

1. Get Guardian API key (free tier)
2. Implement `GuardianAdapter.kt`
3. Map Guardian's unique response structure to domain model

**Day 3: Deduplication Algorithm (2 hrs)**

1. Implement `DeduplicateArticlesUseCase.kt`:

```kotlin
class DeduplicateArticlesUseCase {
    suspend operator fun invoke(articles: List<Article>): List<Article> {
        val seen = mutableSetOf<String>()
        val deduplicated = mutableListOf<Article>()

        articles.sortedByDescending { it.publishedAt }.forEach { article ->
            val signature = generateSignature(article)

            if (signature !in seen) {
                seen.add(signature)
                deduplicated.add(article)
            }
        }

        return deduplicated
    }

    private fun generateSignature(article: Article): String {
        // Normalize title (remove punctuation, lowercase, trim)
        val normalizedTitle = article.title
            .lowercase()
            .replace("[^a-z0-9\\s]".toRegex(), "")
            .trim()

        // Use first 50 chars of title + source
        return normalizedTitle.take(50)
    }
}
```

2. Add fuzzy matching for similar titles (Levenshtein distance)
3. Merge articles from multiple sources (keep most complete version)

**Day 4: Source Management UI (2 hrs)**

1. Create `SourcesScreen.kt` showing:
   - Available sources with logos
   - Enable/disable toggles
   - Health status indicators (green/yellow/red)
   - Last successful fetch time
2. Implement `SourcesViewModel.kt` with:
   - Source list from `SourceRepository`
   - Toggle logic
   - Health check trigger
3. Add source filter in `HomeScreen.kt`

**Deliverables:**

- 4+ news sources (NewsAPI, HBVL, GvA, Guardian)
- Smart deduplication
- Source management screen
- Health monitoring

---

### **Week 5: Search, Favorites & Sentiment (8 hrs)**

#### Goals

- Full-text search across sources
- Favorites/bookmarks system
- Sentiment analysis integration
- Advanced filtering

#### Tasks

**Day 1-2: Search Implementation (3 hrs)**

1. Add FTS (Full-Text Search) to Room:

```kotlin
@Entity(tableName = "articles_fts")
@Fts4(contentEntity = ArticleEntity::class)
data class ArticleFtsEntity(
    val title: String,
    val description: String,
    val content: String
)

@Dao
interface ArticleDao {
    @Query("SELECT * FROM articles JOIN articles_fts ON articles.id = articles_fts.docid WHERE articles_fts MATCH :query")
    fun searchArticles(query: String): Flow<List<ArticleEntity>>
}
```

2. Create `SearchScreen.kt` with:
   - Search bar with suggestions
   - Recent searches chip row
   - Filter options (source, date range, language)
   - Results list with highlighted matches
3. Implement `SearchViewModel.kt` with debounced search

**Day 3: Favorites System (2 hrs)**

1. Add `isFavorite` column to `ArticleEntity`
2. Create `ToggleFavoriteUseCase.kt`
3. Build `FavoritesScreen.kt` showing saved articles
4. Add export feature (share as text or JSON)

**Day 4: Sentiment Analysis (2 hrs)**

1. Implement `AnalyzeSentimentUseCase.kt`
2. Add sentiment to `ArticleCard.kt`:
   - Color-coded badge (green/gray/red)
   - Emoji indicator
   - Explanation tooltip
3. Create sentiment filter in search/feed

**Day 5: Advanced Filtering (1 hr)**

1. Add category chips to home screen
2. Implement date range picker
3. Add "Read later" queue with notifications

**Deliverables:**

- Full-text search with filters
- Working favorites system
- Sentiment analysis on articles
- Export functionality

---

### **Week 6-7: Advanced AI Features (10 hrs)**

#### Goals

- Translation between NL/EN
- Topic extraction & related articles
- AI-powered article comparison
- Reading time & TTS

#### Tasks

**Day 1-2: Translation (3 hrs)**

1. Implement `TranslateArticleUseCase.kt`
2. Add translation button in article detail view
3. Show side-by-side comparison (original vs translated)
4. Cache translations to save costs

**Day 3-4: Topic Extraction & Recommendations (4 hrs)**

1. Create `ExtractTopicsUseCase.kt`:

```kotlin
class ExtractTopicsUseCase @Inject constructor(
    private val openRouterClient: OpenRouterClient
) {
    suspend operator fun invoke(article: Article): Result<List<String>> {
        val prompt = """
            Extract 5 key topics/keywords from this article.
            Output as comma-separated list only.

            Title: ${article.title}
            Content: ${article.description}
        """.trimIndent()

        return openRouterClient.summarize(prompt, article.language, maxTokens = 50)
            .map { summary ->
                summary.text.split(",").map { it.trim() }.filter { it.isNotEmpty() }
            }
    }
}
```

2. Implement `GetRelatedArticlesUseCase.kt` using extracted topics
3. Add "Related Articles" section in detail view

**Day 5-6: Article Comparison (2 hrs)**

1. Build "Compare Sources" feature:
   - User selects 2-3 articles about same event
   - AI generates comparison highlighting differences in tone, facts
2. Show visual diff (e.g., side-by-side with colored highlights)

**Day 7: Reading Time & TTS (1 hr)**

1. Calculate reading time (word count / 250 wpm)
2. Integrate Android TTS for article reading
3. Add play/pause/speed controls

**Deliverables:**

- Translation feature
- Related articles recommendations
- Article comparison tool
- Accessibility features (TTS)

---

### **Week 8: Offline Mode & Notifications (7 hrs)**

#### Goals

- Background sync with WorkManager
- Offline reading
- Breaking news notifications
- Pre-caching summaries

#### Tasks

**Day 1-2: WorkManager Setup (3 hrs)**

1. Create `SyncNewsWorker.kt`:

```kotlin
@HiltWorker
class SyncNewsWorker @AssistedInject constructor(
    @Assisted context: Context,
    @Assisted params: WorkerParameters,
    private val getAggregatedNewsUseCase: GetAggregatedNewsUseCase
) : CoroutineWorker(context, params) {

    override suspend fun doWork(): Result {
        return try {
            getAggregatedNewsUseCase(
                sourceIds = listOf("newsapi", "hbvl", "gva"),
                forceRefresh = true
            ).first()

            Result.success()
        } catch (e: Exception) {
            if (runAttemptCount < 3) Result.retry() else Result.failure()
        }
    }
}
```

2. Schedule periodic sync (every 3 hours when on WiFi)
3. Create `PrefetchSummariesWorker.kt` to pre-generate AI summaries

**Day 3: Offline UI (2 hrs)**

1. Show offline banner when network unavailable
2. Gray out AI features that require network
3. Display last sync time
4. Add manual refresh button

**Day 4: Notifications (2 hrs)**

1. Implement `BreakingNewsWorker.kt`
2. Create notification channels (Breaking, Daily Digest)
3. Add notification preferences in settings
4. Show summary in notification (using cached AI summaries)

**Deliverables:**

- Offline reading support
- Background sync
- Smart notifications
- Pre-cached AI content

---

### **Week 9: Testing & Refinement (5 hrs)**

#### Goals

- Unit tests for critical paths
- UI tests for main flows
- Performance optimization
- Bug fixes

#### Tasks

**Day 1-2: Unit Tests (3 hrs)**

1. Test UseCases:

```kotlin
class GetAggregatedNewsUseCaseTest {
    private val mockNewsRepo = mockk<NewsRepository>()
    private val useCase = GetAggregatedNewsUseCase(mockNewsRepo)

    @Test
    fun `when sources return articles, should deduplicate and sort by date`() = runTest {
        // Given
        val articles = listOf(
            createArticle(title = "Breaking News", publishedAt = Instant.now()),
            createArticle(title = "Breaking News", publishedAt = Instant.now().minusSeconds(10)) // Duplicate
        )
        coEvery { mockNewsRepo.getArticles(any()) } returns flowOf(Result.success(articles))

        // When
        val result = useCase(listOf("newsapi", "hbvl")).first().getOrNull()

        // Then
        assertThat(result).hasSize(1) // Deduplicated
    }
}
```

2. Test Scrapers with mock HTML responses
3. Test ViewModel state transitions

**Day 3: UI Tests (2 hrs)**

1. Test article card display
2. Test search flow
3. Test AI summary generation

**Deliverables:**

- 60%+ code coverage
- All critical paths tested
- No major bugs

---

### **Week 10: CI/CD, Polish & Documentation (5 hrs)**

#### Goals

- GitHub Actions pipeline
- APK signing & release
- README & documentation
- Final polish

#### Tasks

**Day 1-2: GitHub Actions (2 hrs)**

1. Create `.github/workflows/android-ci.yml`:

```yaml
name: Android CI/CD

on:
  push:
    branches: [ main, develop ]
  pull_request:
    branches: [ main ]
  release:
    types: [ created ]

jobs:
  build:
    runs-on: ubuntu-latest
    steps:
      - uses: actions/checkout@v3

      - name: Set up JDK 17
        uses: actions/setup-java@v3
        with:
          java-version: '17'
          distribution: 'temurin'

      - name: Cache Gradle
        uses: actions/cache@v3
        with:
          path: |
            ~/.gradle/caches
            ~/.gradle/wrapper
          key: ${{ runner.os }}-gradle-${{ hashFiles('**/*.gradle*', '**/gradle-wrapper.properties') }}

      - name: Run tests
        run: ./gradlew test

      - name: Build Debug APK
        run: ./gradlew assembleDebug

      - name: Upload APK
        uses: actions/upload-artifact@v3
        with:
          name: app-debug
          path: app/build/outputs/apk/debug/*.apk

  release:
    if: github.event_name == 'release'
    runs-on: ubuntu-latest
    needs: build
    steps:
      - uses: actions/checkout@v3

      - name: Decode Keystore
        run: echo "${{ secrets.KEYSTORE_BASE64 }}" | base64 -d > release.keystore

      - name: Build Release APK
        run: ./gradlew assembleRelease
        env:
          KEYSTORE_FILE: ../release.keystore
          KEYSTORE_PASSWORD: ${{ secrets.KEYSTORE_PASSWORD }}
          KEY_ALIAS: ${{ secrets.KEY_ALIAS }}
          KEY_PASSWORD: ${{ secrets.KEY_PASSWORD }}

      - name: Upload to Release
        uses: softprops/action-gh-release@v1
        with:
          files: app/build/outputs/apk/release/*.apk
        env:
          GITHUB_TOKEN: ${{ secrets.GITHUB_TOKEN }}
```

2. Generate signing key and add to GitHub secrets
3. Test release build

**Day 3: Documentation (2 hrs)**

1. Write comprehensive README:
   - Features list with screenshots
   - Architecture diagram (use Mermaid or draw.io)
   - Setup instructions
   - API keys guide
2. Create CONTRIBUTING.md
3. Add inline code documentation

**Day 4: Final Polish (1 hr)**

1. Add app icon and splash screen
2. Polish animations and transitions
3. Test on different devices/screen sizes
4. Create demo video for portfolio

**Deliverables:**

- Automated CI/CD pipeline
- Signed release APK
- Complete documentation
- Portfolio-ready project
</details>

<details>
<summary>5. Feature Specifications (Detailed Requirements)</summary>

### 5.1 Core Features (Must-Have)

| Feature | Description | Acceptance Criteria | Complexity |
|---------|-------------|---------------------|------------|
| **Multi-Source Aggregation** | Combine articles from 4+ sources (API + scrapers) | - All sources appear in unified feed<br>- Duplicates removed<br>- Sources labeled | Medium |
| **OpenRouter AI Summary** | Generate concise 2-3 sentence summaries | - Summaries cached in DB<br>- Shows model used<br>- Cost displayed | Medium |
| **Language Detection** | Auto-detect NL/EN and use appropriate prompts | - 95%+ accuracy<br>- Supports Dutch/English | Low |
| **Source Management** | Enable/disable news sources | - Toggle persists across sessions<br>- Health status visible | Low |
| **Article Detail View** | Full article with images, metadata, actions | - WebView fallback for paywalls<br>- Share button<br>- Favorite toggle | Medium |
| **Offline Reading** | Cached articles accessible without internet | - Last 100 articles cached<br>- Offline indicator shown | Medium |
| **Search** | Full-text search with filters | - Real-time results<br>- Recent searches saved | Medium |
| **Favorites** | Bookmark articles for later | - Persists in Room<br>- Export option | Low |

### 5.2 Advanced Features (Should-Have)

| Feature | Description | Acceptance Criteria | Complexity |
|---------|-------------|---------------------|------------|
| **Sentiment Analysis** | Classify articles as positive/neutral/negative | - Visual indicator (emoji/color)<br>- 80%+ accuracy | Medium |
| **Translation** | Translate between NL/EN | - Inline display<br>- Cached results | Medium |
| **Related Articles** | AI-extracted topics → recommendations | - 3-5 related articles shown<br>- Relevance score | High |
| **Model Selection** | Choose OpenRouter model per task | - Shows cost per model<br>- Persists choice | Low |
| **Usage Tracking** | Token usage and cost estimation | - Daily/monthly stats<br>- Reset option | Low |
| **Notifications** | Breaking news alerts | - Configurable frequency<br>- Summary in notification | Medium |
| **Batch Summarization** | Generate summaries for all visible articles | - Progress indicator<br>- Cancel option | Medium |
| **Source Health Check** | Periodic checks for scraper/API availability | - Auto-disable failing sources<br>- Notification on failure | Medium |

### 5.3 Nice-to-Have Features (Bonus)

| Feature | Description | Complexity |
|---------|-------------|------------|
| **Article Comparison** | AI highlights differences between sources covering same story | High |
| **Topic Clustering** | Group similar articles with AI | High |
| **Reading Statistics** | Track reading time, articles read | Low |
| **Text-to-Speech** | Listen to articles | Medium |
| **Dark/Light Theme** | User-selectable theme | Low |
| **Widget** | Home screen widget with latest headlines | Medium |
| **Export to Podcast** | TTS → MP3 for offline listening | High |
</details>

<details>
<summary>6. Technical Specifications</summary>

### 6.1 Supported News Sources (Initial)

| Source | Type | Language | API/URL | Notes |
|--------|------|----------|---------|-------|
| **NewsAPI** | API | EN | newsapi.org | Free tier: 100 req/day |
| **Guardian** | API | EN | theguardian.com/open-platform | Free with key |
| **HBVL** | Scraper | NL | hbvl.be | Flemish regional |
| **GvA** | Scraper | NL | gva.be | Antwerp regional |
| **De Standaard** | Scraper | NL | standaard.be | (Optional) National |

**Scraping Strategy:**

- Use `Jsoup` for HTML parsing
- Implement polite scraping (1 req/5s per source)
- Add User-Agent header to identify app
- Handle rate limiting (429 errors)
- Cache aggressively to minimize requests

**Deduplication Logic:**

- Normalize titles (remove punctuation, lowercase)
- Calculate Levenshtein distance for similarity
- Threshold: 85% similarity = duplicate
- Keep article with most complete content

### 6.2 OpenRouter Configuration

**Base URL:** `https://openrouter.ai/api/v1`

**Recommended Models:**

| Task | Model | Cost (per 1M tokens) | Reason |
|------|-------|----------------------|--------|
| Summarization | `openai/gpt-4o-mini` | $0.15 / $0.60 | Fast, cheap, good quality |
| Sentiment | `google/gemini-flash-1.5` | $0.075 / $0.30 | Very cheap, adequate for classification |
| Translation | `anthropic/claude-3.5-sonnet` | $3 / $15 | Best quality for nuance |
| Extraction | `meta-llama/llama-3.1-8b-instruct` | $0.05 / $0.05 | Open source, cheap |

**Rate Limits:**

- OpenRouter: 200 requests/minute (paid tier)
- Free tier: 10 requests/minute
- Implement exponential backoff on 429 errors

**Cost Management:**

- Cache all AI responses in Room
- Show cost estimate before batch operations
- Daily spending cap in settings (default: $1)
- Warn user when approaching limit

### 6.3 Database Schema

```kotlin
@Entity(tableName = "articles")
data class ArticleEntity(
    @PrimaryKey val id: String,
    val title: String,
    val description: String,
    val content: String?,
    val url: String,
    val imageUrl: String?,
    val sourceId: String,
    val sourceName: String,
    val author: String?,
    val publishedAt: Long,
    val fetchedAt: Long,
    val language: String,
    val category: String?,
    val isFavorite: Boolean = false,
    val isRead: Boolean = false
)

@Entity(tableName = "summaries")
data class SummaryEntity(
    @PrimaryKey val articleId: String,
    val summaryText: String,
    val modelUsed: String,
    val tokensUsed: Int,
    val generatedAt: Long,
    val language: String
)

@Entity(tableName = "sources")
data class SourceEntity(
    @PrimaryKey val id: String,
    val displayName: String,
    val type: String, // "API" or "SCRAPER"
    val language: String,
    val isEnabled: Boolean,
    val healthStatus: String,
    val lastFetchedAt: Long?,
    val logoUrl: String?
)

@Entity(tableName = "usage_records")
data class UsageEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val timestamp: Long,
    val modelId: String,
    val promptTokens: Int,
    val completionTokens: Int,
    val totalTokens: Int,
    val costUSD: Double
)

@Entity(tableName = "sentiment_cache")
data class SentimentEntity(
    @PrimaryKey val articleId: String,
    val sentiment: String, // "POSITIVE", "NEUTRAL", "NEGATIVE"
    val confidence: Double,
    val analyzedAt: Long
)
```

### 6.4 API Keys Management

**Storage:**

- `EncryptedSharedPreferences` for OpenRouter API key
- Use Android Keystore for encryption key
- Never log API keys
- Validate on first input

**Environment Variables (local.properties):**

```properties
# DO NOT COMMIT THIS FILE
NEWS_API_KEY=your_newsapi_key
GUARDIAN_API_KEY=your_guardian_key
OPENROUTER_API_KEY=your_openrouter_key  # Only for development
```

**User-Provided Keys:**

- Users enter OpenRouter key in settings
- Stored encrypted
- Optional: App-provided key with usage limits
</details>

<details>
<summary>7. UI/UX Specifications</summary>

### 7.1 Screen Flow

```text
Splash Screen
    ↓
Onboarding (first launch)
    ├─> Source Selection
    ├─> Category Preferences
    └─> AI Setup (optional)
    ↓
Home Screen
    ├─> Article Detail
    │   ├─> AI Summary
    │   ├─> Translation
    │   └─> Related Articles
    ├─> Search
    ├─> Favorites
    └─> Settings
        ├─> AI Settings
        ├─> Source Management
        ├─> Preferences
        └─> Usage Stats
```

### 7.2 Home Screen Design

**Layout:**

```text
┌─────────────────────────────────────┐
│  NexusNews AI           🔍  ⚙️      │  ← Top bar
├─────────────────────────────────────┤
│ [All] [Tech] [Local] [Business]    │  ← Category tabs
├─────────────────────────────────────┤
│ Sources: [NewsAPI] [HBVL] [GvA]    │  ← Source filter chips
├─────────────────────────────────────┤
│ ┌─────────────────────────────────┐ │
│ │ 📰 Breaking: AI News            │ │  ← Article card
│ │ NewsAPI • 2 hours ago           │ │
│ │ [Image]                         │ │
│ │ Summary: Researchers unveil...  │ │
│ │ 😊 Positive • [AI Summary]      │ │  ← Sentiment + Action
│ └─────────────────────────────────┘ │
│ ┌─────────────────────────────────┐ │
│ │ 📰 Lokaal nieuws Limburg        │ │
│ │ HBVL • 4 hours ago  • 🇳🇱        │ │
│ │ ...                             │ │
│ └─────────────────────────────────┘ │
└─────────────────────────────────────┘
│ [Home] [Search] [Favorites] [More] │  ← Bottom nav
└─────────────────────────────────────┘
```

**Interactions:**

- Pull-to-refresh
- Infinite scroll (pagination)
- Swipe left on card → Quick favorite
- Long press → Share/Save/Translate menu

### 7.3 Article Detail Screen

**Components:**

- Hero image (full width)
- Title (large, bold)
- Source badge + author + date
- Sentiment indicator
- AI Summary section (expandable)
  - "Generate Summary" button (if not cached)
  - Shows loading animation
  - Displays summary with model info
- Full article content
  - WebView if external link
  - Parsed text if scraped
- Translation toggle (NL ↔ EN)
- Related Articles section (horizontal scroll)
- Actions: Favorite, Share, TTS

### 7.4 AI Settings Screen

**Sections:**

1. **API Key Management**
   - Input field (password type)
   - "Validate" button → Shows checkmark or error
   - Current status: ✅ Valid / ❌ Invalid / ⏳ Not set

2. **Model Selection**
   - Grouped list by task:
     - Summarization: [Dropdown with models]
     - Sentiment: [Dropdown]
     - Translation: [Dropdown]
   - Each shows: Name, Provider, Cost/1M tokens

3. **Usage Statistics**
   - Card showing:
     - Total tokens used (today/month)
     - Estimated cost ($X.XX)
     - Requests made
     - Most used model
   - "Reset Stats" button

4. **Advanced Options**
   - Daily spending limit (slider, $0.50 - $10)
   - Auto-summarize on load (toggle)
   - Batch summarization limit (number picker)
</details>

<details>
<summary>8. Success Criteria & Deliverables</summary>

### 8.1 Minimum Viable Product (MVP)

- ✅ 3+ news sources (1 API + 2 scrapers minimum)
- ✅ OpenRouter integration with summarization
- ✅ Language detection (NL/EN)
- ✅ MVVM architecture with Clean Architecture
- ✅ Offline reading (cached articles)
- ✅ Basic search and favorites
- ✅ Source management UI
- ✅ CI/CD pipeline for APK releases

### 8.2 Portfolio Requirements

- ✅ Professional README with:
  - Architecture diagram
  - Screenshots/GIFs
  - Setup instructions
  - Key learnings section
- ✅ Deployable APK (GitHub Releases)
- ✅ Code quality:
  - No major linting errors
  - Consistent naming conventions
  - Documented complex logic
- ✅ 50%+ unit test coverage on domain layer

### 8.3 Learning Outcomes

By the end, you should be able to:

1. Explain Clean Architecture layers and MVVM in Kotlin
2. Implement web scraping with Jsoup for production use
3. Design AI-powered features with cost optimization
4. Build offline-first Android apps with Room + WorkManager
5. Setup CI/CD for Android projects
6. Handle multi-language content and localization
</details>

<details>
<summary>9. Next Steps</summary>

### Before Week 1

1. **Get API Keys:**
   - NewsAPI: <https://newsapi.org/register>
   - Guardian: <https://open-platform.theguardian.com/access/>
   - OpenRouter: <https://openrouter.ai/keys>

2. **Setup Development Environment:**
   - Install Android Studio (latest stable)
   - Install Kotlin plugin
   - Setup Android emulator or physical device

3. **Research News Sites:**
   - Visit HBVL, GvA websites
   - Use browser DevTools to inspect HTML structure
   - Document CSS selectors for title, description, image, etc.

4. **Create GitHub Repo:**
   - Initialize with `.gitignore` for Android
   - Add `local.properties` to `.gitignore`
   - Create initial project structure

### Weekly Checkpoints

- **End of Each Week:** Create a GitHub Issue summarizing:
  - What you built
  - Challenges faced
  - New Kotlin/Android concepts learned
  - Hours spent
- **Midpoint (Week 5):** Record a quick demo video
- **Week 10:** Finalize README and portfolio presentation
</details>

<details>
<summary>10. Risk Mitigation</summary>

| Risk | Mitigation Strategy |
|------|---------------------|
| **News sites change HTML structure** | Create adapter interface; document selectors in config file for easy updates |
| **OpenRouter API costs exceed budget** | Implement strict caching; daily spending cap; local fallback for non-critical features |
| **Scraping gets blocked (rate limiting)** | Add polite delays (5s between requests); rotate User-Agent; implement exponential backoff |
| **Language detection inaccuracy** | Allow manual language override; improve with more keywords; consider ML model if needed |
| **Time overrun on features** | Prioritize MVP features first; move nice-to-haves to "Phase 2" backlog |
| **Device compatibility issues** | Test on emulator + 1 physical device; focus on Android 8+ (API 26+) |
</details>

<details>
<summary>11. Design System & Color Scheme</summary>

### Design Principles

- **Primary Colors**: Deep blue for trust and news authority (inspired by skies and media branding).
- **Accent Colors**: Teal for interactivity (buttons, AI features) – energetic but not overwhelming.
- **Neutral Base**: Grays/whites for readability; avoids fatigue during long reading sessions.
- **Semantic Colors**: Specific for sentiments (green/red/gray), errors, and success to guide users emotionally (e.g., positive news gets a uplifting green).
- **AI Focus**: A vibrant electric blue accent for AI elements (summaries, model badges) to highlight "smart" features without clashing.
- **Consistency**: Use the primary for navigation/top bars, secondary for cards/sources, accents for CTAs (e.g., "Generate Summary" button). Transitions/animations can use subtle gradients (e.g., blue-to-teal).
- **Accessibility**: All text colors have sufficient contrast; test with tools like Android Studio's accessibility inspector.
- **Dark Mode**: Inverted neutrals with warmer tones to reduce eye strain.

### Color Palette

| Category | Color Name | Hex Code | Light Mode Example | Dark Mode Example |
|----------|------------|----------|--------------------|-------------------|
| **Primary** | Deep Navy | #1976D2 | Top bar, primary buttons | Same |
| **Primary Variant** | Navy Light | #42A5F5 | Hover states, links | Navigation icons |
| **Secondary** | Teal Accent | #00BCD4 | Secondary buttons, chips | Card borders |
| **Secondary Variant** | Teal Soft | #80DEEA | Card backgrounds | Divider lines |
| **Background** | Pure White | #FFFFFF | Main background, feed | N/A |
| **Background Dark** | Charcoal | #121212 | N/A | Main background |
| **Surface** | Light Gray | #F5F5F5 | Article cards, search bar | N/A |
| **Surface Dark** | Dark Gray | #1E1E1E | N/A | Article cards, modals |
| **AI Accent** | Electric Blue | #2196F3 | AI buttons, badges | AI highlights |
| **Text Primary** | Dark Charcoal | #212121 | Titles, body text | N/A |
| **Sentiment Pos** | Success Green | #4CAF50 | Positive badges | Same |
| **Sentiment Neg** | Alert Red | #F44336 | Negative badges | Same |

#### Visual Usage Examples Across the App

- **Home Feed**: Background (#FFFFFF light / #121212 dark); Article cards (#F5F5F5 light / #1E1E1E dark) with teal borders (#00BCD4).
- **Article Detail**: AI Summary button (electric blue #2196F3 background); Sentiment badge: Green (#4CAF50) for positive news.
- **AI Settings**: Model cards with teal accents (#00BCD4); Usage stats cards (secondary variant #80DEEA).
- **Source Management**: Source logos on navy (#1976D2) badges; Health indicators: Green for healthy (#4CAF50).

### Implementation in Jetpack Compose

In your `presentation/theme/Color.kt` file:

```kotlin
import androidx.compose.ui.graphics.Color

val Primary = Color(0xFF1976D2)
val Secondary = Color(0xFF00BCD4)
val BackgroundDark = Color(0xFF121212)
val AIAccent = Color(0xFF2196F3)
val SuccessGreen = Color(0xFF4CAF50)
val AlertRed = Color(0xFFF44336)
```

In `presentation/theme/Theme.kt`:

```kotlin
@Composable
fun NewsNexusTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    val colors = if (darkTheme) {
        darkColorScheme(primary = Primary, background = BackgroundDark)
    } else {
        lightColorScheme(primary = Primary, background = Color.White)
    }

    MaterialTheme(colorScheme = colors, content = content)
}
```
</details>
