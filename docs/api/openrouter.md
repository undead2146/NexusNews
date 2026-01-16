---
title: OpenRouter API Integration
description: Documentation for OpenRouter AI service integration
category: api
lastUpdated: 2026-01-16
aiContext: true
tags: [ai, openrouter, api, integration]
---

# OpenRouter API Integration

NexusNews uses [OpenRouter](https://openrouter.ai/) as a unified gateway to access various LLMs (Large Language Models) for intelligent features. This integration allows switching between models (OpenAI, Anthropic, Google, Meta, etc.) without changing client code.

## 🏗️ Implementation Overview

The integration is encapsulated in `OpenRouterAiService` and follows Clean Architecture principles.

- **Service**: `com.example.nexusnews.data.ai.OpenRouterAiService`
- **DI Module**: `com.example.nexusnews.di.AiModule`
- **Configuration**: Dynamic model selection via Settings

## 🔑 Authentication

API keys are managed securely using `ApiKeyDataStore` (backed by EncryptedSharedPreferences).

1.  **Storage**: Keys are encrypted at rest.
2.  **Fallback**: If no user key is provided, the app falls back to `BuildConfig.OPENROUTER_API_KEY`.
3.  **Headers**: Requests include `Authorization: Bearer <KEY>` and `HTTP-Referer`.

## 🤖 Supported Features

The `OpenRouterAiService` implements `AiService` interface to provide:

### 1. Article Summarization
- **Method**: `summarizeArticle(content, maxLength)`
- **System Prompt**: "You are a news summarization assistant. Summarize concisely and objectively."
- **Model Parameters**: `temperature=0.3`

### 2. Sentiment Analysis
- **Method**: `analyzeSentiment(content)`
- **Output**: `POSITIVE`, `NEUTRAL`, `NEGATIVE`
- **System Prompt**: Enforces single-word response.

### 3. Translation
- **Method**: `translateArticle(content, targetLanguage)`
- **System Prompt**: "You are a professional translator..."
- **Context**: Preserves tone and meaning.

### 4. Advanced Analysis (Phase 4)
- **Key Points Extraction**: Extracts bullet points of main events.
- **Entity Recognition**: Identifies People, Places, Organizations.
- **Topic Classification**: Categorizes articles (e.g., Politics, Tech).
- **Bias Detection**: Analyzes potential political or tonal bias.
- **Chat Assistant**: Context-aware Q&A about the current article.

## 🔄 Model Strategy

The service implements a **Fallback Mechanism** to ensure reliability:

1.  **Primary Model**: User selected model (default: `meta-llama/llama-3.1-70b-instruct`).
2.  **Fallback Models**: If primary fails (timeout, rate limit), it automatically tries:
    - `google/gemma-2-27b-it`
    - `mistralai/mistral-small`

## 📊 Usage Tracking

All AI requests are tracked in the local Room database (`AiUsageDao`) for monitoring:

- **Metrics**: Token usage (prompt/completion), Latency, Model used.
- **Storage**: `AiUsageEntity`
- **Purpose**: Cost monitoring and performance analysis.

## 📝 Code Example

```kotlin
// Example usage in ViewModel
viewModelScope.launch {
    aiService.summarizeArticle(article.content)
        .onSuccess { summary ->
            _uiState.update { it.copy(summary = summary) }
        }
        .onFailure { error ->
            _uiState.update { it.copy(error = error.message) }
        }
}
```
