# NexusNews AI

 <!-- _![NexusNews AI Banner](https://via.placeholder.com/1200x300/0F4C5A/FFFFFF?text=NexusNews+AI) Replace with actual banner/screenshot -->

A modern Android news aggregator app that combines international news APIs with local sources (via web scraping). Powered by OpenRouter AI for intelligent features like auto-summarization, sentiment analysis, multi-language translations, and related article recommendations. Built as a college assignment to explore Jetpack Compose, MVVM, Clean Architecture, and advanced Android patterns.

This app demonstrates production-ready mobile development: offline caching, background sync, secure AI key management, and a polished UI. It's not just a news reader—it's an AI-enhanced discovery tool for personalized news consumption.

## ✨ Features

### Core News Aggregation
- **Multi-Source Feed**: Pulls from NewsAPI (international), Guardian (UK/global), and local news sites (via Jsoup scraping).
- **Source Management**: Enable/disable sources, view health status (API availability, scraping success), and filter by category (Tech, Local, Business, Sports).
- **Search & Favorites**: Full-text search with filters; bookmark articles with offline export (JSON/text).

### AI-Powered Enhancements (via OpenRouter)
- **Smart Summarization**: Generate 2-3 sentence summaries in Dutch or English, cached for offline use. Batch mode for feeds.
- **Sentiment Analysis**: Classify articles as Positive/Neutral/Negative with visual indicators (emojis, colors).
- **Translations**: On-demand NL ↔ EN translation with side-by-side views.
- **Topic Extraction**: AI finds key topics and suggests related articles.
- **Model Selection**: Choose from 100+ OpenRouter models (e.g., GPT-4o-mini for speed, Claude for quality); track token usage and costs.
- **Cost Optimization**: Caching, rate limiting, and spending caps to keep API calls efficient.

### Advanced UX/UI
- **Offline-First**: Room DB for caching articles/summaries; background sync with WorkManager.
- **Notifications**: Breaking news alerts with pre-generated AI summaries.
- **Accessibility**: Reading time estimates, Text-to-Speech (TTS), and Material 3 themes (light/dark).
- **Polish**: Infinite scrolling, animations (Compose), haptic feedback, and onboarding flow for preferences.

### Architecture Highlights
- **MVVM + Clean Architecture**: Presentation (Compose UI), Domain (Use Cases/Entities), Data (Repositories/Adapters).
- **Reactive UI**: StateFlow for real-time updates (e.g., feed refresh, AI loading).
- **Background Services**: Workers for prefetching and health checks.
- **Security**: Encrypted storage for OpenRouter API keys (Android Keystore).

## 🛠 Tech Stack

| Category | Technologies |
|----------|--------------|
| **Language & UI** | Kotlin, Jetpack Compose (Material 3), Coil (images) |
| **Architecture** | MVVM, Clean Architecture, Hilt (Dependency Injection) |
| **Data** | Room (SQLite DB for caching/articles/favorites), DataStore (preferences/keys) |
| **Networking** | Retrofit + OkHttp (APIs), Jsoup (scraping), Moshi (JSON) |
| **AI & APIs** | OpenRouter (100+ LLMs for summarization/sentiment/translation), NewsAPI, Guardian API |
| **Background** | WorkManager (sync/notifications), Coroutines/Flow (async) |
| **Testing** | JUnit, Mockito, Turbine (unit tests); Compose UI Testing (instrumentation) |
| **Other** | Timber (logging), Security-Crypto (encryption), Navigation Compose |

## 📱 Screenshots

<!-- Add actual screenshots here -->
1. **Home Feed**: Aggregated articles with AI badges and source filters.  
   ![Home Feed](screenshots/home_feed.png)

2. **Article Detail**: Full view with AI summary, sentiment, and translation.  
   ![Article Detail](screenshots/article_detail.png)

3. **AI Settings**: Model selection, key management, and usage stats.  
   ![AI Settings](screenshots/ai_settings.png)

4. **Source Management**: Toggle sources and monitor health.  
   ![Sources](screenshots/sources.png)

5. **Search & Favorites**: Quick search with offline support.  
   ![Search](screenshots/search.png)

## 🚀 Getting Started

### Prerequisites
- Android Studio (Giraffe or later)
- Android SDK API 26+ (Android 8.0+)
- API Keys: 
  - [NewsAPI Key](https://newsapi.org) (free tier)
  - [Guardian API Key](https://open-platform.theguardian.com) (free)
  - [OpenRouter API Key](https://openrouter.ai/keys) (free tier for testing; paid for production)

### Setup
1. Clone the repo:
`git clone https://github.com/undead2146/NexusNews.git`
