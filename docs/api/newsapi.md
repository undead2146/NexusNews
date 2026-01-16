---
title: NewsAPI Integration
description: Integration details for NewsAPI.org service
category: api
lastUpdated: 2026-01-16
aiContext: true
tags: [newsapi, api, retrofit]
---

# NewsAPI Integration

NexusNews uses [NewsAPI.org](https://newsapi.org/) as a primary source for international headlines.

## 🏗️ Implementation

- **Interface**: `com.example.nexusnews.data.remote.api.NewsApiService`
- **Base URL**: `https://newsapi.org/v2/`
- **Client**: Retrofit + Moshi

## 🔌 Endpoints

### 1. Top Headlines (`/top-headlines`)

Fetches breaking news headlines.

```kotlin
suspend fun getTopHeadlines(
    @Query("country") country: String? = null,
    @Query("category") category: String? = null,
    @Query("q") query: String? = null,
    @Query("page") page: Int = 1
): Response<NewsApiResponse>
```

- **Usage**: Main feed, Category tabs.
- **Parameters**:
    - `country`: 2-letter ISO code (e.g., 'us', 'be').
    - `category`: 'technology', 'sports', 'business', etc.

### 2. Everything (`/everything`)

Search through millions of articles.

```kotlin
suspend fun getEverything(
    @Query("q") query: String,
    @Query("sortBy") sortBy: String? = null,
    @Query("page") page: Int = 1
): Response<NewsApiResponse>
```

- **Usage**: Search screen.
- **Parameters**:
    - `q`: Search keywords (Required).
    - `sortBy`: 'relevancy', 'popularity', 'publishedAt'.

## 🔑 Authentication

Authentication is handled via `AuthInterceptor` which appends the API key to every request header.

```http
X-Api-Key: <YOUR_API_KEY>
```

## ⚠️ Rate Limits (Free Tier)

- **Limit**: 100 requests per day (Developer Plan).
- **Strategy**:
    - The app uses **Room Database Caching** to minimize API calls.
    - `NewsRepository` checks local cache availability and expiration (default: 30 minutes) before hitting the API.
