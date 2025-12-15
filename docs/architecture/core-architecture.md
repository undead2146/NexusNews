# Core Architecture

## Overview

NexusNews follows Clean Architecture principles with MVVM pattern for the presentation layer.

## Constants Organization

Constants are organized into separate classes by category for better maintainability:

- **NetworkConstants:** Network configuration, timeouts, retry policies
- **ApiConstants:** HTTP headers, status codes, API response formats
- **DatabaseConstants:** Database schema, table/column names
- **UiConstants:** UI dimensions, animations, layout values
- **AppConstants:** General application settings and preferences

### Usage Example

```kotlin
import com.example.nexusnews.util.constants.NetworkConstants.CONNECT_TIMEOUT_SECONDS
import com.example.nexusnews.util.constants.ApiConstants.HTTP_OK
import com.example.nexusnews.util.constants.DatabaseConstants.ARTICLES_TABLE
```

## Architecture Layers

### 1. Domain Layer (Business Logic)

- **Location:** `app/src/main/java/com/example/nexusnews/domain/`
- **Purpose:** Contains business logic and models independent of frameworks
- **Components:**
  - **Models:** Pure Kotlin data classes representing business entities
  - **Repository Interfaces:** Contracts for data operations
  - **Use Cases:** Single-responsibility business operations

**Key Principles:**

- No Android framework dependencies
- Framework-agnostic
- Defines interfaces, not implementations

### 2. Data Layer (Data Management)

- **Location:** `app/src/main/java/com/example/nexusnews/data/`
- **Purpose:** Manages data from various sources (API, database, cache, preferences)
- **Components:**
  - **Repositories:** Implement domain repository interfaces
  - **Data Sources:** Local (Room, DataStore) and Remote (Retrofit)
  - **Mappers:** Convert between data models and domain models

**Patterns:**
- Repository Pattern: Single source of truth
- Offline-First: Cache-then-network strategy
- Preferences Persistence: Type-safe DataStore for user settings
  - Theme & Accessibility
  - Notifications & Privacy
  - Feed Customization
  - AI Model Configuration
- Secure Storage: EncryptedSharedPreferences for API keys

### 2.5. AI Service Layer

- **Location:** `app/src/main/java/com/example/nexusnews/data/ai/`
- **Purpose:** Integrates AI-powered features via OpenRouter API
- **Components:**
  - **AiService Interface:** Domain contract for AI operations
  - **OpenRouterAiService:** Implementation with Retrofit
  - **FreeAiModel:** Configuration for 6 free models
  - **Prompt Engineering:** Optimized prompts for summarization, sentiment, translation
  - **ApiKeyDataStore:** Secure encrypted storage for API keys

**Features:**
- Article summarization (150 chars)
- Sentiment analysis (POSITIVE/NEUTRAL/NEGATIVE)
- Translation support
- Free tier: 50 requests/day, 20 requests/minute


### 3. Presentation Layer (UI)

- **Location:** `app/src/main/java/com/example/nexusnews/presentation/`
- **Purpose:** Handles UI logic, user interactions, and visual presentation
- **Components:**
  - **Composables:** Jetpack Compose UI components with Material Design 3
  - **ViewModels:** UI state management and persistence logic
  - **Navigation:** Type-safe screen routing
  - **Animations:** Material Motion transitions (Shared Element, Fade, Scale)

**Patterns:**
- MVVM: Separation of UI and business logic
- Unidirectional Data Flow: State flows down, events flow up
- Accessibility-First: TalkBack support, dynamic scaling, and semantic properties
- State Management: StateFlow for reactive UI updates

## Network Layer Architecture

### Network Layer Overview

The network layer provides a robust, reactive foundation for API communication with comprehensive error handling, retry logic, and monitoring capabilities.

### Network Components

#### 1. NetworkMonitor

- **Purpose:** Real-time network connectivity monitoring
- **Implementation:** Uses ConnectivityManager with Flow-based reactive streams
- **Features:**
  - Connectivity state observation
  - Network type detection (WiFi, Cellular, Ethernet)
  - Reactive updates for UI adaptation

#### 2. RetryPolicy

- **Purpose:** Configurable retry logic with exponential backoff
- **Features:**
  - Exponential backoff with jitter
  - Configurable retry attempts and delays
  - Selective retry based on exception types
  - Custom retry conditions

#### 3. OkHttp Interceptors

- **AuthInterceptor:** API key and authentication header injection
- **ErrorInterceptor:** HTTP error parsing and typed exception throwing
- **RetryInterceptor:** Automatic retry logic integration

#### 4. NetworkDataSource

- **Purpose:** Base class for network operations
- **Features:**
  - Connectivity checking before requests
  - Automatic retry with policy
  - Standardized error handling
  - Flow-based response wrapping

#### 5. API Response Models

- **ApiResponse&lt;T&gt;:** Sealed class for success/error states
- **ErrorResponse:** Structured error information from APIs
- **NetworkException:** Typed exception hierarchy

### Network Request Flow

```mermaid
Request → Connectivity Check → Auth Headers → API Call → Error Handling
     ↓                                                    ↓
Retry Logic ← Response Processing ← Success/Error Parsing
```

### Network Error Handling Strategy

#### Typed Network Exceptions

- **UnauthorizedException:** 401 responses
- **ForbiddenException:** 403 responses
- **NotFoundException:** 404 responses
- **RateLimitException:** 429 responses
- **ServerException:** 5xx responses
- **HttpException:** Other 4xx responses

#### Error Processing Pipeline

1. **ErrorInterceptor:** Parses HTTP responses into typed exceptions
2. **RetryInterceptor:** Applies retry logic for retryable errors
3. **NetworkDataSource:** Wraps results in ApiResponse sealed class
4. **Repository:** Converts to domain Result types
5. **ViewModel:** Maps to UiState for presentation

### Network Dependency Injection

Network components are configured via Hilt modules:

```kotlin
@Module
@InstallIn(SingletonComponent::class)
object NetworkModule {
    @Provides
    fun provideOkHttpClient(
        authInterceptor: AuthInterceptor,
        retryInterceptor: RetryInterceptor,
        errorInterceptor: ErrorInterceptor
    ): OkHttpClient

    @Provides
    fun provideRetrofit(okHttpClient: OkHttpClient): Retrofit
}
```

### Network Testing Strategy

#### Unit Testing

- **NetworkMonitor:** Connectivity state testing with mock Context
- **RetryPolicy:** Backoff calculation and retry logic testing
- **Interceptors:** HTTP request/response manipulation testing
- **NetworkDataSource:** Error handling and Flow emission testing

#### Integration Testing

- **MockWebServer:** End-to-end API call simulation
- **Interceptor Chain:** Full request pipeline testing
- **Error Scenarios:** Network failures and API errors

### Network Configuration Constants

Network behavior is controlled by constants:

```kotlin
object NetworkConstants {
    const val CONNECT_TIMEOUT_SECONDS = 30L
    const val READ_TIMEOUT_SECONDS = 30L
    const val MAX_RETRY_ATTEMPTS = 3
    const val RETRY_BACKOFF_MULTIPLIER = 2.0
}
```

## Data Flow

```mermaid
User Action → ViewModel → Use Case → Repository → Data Source
                ↓
            UI State ← ViewModel ← Use Case ← Repository ← Data Source
```

## Error Handling Strategy

### Layered Error Handling

1. **Data Layer:** Catches network/database exceptions
2. **Domain Layer:** Wraps in Result sealed class
3. **Presentation Layer:** Converts to user-friendly messages

### Result Type

```kotlin
sealed class Result<out T> {
    data class Success<T>(val data: T) : Result<T>()
    data class Error(val exception: Throwable) : Result<Nothing>()
    data object Loading : Result<Nothing>()
}
```

### UiState Type

```kotlin
sealed interface UiState<out T> {
    data object Idle : UiState<Nothing>
    data object Loading : UiState<Nothing>
    data class Success<T>(val data: T) : UiState<T>
    data class Error(val message: String) : UiState<Nothing>
}
```

## Navigation Architecture

### Type-Safe Navigation

- Sealed class defines all routes
- NavHost manages navigation graph
- Extension functions for common navigation patterns

### Example

```kotlin
sealed class Screen(val route: String) {
    data object Home : Screen("home")
    data object ArticleDetail : Screen("article/{articleId}") {
        fun createRoute(articleId: String) = "article/$articleId"
    }
}
```

## Testing Strategy

### Unit Tests

- **Domain Layer:** Test use cases with mock repositories
- **Data Layer:** Test repositories with mock data sources
- **Presentation Layer:** Test ViewModels with mock use cases

### Test Tools

- JUnit 4 for unit tests
- Mockk for mocking
- Turbine for Flow testing
- Coroutines Test for suspending functions

## Best Practices

1. **Dependency Rule:** Dependencies point inward (presentation → domain ← data)
2. **Single Responsibility:** Each class has one reason to change
3. **Dependency Injection:** Use Hilt for all dependencies
4. **Immutability:** Prefer immutable data classes
5. **Flow over LiveData:** Use Kotlin Flow for reactive streams
6. **Sealed Classes:** For type-safe state management
7. **Extension Functions:** For reusable logic
8. **Timber for Logging:** Structured logging across app

## Future Enhancements

- Add pagination support in BaseRepository
- Implement caching strategies (TTL, size limits)
- Add analytics tracking layer
- Implement feature flags
