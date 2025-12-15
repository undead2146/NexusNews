---
title: Project Overview
description: High-level overview of the NexusNews project
category: project
lastUpdated: 2025-11-03
aiContext: true
---

# NexusNews Project Overview

## What is NexusNews?

NexusNews is a modern Android news aggregator application that combines international news APIs with local  sources through web scraping. The app is enhanced with OpenRouter AI to provide intelligent features like summarization, sentiment analysis, and personalized recommendations.

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
6. **Phase 5**: Expansion & Polish (Week 10)

[View detailed roadmap →](/project/roadmap)

## Current Status

**Phase**: Phase 2 - Enhanced News Features & UI Polish
**Status**: In Progress
**Last Updated**: December 15, 2025

### Completed

**Phase 0 & 1:**
- ✅ Project structure setup
- ✅ Gradle configuration with version catalogs
- ✅ CI/CD pipeline (GitHub Actions)
- ✅ Documentation structure (VitePress)
- ✅ Code quality tools (Ktlint, Detekt)
- ✅ Core architecture implementation (MVVM + Clean Architecture)
- ✅ Network layer foundation with monitoring, retry logic, and error handling
- ✅ Comprehensive testing infrastructure (unit + integration tests)
- ✅ Constants restructuring into categorized classes
- ✅ NewsAPI integration
- ✅ Room database for offline caching
- ✅ Basic news list UI with pull-to-refresh
- ✅ News detail screen
- ✅ Bottom navigation (Home, Search, Bookmarks, Settings)

**Phase 2 - Epic 2.1 (Week 6):**
- ✅ Search functionality with debounced input
- ✅ Search history with DataStore
- ✅ Category filtering
- ✅ Sort options

**Phase 2 - Epic 2.2 (Week 7):**
- ✅ Category navigation with horizontal selector
- ✅ Enhanced NewsCategory enum (icons, colors, descriptions)
- ✅ Topic/tag system with automatic extraction
- ✅ Tag display UI components
- ✅ Category preferences persistence (DataStore)

**Phase 2 - Epic 2.3 (Week 8):**
- ✅ Room database infrastructure setup
- ✅ Bookmark entities and DAOs
- ✅ Bookmark repository layer
- ✅ BookmarksViewModel with favorites filtering
- ✅ BookmarksScreen UI
- ✅ Navigation integration
- ✅ Final integration and testing

**Phase 2 - Epic 2.4 (Week 9):**
- ✅ Theme management (Light/Dark/System)
- ✅ Settings screen implementation
- ✅ Swipe gestures for bookmarks/favorites
- ✅ Custom animations (Material Motion)
- ✅ Accessibility features (TalkBack, preferences)

### In Progress

- 🔄 Phase 3: AI Integration Foundation (Planning Complete)
  - Epic 3.1: OpenRouter Setup (API key management, Retrofit client)
  - Epic 3.2: AI Service Architecture (summarization, usage tracking)
  - Epic 3.3: Model Selection (free models: Llama, Gemma, Mistral)
  - Epic 3.4: Article Summarization (caching, UI integration)

### Recently Completed

- ✅ Epic 2.5: Settings & Preferences (67% - core features done)
  - Notification preferences
  - Cache management
  - Feed/Privacy DataStores

### Next Up

- ⏳ Epic 3.5: Advanced AI Features (sentiment analysis, translation)
- ⏳ Phase 4: Production Polish & Testing

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
