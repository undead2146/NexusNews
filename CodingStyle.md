# Kotlin Coding Style Guide - NexusNews

This document outlines the coding conventions for the NexusNews Android project.
The goal is to ensure consistency, readability, and maintainability across the Kotlin codebase.
These conventions are adapted from the [Microsoft C# Coding Conventions](https://learn.microsoft.com/en-us/dotnet/csharp/fundamentals/coding-style/coding-conventions),
with Kotlin-specific practices from [Kotlin Coding Conventions](https://kotlinlang.org/docs/coding-conventions.html),
[Android Kotlin Style Guide](https://developer.android.com/kotlin/style-guide), and project-specific preferences.

---

## 1. General Principles

- **Consistency**: Follow these conventions throughout the codebase.
- **Readability**: Prioritize code clarity and maintainability.
- **Common Sense**: Use good judgment; code reviews will enforce readability and style.
- **Android Standards**: Follow Android development best practices and Jetpack guidelines.

---

## 2. Indentation and Formatting

- **Indentation**: Use 4 spaces per indentation level (no tabs).
- **Line Length**: Maximum 100 characters per line for better readability on various screen sizes. Enforce via KTlint configuration.
- **Braces**: Use K&R style (opening brace on the same line as the declaration) for all constructs (functions, classes, if/else, loops, etc.).
- **Blank Lines**: Use single blank lines to separate logical sections of code.
- **Whitespace**:
  - Use spaces around binary operators (e.g., `a + b`).
  - No spaces after method names before parentheses (e.g., `methodName()`).
  - No spaces before commas, semicolons, or colons.
  - Spaces after commas and colons.
- **Comments**:
  - Use `//` for single-line comments.
  - Use `/* */` for multi-line comments only when necessary.
  - Place comments on their own line above the code they describe.
  - Use KDoc for public APIs: `/** Comment */`.

---

## 3. Naming Conventions

- **Packages**: All lowercase, no underscores (e.g., `com.example.nexusnews.data`).
- **Classes, Interfaces, Objects, Enums**: PascalCase (e.g., `NewsArticle`, `NewsRepository`).
- **Functions, Properties, Methods**: camelCase (e.g., `getArticles()`, `articleTitle`).
- **Constants**: UPPER_SNAKE_CASE (e.g., `MAX_RETRY_COUNT`).
- **Variables**:
  - camelCase for local variables and parameters.
  - Backing properties: `_camelCase` with underscore prefix.
- **Type Parameters**: Single uppercase letters (e.g., `T`, `U`, `V`).
- **Test Methods**: camelCase with backticks for readability (e.g., `` `should return empty list when no articles` ``).
- **Composable Functions**: PascalCase, prefixed with descriptive names (e.g., `ArticleCard`, `NewsFeed`).

---

## 4. Class Structure and Ordering

**Class Member Order** (strict):

1. Companion objects
2. Constants and immutable properties
3. Mutable properties
4. Constructors (primary first, then secondary)
5. Initialization blocks
6. Methods (grouped by functionality)
   - Public methods first, then protected, internal, private
   - Override methods grouped together
   - Static/extension methods at the end

**Example:**

```kotlin
class NewsRepository @Inject constructor(
    private val apiService: NewsApiService,
    private val articleDao: ArticleDao
) {

    companion object {
        private const val CACHE_DURATION = 30 * 60 * 1000L // 30 minutes
    }

    // Public methods
    suspend fun getArticles(category: String): Result<List<Article>> {
        // Implementation
    }

    // Private methods
    private fun isCacheValid(timestamp: Long): Boolean {
        return System.currentTimeMillis() - timestamp < CACHE_DURATION
    }
}
```

---

## 5. Kotlin-Specific Features

- **Null Safety**: Use nullable types (`?`) explicitly and handle nulls appropriately.
- **Data Classes**: Use for simple data holders; avoid adding logic beyond `equals()`, `hashCode()`, `toString()`.
- **Sealed Classes**: Use for restricted class hierarchies (e.g., `Result`, `UiState`).
- **Inline Functions**: Use for performance-critical code with lambda parameters.
- **Extension Functions**: Use to extend existing classes without inheritance.
- **Scope Functions**: Prefer `apply`, `let`, `run`, `with`, `also` appropriately:
  - `apply`: Configure object and return it
  - `let`: Execute lambda on non-null object
  - `run`: Execute lambda and return result
  - `with`: Execute lambda in context of object
  - `also`: Execute lambda and return original object
- **Coroutines**: Use structured concurrency (`viewModelScope`, `lifecycleScope`); prefer `StateFlow` for UI state over `LiveData`.

---

## 6. Android-Specific Guidelines

- **ViewModels**: Use `viewModelScope` for coroutines; avoid direct context references.
- **LiveData/Flow**: Prefer `StateFlow` and `SharedFlow` over `LiveData` for new code.
- **Dependency Injection**: Use Hilt for DI; follow constructor injection pattern.
- **Resources**: Access via `Context` extensions; avoid hardcoded strings.
- **Activities/Fragments**: Keep thin; delegate logic to ViewModels.
- **Composable Functions**: Follow Compose naming conventions (PascalCase).

**Jetpack Compose Guidelines:**

- Use `@Composable` functions for UI components.
- Prefer `remember` for state that survives recomposition.
- Use `LaunchedEffect` for side effects.
- Follow Material Design 3 principles.
- Use semantic colors from the theme.
- **Composable Ordering**: Group parameters (e.g., `@Composable fun ArticleCard(article: Article, onClick: () -> Unit) { }`); use `@Preview` for previews.

---

## 7. Error Handling and Logging

- **Exceptions**: Use custom exceptions for domain-specific errors.
- **Result Type**: Use `kotlin.Result` or custom sealed classes for operation results.
- **Logging**: Use Timber for logging; avoid `println()` or `Log.d()` directly.
- **Error States**: Handle loading, success, and error states explicitly in UI.

---

## 8. Testing Guidelines

- **Unit Tests**: Test business logic in isolation; use JUnit 5 and MockK.
- **Integration Tests**: Test component interactions.
- **UI Tests**: Use Espresso for instrumentation tests.
- **Test Naming**: Follow `should_when_then` pattern or descriptive names.
- **Test Data**: Use factories or builders for test data creation.

---

## 9. Documentation

- **KDoc**: Required for all public APIs.
- **Inline Comments**: Use sparingly; prefer self-documenting code.
- **README**: Keep updated with setup and usage instructions.

---

## 10. Version Control and Commits

- **Conventional Commits**: Use conventional commit messages for all commits and pull requests (PRs) to maintain a clear, structured history. Follow the format: `<type>[optional scope]: <description>`. Common types include:
  - `feat`: A new feature.
  - `fix`: A bug fix.
  - `docs`: Documentation changes.
  - `style`: Formatting, missing punctuation, etc. (no code change).
  - `refactor`: Code changes that neither fix a bug nor add a feature.
  - `test`: Adding or correcting tests.
  - `chore`: Maintenance tasks (e.g., updating dependencies).

  Examples:
  - `feat(news): add article caching mechanism`
  - `fix(ui): resolve article card layout overflow`
  - `refactor(api): migrate to Retrofit for network calls`

  Keep the subject line under 50 characters, body under 72 per line. For PRs, use the same format for the title; include motivation, changes, and related issues in the body.

---

## 11. Tooling and Enforcement

- **Android Studio**: Use default Kotlin formatting (Settings > Editor > Code Style > Kotlin).
- **KTlint**: Integrated for code style checking.
- **Detekt**: Used for code quality analysis.
- **Pre-commit Hooks**: Run linting and tests before commits.
- **CI/CD**: Automated checks for style and quality.
- **KTlint Configuration**: Customize `.editorconfig` or `ktlint.yml` to enforce 100-char limit, brace style, and naming (e.g., `indent: { size: 4 }`).

---

## 12. Architecture Patterns

- **MVVM**: Follow Model-View-ViewModel pattern.
- **Clean Architecture**: Separate concerns into layers (Presentation, Domain, Data).
- **Repository Pattern**: Abstract data sources behind repository interfaces.
- **Use Cases**: Encapsulate business logic in use case classes.

---

Adhering to these conventions will help keep the NexusNews codebase clean, consistent, and easy to maintain. Regular code reviews will ensure compliance and continuous improvement.
