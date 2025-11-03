---
schema: ai-context-v1
type: project-metadata
generated: 2025-11-03T00:00:00Z
purpose: Structured context
---

# NexusNews  Context

This file provides structured metadata to understand the project.

## Project Identity

```json
{
  "name": "NexusNews",
  "fullName": "NexusNews AI - Multi-Source News Aggregator",
  "version": "1.0.0",
  "type": "Android Mobile Application",
  "status": "In Development",
  "repository": "https://github.com/undead2146/NexusNews",
  "documentation": "https://undead2146.github.io/NexusNews/"
}
```

## Technology Stack

```json
{
  "platform": "Android",
  "minSdk": 24,
  "targetSdk": 36,
  "compileSdk": 36,
  "language": "Kotlin 2.0.21",
  "buildTool": "Gradle 8.13.0",
  "ui": "Jetpack Compose (Material 3)",
  "architecture": "MVVM + Clean Architecture",
  "dependencyInjection": "Hilt (Dagger)",
  "database": "Room SQLite",
  "networking": "Retrofit + OkHttp",
  "imageLoading": "Coil",
  "webScraping": "Jsoup",
  "backgroundTasks": "WorkManager",
  "logging": "Timber"
}
```

## Key Features

```json
{
  "features": [
    {
      "name": "Multi-Source News Aggregation",
      "sources": ["NewsAPI", "Guardian API", "HBVL (scraper)", "GvA (scraper)"],
      "status": "planned"
    },
    {
      "name": "OpenRouter AI Integration",
      "capabilities": ["summarization", "sentiment-analysis", "translation", "recommendations"],
      "models": ["GPT-4o-mini", "Claude Haiku", "Claude Sonnet", "Gemini"],
      "status": "planned"
    },
    {
      "name": "Offline-First Architecture",
      "components": ["Room caching", "DataStore preferences", "Background sync"],
      "status": "planned"
    },
    {
      "name": "Material Design 3 UI",
      "themes": ["light", "dark"],
      "accessibility": true,
      "status": "in-progress"
    }
  ]
}
```

## Architecture Layers

```json
{
  "layers": {
    "presentation": {
      "location": "app/src/main/java/com/example/nexusnews/presentation/",
      "components": ["Compose UI", "ViewModels", "Navigation", "UI State"],
      "pattern": "MVVM"
    },
    "domain": {
      "location": "app/src/main/java/com/example/nexusnews/domain/",
      "components": ["Use Cases", "Entities", "Repository Interfaces"],
      "pattern": "Clean Architecture"
    },
    "data": {
      "location": "app/src/main/java/com/example/nexusnews/data/",
      "components": ["Repositories", "Local Data Sources", "Remote Data Sources", "Scrapers", "AI Client"],
      "pattern": "Repository Pattern"
    }
  }
}
```

## Dependencies Overview

```json
{
  "categories": {
    "core": ["androidx.core:core-ktx:1.17.0", "androidx.lifecycle:lifecycle-runtime-ktx:2.9.4"],
    "ui": ["Compose BOM 2024.09.00", "Material 3", "Navigation Compose 2.7.6", "Coil 2.5.0"],
    "data": ["Room 2.6.1", "DataStore 1.0.0", "Security Crypto 1.1.0-alpha06"],
    "network": ["Retrofit 3.0.0", "OkHttp 4.12.0", "Moshi 1.15.1", "Jsoup 1.17.2"],
    "di": ["Hilt 2.50"],
    "background": ["WorkManager 2.9.0"],
    "logging": ["Timber 5.0.1"],
    "testing": ["JUnit 4.13.2", "Mockk 1.13.9", "Turbine 1.0.0", "Espresso 3.7.0"]
  },
  "versionsFile": "gradle/libs.versions.toml"
}
```

## API Integrations

```json
{
  "apis": [
    {
      "name": "NewsAPI",
      "url": "https://newsapi.org",
      "type": "REST API",
      "authentication": "API Key",
      "rateLimit": "100 requests/day (free tier)",
      "purpose": "International news articles"
    },
    {
      "name": "Guardian API",
      "url": "https://open-platform.theguardian.com",
      "type": "REST API",
      "authentication": "API Key",
      "rateLimit": "5000 requests/day (free tier)",
      "purpose": "UK and global news"
    },
    {
      "name": "OpenRouter",
      "url": "https://openrouter.ai",
      "type": "AI API",
      "authentication": "API Key",
      "pricing": "Pay-per-token",
      "purpose": "AI features (summarization, sentiment, translation)"
    }
  ],
  "scrapers": [
    {
      "name": "HBVL",
      "url": "https://www.hbvl.be",
      "language": "Dutch",
      "purpose": " local news"
    },
    {
      "name": "GvA",
      "url": "https://www.gva.be",
      "language": "Dutch",
      "purpose": " news"
    }
  ]
}
```

## Project Structure

```json
{
  "rootFiles": [
    "README.md - Project overview and setup instructions",
    "build.gradle.kts - Root Gradle configuration",
    "settings.gradle.kts - Gradle settings",
    "gradle/libs.versions.toml - Centralized dependency versions",
    "local.properties - Local SDK paths (gitignored)"
  ],
  "appModule": "app/",
  "documentation": "docs/",
  "notes": "Notes/",
  "ciCd": ".github/workflows/"
}
```

## Development Workflow

```json
{
  "branchStrategy": "GitHub Flow",
  "mainBranch": "main",
  "cicd": {
    "tool": "GitHub Actions",
    "workflows": [
      "android-ci.yml - Build and test on push",
      "deploy.yml - Deploy docs to GitHub Pages"
    ]
  },
  "codeQuality": {
    "linting": "Ktlint",
    "staticAnalysis": "Detekt",
    "testing": "JUnit + Mockk + Turbine"
  }
}
```

## Current Phase

```json
{
  "phase": "Phase 0: Project Foundation & Setup",
  "status": "In Progress",
  "completedEpics": [
    "0.1: Project Infrastructure",
    "0.2: Development Tools (partial)"
  ],
  "nextEpics": [
    "0.3: Core Architecture",
    "1.1: Network Layer Foundation"
  ]
}
```

## Documentation Map

```json
{
  "structure": {
    "/": "Documentation home",
    "/architecture/": "System design and patterns",
    "/development/": "Setup guides and coding standards",
    "/api/": "Dependencies and API references",
    "/project/": "Roadmap, PRD, and project management",
    "/weekly/": "Development progress reports",
    "/ai-context/": "AI agent context files (this section)"
  },
  "keyDocuments": {
    "overview": "/project/overview.md",
    "roadmap": "/project/roadmap.md",
    "prd": "/project/prd.md",
    "dependencies": "/api/dependencies.md",
    "architecture": "/architecture/index.md",
    "setup": "/development/setup.md",
    "codingStyle": "/development/coding-style.md"
  }
}
```

## Instructions

When assisting with this project:

1. **Always check version compatibility** in `gradle/libs.versions.toml`
2. **Follow Clean Architecture** - respect layer boundaries
3. **Use Hilt for DI** - all dependencies should be injected
4. **Follow MVVM pattern** - ViewModels should not reference Android framework classes
5. **Write tests** - Unit tests for logic, UI tests for critical flows
6. **Check coding style** at `/development/coding-style.md`
7. **Update documentation** when making significant changes
8. **Reference the roadmap** at `/project/roadmap.md` for feature priorities

## Common Tasks Reference

```json
{
  "tasks": {
    "build": "./gradlew build",
    "test": "./gradlew test",
    "assembleDebug": "./gradlew assembleDebug",
    "lint": "./gradlew ktlintCheck",
    "format": "./gradlew ktlintFormat",
    "detekt": "./gradlew detekt",
    "clean": "./gradlew clean"
  }
}
```

## Environment Variables Required

```json
{
  "required": [
    "NEWSAPI_KEY - NewsAPI.org API key",
    "GUARDIAN_API_KEY - Guardian API key",
    "OPENROUTER_API_KEY - OpenRouter API key"
  ],
  "optional": [],
  "storage": "Encrypted in app using AndroidX Security Crypto"
}
```
