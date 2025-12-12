# Report 08/12 Week 7 - Epic 2.2: Categories & Topics

| Description | Commit Message |
|-------------|----------------|
| Added tags field to Article domain model to support topic/tag system for better content discovery. Default empty list ensures backward compatibility. | `feat(domain): add tags field to Article model` |
| Implemented tag extraction in ArticleMapper using regex pattern matching to identify capitalized words/phrases from article titles and descriptions. Returns up to 5 unique relevant tags per article. | `feat(data): implement tag extraction in ArticleMapper` |
| Created CategoryPreferencesDataStore singleton for managing user category preferences using DataStore. Supports selected category tracking, favorite categories management, and preference persistence. | `feat(data): create CategoryPreferencesDataStore for category preferences` |
| Enhanced NewsCategory enum with Material icons, accent colors, and descriptions for each category to support rich UI presentation. Added proper Compose imports. | `feat(domain): enhance NewsCategory enum with icons and colors` |
| Added category-specific colors to theme (BusinessBlue, EntertainmentPurple, etc.) for visual distinction between different news categories. | `feat(ui): add category-specific colors to theme` |
| Enhanced NewsListViewModel with category filtering support using CategoryPreferencesDataStore. Added selectedCategory StateFlow and selectCategory function to persist user's category selection. | `feat(presentation): add category filtering to NewsListViewModel` |
| Added horizontal category selector to NewsListScreen with FilterChips for each category. Includes "All" option, category icons with accent colors, and smooth scrolling. Selection persists via DataStore. | `feat(ui): add category selector to NewsListScreen` |
| Created TagChip and TagRow composables for displaying article tags in a horizontal scrollable list using Material 3 AssistChip. | `feat(ui): create TagChip and TagRow components` |
| Integrated TagRow into ArticleItem to display article tags below metadata. Tags only show when available. | `feat(ui): add tag display to ArticleItem` |
