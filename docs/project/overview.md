---
title: Project Overview
description: High-level overview of the NexusNews project
category: project
lastUpdated: 2026-01-16
aiContext: true
---

# NexusNews Project Overview

## What is NexusNews?

NexusNews is a modern Android news aggregator application that combines international news APIs with local sources through web scraping. The app is enhanced with OpenRouter AI to provide intelligent features like summarization, sentiment analysis, and personalized recommendations.

## Project Goals

### Primary Objectives

1. **Multi-Source Aggregation**: Unify news from various sources (NewsAPI, Guardian, HBVL, GvA) into a single feed
2. **AI Enhancement**: Use OpenRouter to add intelligent features without managing multiple AI providers
3. **Offline-First**: Ensure the app works seamlessly even without internet connectivity
4. **Clean Architecture**: Demonstrate modern Android development patterns (MVVM, Clean Architecture, Hilt)

### Learning Objectives

- Master Kotlin and Jetpack Compose
- Implement Clean Architecture in a real-world project
- Integrate multiple data sources (APIs + web scraping)
- Work with AI APIs (OpenRouter)
- Build production-ready Android applications

## Key Features

### Core Functionality

- **News Feed**: Aggregated articles from multiple sources
- **Search & Filter**: Find articles by keyword, category, or source
- **Bookmarks**: Save articles for later reading
- **Offline Reading**: Cached articles available offline

### AI-Powered Features

- **Smart Summarization**: Generate concise summaries in Dutch or English
- **Sentiment Analysis**: Classify articles as positive, neutral, or negative
- **Translations**: On-demand translation between Dutch and English
- **Recommendations**: AI-suggested related articles
- **Chat Assistant**: Context-aware Q&A about articles

### Technical Features

- **Material Design 3**: Modern, polished UI
- **Dark/Light Themes**: Automatic theme switching
- **Background Sync**: Periodic news updates via WorkManager
- **Secure Storage**: Encrypted API key storage

## Technology Stack

### Core Technologies

- **Platform**: Android (API 24+)
- **Language**: Kotlin 2.0.21
- **UI**: Jetpack Compose + Material 3
- **Architecture**: MVVM + Clean Architecture
- **Dependency Injection**: Hilt (Dagger)

### Data Layer

- **Database**: Room SQLite
- **Preferences**: DataStore
- **Security**: AndroidX Security Crypto
- **Networking**: Retrofit + OkHttp
- **Web Scraping**: Jsoup
- **JSON**: Moshi

### Additional Libraries

- **Image Loading**: Coil
- **Background Tasks**: WorkManager
- **Logging**: Timber
- **Testing**: JUnit, Mockk, Turbine, Espresso

## Project Timeline

**Duration**: 10 weeks (50-60 hours)

### Phases

1. **Phase 0**: Project Foundation & Setup (Weeks 1-2)
2. **Phase 1**: News API Integration & Basic UI (Weeks 3-5)
3. **Phase 2**: Enhanced Features & UI Polish (Weeks 6-7)
4. **Phase 3**: AI Integration Foundation (Week 8)
5. **Phase 4**: Advanced AI Features (Week 9)
6. **Phase 5**: Expansion & Polish (Weeks 10+)

[View detailed roadmap →](/project/roadmap)

## Current Status

**Phase**: Phase 5 - Refactoring & Stabilization
**Status**: In Progress
**Last Updated**: January 16, 2026

### Completed

**Phase 0 & 1:**
- ✅ Project structure & Gradle setup
- ✅ CI/CD pipeline & Documentation
- ✅ Core architecture (MVVM)
- ✅ Network layer & NewsAPI integration
- ✅ Room database & Offline caching
- ✅ Basic UI (News Feed, Details, Navigation)

**Phase 2 - Enhanced Features:**
- ✅ Search & Filtering (History, Categories, Sort)
- ✅ Topic/Tag system
- ✅ Bookmarks with Room persistence
- ✅ UI Polish (Animations, Dark/Light Theme)
- ✅ Settings & Preferences (DataStore)

**Phase 3 - AI Foundation:**
- ✅ OpenRouter API Client & Secure Storage
- ✅ AI Service Architecture
- ✅ Model Selection & Fallback Logic (6 models)
- ✅ Article Summarization with Caching
- ✅ AI Usage Tracking

**Phase 4 - Advanced AI:**
- ✅ Sentiment Analysis & Bias Detection
- ✅ Key Points & Entity Extraction
- ✅ Smart Recommendations
- ✅ AI Chat Assistant
- ✅ Content Generation (Headlines, Tweets)

**Phase 5 - Refactoring (Partial):**
- ✅ Clean Architecture Migration (Use Cases)
- ✅ Comprehensive Unit & Integration Testing
- ✅ Performance Monitoring (Latency Tracking)
- ✅ Room 2.7.0+ Migration

### In Progress

- 🔄 **Phase 5: Stabilization**
  - Refactoring legacy view models
  - Improving error handling and recovery
  - Final UI polish and consistent styling

### Next Up

- ⏳ **Epic 5.3: Advanced Features** (Notifications, Widgets)
- ⏳ **Epic 5.5: Analytics & Monitoring** (Crashlytics)
- ⏳ **Release Preparation** (ProGuard/R8, Signing)

## Team

**Developer**: undead2146
**Repository**: [github.com/undead2146/NexusNews](https://github.com/undead2146/NexusNews)
**Documentation**: [undead2146.github.io/NexusNews](https://undead2146.github.io/NexusNews/)

## Resources

### Documentation

- [Product Requirements Document](/project/prd)
- [Roadmap](/project/roadmap)
- [Architecture Overview](/architecture/)
- [Dependencies](/api/dependencies)
- [Setup Guide](/development/setup)

### External Links

- [GitHub Repository](https://github.com/undead2146/NexusNews)
- [GitHub Issues](https://github.com/undead2146/NexusNews/issues)
- [GitHub Actions](https://github.com/undead2146/NexusNews/actions)

## Contributing

This is currently a solo learning project, but suggestions and feedback are welcome via GitHub Issues.

## License

See [LICENSE](https://github.com/undead2146/NexusNews/blob/main/LICENSE) file for details.
