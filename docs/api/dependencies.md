---
title: Dependencies Reference
description: Complete list of dependencies, libraries, and their usage
category: api
lastUpdated: 2025-11-03
aiContext: true
tags: [dependencies, libraries, gradle, versions]
versionsFile: gradle/libs.versions.toml
---

# Dependencies Reference

Complete reference of all dependencies used in NexusNews with their purposes, versions, and documentation links.

## 📦 Core Dependencies

### Android Core

| Library | Version | Purpose | Documentation |
|---------|---------|---------|---------------|
| `androidx.core:core-ktx` | 1.17.0 | Kotlin extensions for Android APIs | [Docs](https://developer.android.com/kotlin/ktx) |
| `androidx.lifecycle:lifecycle-runtime-ktx` | 2.9.4 | Lifecycle-aware components | [Docs](https://developer.android.com/topic/libraries/architecture/lifecycle) |
| `androidx.activity:activity-compose` | 1.11.0 | Activity integration with Compose | [Docs](https://developer.android.com/jetpack/androidx/releases/activity) |

### Jetpack Compose

| Library | Version | Purpose | Documentation |
|---------|---------|---------|---------------|
| `androidx.compose:compose-bom` | 2024.09.00 | Bill of Materials for Compose | [Docs](https://developer.android.com/jetpack/compose/bom) |
| `androidx.compose.ui:ui` | (BOM) | Core UI components | [Docs](https://developer.android.com/jetpack/compose) |
| `androidx.compose.material3:material3` | (BOM) | Material Design 3 components | [Docs](https://developer.android.com/jetpack/compose/designsystems/material3) |
| `androidx.navigation:navigation-compose` | 2.7.6 | Navigation for Compose | [Docs](https://developer.android.com/jetpack/compose/navigation) |
| `androidx.lifecycle:lifecycle-viewmodel-compose` | 2.9.4 | ViewModel integration | [Docs](https://developer.android.com/topic/libraries/architecture/viewmodel) |

**Usage Context**: UI layer, all screens and components

---

## 🗄️ Data Layer

### Room Database

| Library | Version | Purpose | Documentation |
|---------|---------|---------|---------------|
| `androidx.room:room-runtime` | 2.6.1 | SQLite database runtime | [Docs](https://developer.android.com/training/data-storage/room) |
| `androidx.room:room-ktx` | 2.6.1 | Kotlin extensions + Coroutines | [Docs](https://developer.android.com/training/data-storage/room/async-queries) |
| `androidx.room:room-compiler` | 2.6.1 | Annotation processor (kapt) | [Docs](https://developer.android.com/training/data-storage/room) |

**Bundle**: `libs.bundles.room`  
**Usage**: Caching articles, summaries, user preferences, AI usage tracking

### DataStore & Security

| Library | Version | Purpose | Documentation |
|---------|---------|---------|---------------|
| `androidx.datastore:datastore-preferences` | 1.0.0 | Key-value storage (replaces SharedPreferences) | [Docs](https://developer.android.com/topic/libraries/architecture/datastore) |
| `androidx.security:security-crypto` | 1.1.0-alpha06 | Encrypted storage for API keys | [Docs](https://developer.android.com/topic/security/data) |

**Usage**: User settings, encrypted API key storage (OpenRouter, NewsAPI)

---

## 🌐 Network Layer

### Retrofit & HTTP

| Library | Version | Purpose | Documentation |
|---------|---------|---------|---------------|
| `com.squareup.retrofit2:retrofit` | 3.0.0 | HTTP client library | [Docs](https://square.github.io/retrofit/) |
| `com.squareup.retrofit2:converter-moshi` | 3.0.0 | JSON converter for Retrofit | [Docs](https://github.com/square/retrofit/tree/master/retrofit-converters/moshi) |
| `com.squareup.okhttp3:okhttp` | 4.12.0 | HTTP client (underlying Retrofit) | [Docs](https://square.github.io/okhttp/) |
| `com.squareup.okhttp3:logging-interceptor` | 4.12.0 | HTTP request/response logging | [Docs](https://github.com/square/okhttp/tree/master/okhttp-logging-interceptor) |

**Usage**: NewsAPI, Guardian API, OpenRouter API calls

### JSON Parsing (Moshi)

| Library | Version | Purpose | Documentation |
|---------|---------|---------|---------------|
| `com.squareup.moshi:moshi` | 1.15.1 | JSON parsing library | [Docs](https://github.com/square/moshi) |
| `com.squareup.moshi:moshi-kotlin` | 1.15.1 | Kotlin support for Moshi | [Docs](https://github.com/square/moshi#kotlin) |
| `com.squareup.moshi:moshi-kotlin-codegen` | 1.15.1 | Code generation (kapt) | [Docs](https://github.com/square/moshi#codegen) |

**Bundle**: `libs.bundles.moshiBundle`  
**Usage**: Parsing API responses from NewsAPI, Guardian, OpenRouter

---

## 🧩 Dependency Injection

### Hilt (Dagger)

| Library | Version | Purpose | Documentation |
|---------|---------|---------|---------------|
| `com.google.dagger:hilt-android` | 2.50 | DI framework for Android | [Docs](https://dagger.dev/hilt/) |
| `com.google.dagger:hilt-compiler` | 2.50 | Annotation processor (kapt) | [Docs](https://dagger.dev/hilt/compiler-options) |
| `androidx.hilt:hilt-navigation-compose` | 1.1.0 | Hilt integration with Compose Nav | [Docs](https://developer.android.com/training/dependency-injection/hilt-jetpack) |

**Usage**: All dependency injection across layers (ViewModels, Repositories, Data Sources)

---

## 🕸️ Web Scraping

### Jsoup

| Library | Version | Purpose | Documentation |
|---------|---------|---------|---------------|
| `org.jsoup:jsoup` | 1.17.2 | HTML parsing for web scraping | [Docs](https://jsoup.org/) |

**Usage**: Scraping HBVL, GvA, and other  news sources without public APIs

---

## 🖼️ Image Loading

### Coil

| Library | Version | Purpose | Documentation |
|---------|---------|---------|---------------|
| `io.coil-kt:coil-compose` | 2.5.0 | Image loading for Compose | [Docs](https://coil-kt.github.io/coil/compose/) |

**Usage**: Loading article images, source logos

---

## ⚙️ Background Work

### WorkManager

| Library | Version | Purpose | Documentation |
|---------|---------|---------|---------------|
| `androidx.work:work-runtime-ktx` | 2.9.0 | Background task scheduling | [Docs](https://developer.android.com/topic/libraries/architecture/workmanager) |

**Usage**: Periodic news sync, source health checks, prefetching AI summaries

---

## 📝 Logging

### Timber

| Library | Version | Purpose | Documentation |
|---------|---------|---------|---------------|
| `com.jakewharton.timber:timber` | 5.0.1 | Logging utility | [Docs](https://github.com/JakeWharton/timber) |

**Usage**: Debug logging throughout the app (auto-stripped in release builds)

---

## 🧪 Testing Dependencies

### Unit Testing

| Library | Version | Purpose | Documentation |
|---------|---------|---------|---------------|
| `junit:junit` | 4.13.2 | Unit testing framework | [Docs](https://junit.org/junit4/) |
| `org.jetbrains.kotlinx:kotlinx-coroutines-test` | 1.7.3 | Testing coroutines | [Docs](https://kotlin.github.io/kotlinx.coroutines/kotlinx-coroutines-test/) |
| `io.mockk:mockk` | 1.13.9 | Mocking library for Kotlin | [Docs](https://mockk.io/) |
| `app.cash.turbine:turbine` | 1.0.0 | Testing Kotlin Flows | [Docs](https://github.com/cashapp/turbine) |

**Bundle**: `libs.bundles.testingUnit`

### Android Instrumentation Testing

| Library | Version | Purpose | Documentation |
|---------|---------|---------|---------------|
| `androidx.test.ext:junit` | 1.3.0 | JUnit for Android tests | [Docs](https://developer.android.com/training/testing/junit-runner) |
| `androidx.test.espresso:espresso-core` | 3.7.0 | UI testing framework | [Docs](https://developer.android.com/training/testing/espresso) |
| `androidx.compose.ui:ui-test-junit4` | (BOM) | Compose UI testing | [Docs](https://developer.android.com/jetpack/compose/testing) |
| `com.google.dagger:hilt-android-testing` | 2.50 | Hilt testing utilities | [Docs](https://dagger.dev/hilt/testing) |

---

## �� Gradle Plugins

| Plugin | Version | Purpose | Documentation |
|--------|---------|---------|---------------|
| `com.android.application` | 8.13.0 | Android app plugin | [Docs](https://developer.android.com/build) |
| `org.jetbrains.kotlin.android` | 2.0.21 | Kotlin Android support | [Docs](https://kotlinlang.org/docs/gradle.html) |
| `org.jetbrains.kotlin.plugin.compose` | 2.0.21 | Compose compiler plugin | [Docs](https://developer.android.com/jetpack/compose/compiler) |
| `org.jetbrains.kotlin.kapt` | 2.0.21 | Annotation processing | [Docs](https://kotlinlang.org/docs/kapt.html) |
| `com.google.dagger.hilt.android` | 2.50 | Hilt plugin | [Docs](https://dagger.dev/hilt/gradle-setup) |
| `org.jlleitschuh.gradle.ktlint` | 13.1.0 | Kotlin linting | [Docs](https://github.com/JLLeitschuh/ktlint-gradle) |
| `io.gitlab.arturbosch.detekt` | 1.23.8 | Static code analysis | [Docs](https://detekt.dev/) |

---

## 🎯 API Services Used

### News Sources

1. **NewsAPI** ([newsapi.org](https://newsapi.org))
   - **Tier**: Free tier (100 requests/day)
   - **Coverage**: International news
   - **Response Format**: JSON (Moshi parsing)

2. **Guardian API** ([open-platform.theguardian.com](https://open-platform.theguardian.com))
   - **Tier**: Free tier (5000 requests/day)
   - **Coverage**: UK/global news
   - **Response Format**: JSON (Moshi parsing)

3. **HBVL** ([hbvl.be](https://www.hbvl.be))
   - **Method**: Web scraping (Jsoup)
   - **Coverage**:  local news (Dutch)

4. **GvA** ([gva.be](https://www.gva.be))
   - **Method**: Web scraping (Jsoup)
   - **Coverage**:  news (Dutch)

### AI Service

**OpenRouter** ([openrouter.ai](https://openrouter.ai))
- **Purpose**: AI summarization, sentiment analysis, translation
- **Models**: GPT-4o-mini, Claude Haiku, Gemini, etc.
- **Pricing**: Pay-per-token (tracked in app)

---

## 📚 Related Documentation

- [Architecture Overview](/architecture/)
- [Setup Guide](/development/setup)
- [News Sources Architecture](/architecture/news-sources)
- [OpenRouter Integration](/architecture/ai-integration)

## 🔄 Version Management

All versions are centralized in [`gradle/libs.versions.toml`](../../gradle/libs.versions.toml).

To update a dependency:
1. Update version in `libs.versions.toml`
2. Sync Gradle
3. Update this documentation
4. Test thoroughly
