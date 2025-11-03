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

**Phase**: Phase 0 - Project Foundation & Setup  
**Status**: In Progress  
**Last Updated**: November 3, 2025

### Completed

- ✅ Project structure setup
- ✅ Gradle configuration with version catalogs
- ✅ CI/CD pipeline (GitHub Actions)
- ✅ Documentation structure (VitePress)
- ✅ Code quality tools (Ktlint, Detekt)
- ✅ Core architecture implementation (MVVM + Clean Architecture)
- ✅ Network layer foundation with monitoring, retry logic, and error handling
- ✅ Comprehensive testing infrastructure (unit + integration tests)
- ✅ Constants restructuring into categorized classes

### In Progress

- 🔄 Documentation completion and updates

### Next Up

- ⏳ Room database setup
- ⏳ First news source integration (NewsAPI)
- ⏳ Basic UI implementation

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
