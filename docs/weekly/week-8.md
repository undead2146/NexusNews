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

