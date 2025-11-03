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
- **Purpose:** Manages data from various sources (API, database, cache)
- **Components:**
  - **Repositories:** Implement domain repository interfaces
  - **Data Sources:** Local (Room) and Remote (Retrofit)
  - **Mappers:** Convert between data models and domain models

**Patterns:**

- Repository Pattern: Single source of truth
- Offline-First: Cache-then-network strategy
- NetworkBoundResource: Coordinated cache + network

### 3. Presentation Layer (UI)

- **Location:** `app/src/main/java/com/example/nexusnews/presentation/`
- **Purpose:** Handles UI logic and user interactions
- **Components:**
  - **Composables:** Jetpack Compose UI components
  - **ViewModels:** UI state management
  - **Navigation:** Screen routing

**Patterns:**

- MVVM: Separation of UI and business logic
- Unidirectional Data Flow: State flows down, events flow up
- State Management: StateFlow for reactive UI updates

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
