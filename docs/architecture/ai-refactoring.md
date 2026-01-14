# AI Service Refactoring - Clean Architecture Implementation

## Overview

This document describes the refactoring of the AI service implementation to follow clean architecture principles and proper design patterns.

## Problems Identified

### 1. Single Responsibility Principle Violation
The original [`OpenRouterAiService.kt`](../app/src/main/java/com/example/nexusnews/data/ai/OpenRouterAiService.kt) had multiple responsibilities:
- API calls to OpenRouter
- Prompt building for each AI feature
- Response parsing with Moshi
- Usage tracking
- Model fallback logic

### 2. Code Duplication
Significant repetition across methods:
- API key retrieval (`getApiKeyOrThrow()`)
- Primary model selection (`FreeAiModel.getDefault()`)
- `tryModelWithFallback()` calls with similar parameters
- Moshi adapter instantiation

### 3. Maintainability Issues
- 944 lines of code in a single file
- Mixed concerns (data, parsing, business logic)
- Difficult to test individual components
- Hard to extend with new AI features

## Refactoring Solution

### New Architecture

```
data/ai/
├── prompt/              # Prompt builders (Strategy Pattern)
│   ├── AiPromptBuilder.kt
│   ├── KeyPointsPromptBuilder.kt
│   ├── EntityRecognitionPromptBuilder.kt
│   ├── TopicClassificationPromptBuilder.kt
│   ├── BiasDetectionPromptBuilder.kt
│   ├── RecommendationsPromptBuilder.kt
│   ├── ChatPromptBuilder.kt
│   └── ContentGenerationPromptBuilder.kt
├── parser/              # Response parsers (Template Method Pattern)
│   ├── AiResponseParser.kt
│   ├── BaseMoshiParser.kt
│   ├── KeyPointsResponseParser.kt
│   ├── EntityRecognitionResponseParser.kt
│   ├── TopicClassificationResponseParser.kt
│   ├── BiasDetectionResponseParser.kt
│   ├── RecommendationResponseParser.kt
│   ├── ContentGenerationResponseParser.kt
│   └── ChatResponseParser.kt
├── AiServiceConfig.kt   # Configuration classes
├── OpenRouterAiService.kt        # Original implementation (Phase 3)
└── OpenRouterAiServiceRefactored.kt # Refactored implementation (Phase 4)
```

## Design Patterns Applied

### 1. Strategy Pattern
**Purpose**: Encapsulate prompt building logic for different AI features.

**Implementation**:
- [`AiPromptBuilder`](../app/src/main/java/com/example/nexusnews/data/ai/prompt/AiPromptBuilder.kt) - Base interface
- Concrete implementations for each feature type

**Benefits**:
- Easy to add new prompt types
- Each builder has single responsibility
- Testable in isolation
- Clear contract through interface

### 2. Template Method Pattern
**Purpose**: Provide common parsing logic with customizable behavior.

**Implementation**:
- [`BaseMoshiParser`](../app/src/main/java/com/example/nexusnews/data/ai/parser/AiResponseParser.kt) - Abstract base class
- Concrete parsers for each response type

**Benefits**:
- Eliminates code duplication
- Centralized error handling
- Consistent logging
- Easy to add new parsers

### 3. Configuration Object Pattern
**Purpose**: Encapsulate AI service configuration.

**Implementation**:
- [`AiServiceConfig`](../app/src/main/java/com/example/nexusnews/data/ai/AiServiceConfig.kt) - Service-level config
- [`AiRequestConfig`](../app/src/main/java/com/example/nexusnews/data/ai/AiServiceConfig.kt) - Request-level config

**Benefits**:
- Single source of truth for configuration
- Type-safe configuration
- Easy to extend with new parameters

## Code Quality Improvements

### 1. Separation of Concerns

**Before**: All logic in one 944-line file
**After**: Separate files for each concern

| Concern | Location | Purpose |
|----------|----------|---------|
| Prompt Building | `prompt/` | Build prompts for AI features |
| Response Parsing | `parser/` | Parse JSON responses |
| Configuration | `AiServiceConfig.kt` | Manage service configuration |
| Service Logic | `OpenRouterAiServiceRefactored.kt` | Orchestrate AI operations |

### 2. Testability

**Before**: Difficult to test individual components
**After**: Each component can be tested independently

```kotlin
// Example: Testing a prompt builder
val builder = KeyPointsPromptBuilder(content, maxPoints)
val prompt = builder.build()
assertEquals(expected, prompt)
```

### 3. Maintainability

**Before**: Adding new features required modifying a large file
**After**: Add new builder/parser without touching service logic

```kotlin
// Example: Adding new AI feature
// 1. Create prompt builder
class NewFeaturePromptBuilder(...) : AiPromptBuilder { ... }

// 2. Create response parser
class NewFeatureResponseParser : BaseMoshiParser<NewFeatureResult> { ... }

// 3. Add method to AiService interface
suspend fun newFeature(...): Result<NewFeatureResult>

// 4. Implement in service
override suspend fun newFeature(...) = run {
    val prompt = NewFeaturePromptBuilder(...).build()
    val config = AiRequestConfig.forNewFeature()
    executeAiRequest(prompt, config).mapCatching {
        NewFeatureResponseParser().parse(it)
    }
}
```

### 4. Error Handling

**Before**: Scattered error handling across methods
**After**: Centralized in `BaseMoshiParser`

```kotlin
// Consistent error handling
override fun parse(response: String): T {
    return try {
        parseInternal(response)
    } catch (e: Exception) {
        Timber.e(e, "Failed to parse ${this::class.simpleName}")
        defaultValue // Consistent fallback
    }
}
```

## Migration Path

### Phase 1: Coexistence
Both implementations coexist during migration:
- [`OpenRouterAiService.kt`](../app/src/main/java/com/example/nexusnews/data/ai/OpenRouterAiService.kt) - Original (Phase 3)
- [`OpenRouterAiServiceRefactored.kt`](../app/src/main/java/com/example/nexusnews/data/ai/OpenRouterAiServiceRefactored.kt) - Refactored (Phase 4)

### Phase 2: Gradual Migration
Migrate features one at a time:
1. Update DI binding to use refactored service
2. Test each feature with new implementation
3. Remove old implementation when all features verified

### Phase 3: Cleanup
Once migration is complete:
1. Remove `OpenRouterAiService.kt`
2. Rename `OpenRouterAiServiceRefactored.kt` to `OpenRouterAiService.kt`

## Benefits Summary

| Aspect | Before | After |
|---------|--------|-------|
| File Size | 944 lines | ~300 lines (service) + separate components |
| Responsibilities | 5+ concerns | 1 concern per file |
| Testability | Difficult | Easy - each component testable |
| Extensibility | Modify large file | Add new builder/parser |
| Maintainability | Low - mixed concerns | High - clear separation |
| Code Duplication | High | Low - DRY principles applied |
| Type Safety | Good | Excellent - sealed classes, data classes |

## SOLID Principles Applied

### Single Responsibility Principle
Each class has one reason to change:
- `AiPromptBuilder` - Build prompts
- `AiResponseParser` - Parse responses
- `AiServiceConfig` - Manage configuration
- `OpenRouterAiServiceRefactored` - Orchestrate AI operations

### Open/Closed Principle
Easy to extend without modifying existing code:
- Add new `AiPromptBuilder` implementation
- Add new `AiResponseParser` implementation
- Extend `AiRequestConfig` with new factory methods

### Liskov Substitution Principle
Any `AiPromptBuilder` can be used interchangeably:
```kotlin
val builder: AiPromptBuilder = when (featureType) {
    FeatureType.KEY_POINTS -> KeyPointsPromptBuilder(...)
    FeatureType.ENTITY_RECOGNITION -> EntityRecognitionPromptBuilder(...)
    // ...
}
```

### Interface Segregation Principle
Focused interfaces:
- `AiPromptBuilder` - Single method: `build()`
- `AiResponseParser<T>` - Single method: `parse()`
- No unnecessary methods forced on implementers

### Dependency Inversion Principle
Service depends on abstractions:
```kotlin
class OpenRouterAiServiceRefactored(
    private val openRouterApi: OpenRouterApi,  // Abstraction
    private val apiKeyDataStore: ApiKeyDataStore,  // Abstraction
    private val aiUsageDao: AiUsageDao,  // Abstraction
) : AiService {  // Abstraction
    // ...
}
```

## Next Steps

1. **Create Use Cases**: Extract business logic from service layer
2. **Refactor UI Components**: Apply same patterns to UI layer
3. **Add Unit Tests**: Test each builder and parser independently
4. **Integration Tests**: Test service with mock dependencies
5. **Documentation**: Update architecture documentation with new structure

## References

- [Clean Architecture by Robert C. Martin](https://blog.cleancoder.com/uncle-bob/2012/08/13/the-clean-architecture.html)
- [SOLID Principles](https://en.wikipedia.org/wiki/SOLID)
- [Design Patterns: Elements of Reusable Object-Oriented Software](https://www.amazon.com/Design-Patterns-Elements-Reusable-Object-Oriented-Software/dp/0201632680)
