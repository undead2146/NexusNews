# Report 15/12 Week 8 - Epic 2.3: Bookmarks & Favorites

| Description | Commit Message |
|-------------|----------------|
| Created Room type converters for LocalDateTime and List<String> to support Article tags and database operations. | `feat(data): add Room type converters for database` |
| Created ArticleEntity for Room database with all article fields including tags and cache timestamp for offline support. | `feat(data): create ArticleEntity for local caching` |
| Created BookmarkEntity with foreign key to ArticleEntity and favorite flag support for bookmark management. | `feat(data): create BookmarkEntity with favorites support` |
| Created ArticleDao with CRUD operations for article caching including cache cleanup functionality. | `feat(data): create ArticleDao for article caching` |
| Created BookmarkDao with comprehensive bookmark operations including JOIN queries and favorite management. | `feat(data): create BookmarkDao with bookmark operations` |
| Created AppDatabase with ArticleEntity and BookmarkEntity, including type converters for Room database setup. | `feat(data): create AppDatabase for Room persistence` |
| Created entity mapper extensions for converting between ArticleEntity and domain Article models. | `feat(data): add entity mappers for domain conversion` |
| Created DatabaseModule for Hilt dependency injection of Room database and DAOs. | `feat(di): add DatabaseModule for Room database` |
| Added bookmark and favorites operations to NewsRepository interface for repository layer support. | `feat(domain): add bookmark operations to NewsRepository` |
| Updated NewsRepositoryImpl to inject BookmarkDao and ArticleDao, implementing all bookmark operations with proper error handling. | `feat(data): implement bookmark operations in repository` |
| Created BookmarksViewModel with favorites filtering and bookmark management for presentation layer. | `feat(presentation): create BookmarksViewModel` |
| Added bookmark toggle functionality and isBookmarked check to NewsListViewModel for article bookmark support. | `feat(presentation): add bookmark toggle to NewsListViewModel` |
| Created BookmarksScreen with favorites filter, empty states, and bookmark list display using Material Design 3. | `feat(ui): create BookmarksScreen with favorites filter` |
| Added BookmarksScreen to navigation graph with proper article navigation integration. | `feat(navigation): integrate BookmarksScreen into NavGraph` |
| Added bookmark icon to ArticleItem with isBookmarked state and onBookmarkClick callback for bookmark management. | `feat(ui): add bookmark icon to ArticleItem` |
| Created ThemePreferencesDataStore with ThemeMode enum for persisting user theme preferences (Light/Dark/System). | `feat(data): add theme preferences DataStore` |
| Created ThemeViewModel for app-wide theme state management with reactive updates via StateFlow. | `feat(presentation): create ThemeViewModel` |
| Updated Theme.kt to accept ThemeMode parameter and integrated ThemeViewModel into MainActivity for user-controlled theme. | `feat(ui): update theme system with user control` |
| Created SettingsScreen with Material Design 3 components including theme selector and about section. | `feat(ui): create Settings screen` |
| Created SettingsViewModel to manage settings state and theme preferences with Hilt injection. | `feat(presentation): create SettingsViewModel` |
| Integrated SettingsScreen into NavGraph replacing placeholder implementation. | `feat(navigation): add Settings screen to navigation` |
| Created SwipeableArticleItem with swipe-to-bookmark and swipe-to-favorite gestures using Material 3 SwipeToDismiss API. | `feat(ui): implement swipeable article items` |
| Added toggleFavorite method to NewsListViewModel for favorite management in swipe gestures. | `feat(presentation): add favorite toggle to NewsListViewModel` |
| Integrated SwipeableArticleItem into NewsListScreen with bookmark state tracking and haptic feedback. | `feat(ui): integrate swipeable items in news list` |
| Created centralized animation utilities following Material Motion guidelines with spring animations. | `feat(ui): create animation utilities` |
| Added animated bookmark icon toggle to ArticleItem with scale and bounce effects using AnimatedVisibility. | `feat(ui): add animated bookmark toggle` |
| Added list item placement animations to NewsListScreen for smooth enter/exit transitions. | `feat(ui): add list item placement animations` |
| Created AccessibilityPreferencesDataStore for managing reduce animations, high contrast mode, and font size multiplier settings. | `feat(data): add accessibility preferences DataStore` |
| Created AccessibilityUtils with helper functions for custom actions and screen reader formatting. | `feat(ui): create accessibility utilities` |
| Enhanced ArticleItem with comprehensive TalkBack support including custom accessibility actions and semantic properties. | `feat(ui): enhance ArticleItem accessibility` |

