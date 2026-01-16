package com.example.nexusnews.presentation.screens.search

import androidx.lifecycle.viewModelScope
import com.example.nexusnews.data.local.datastore.SearchHistoryDataStore
import com.example.nexusnews.domain.model.Article
import com.example.nexusnews.domain.model.NewsCategory
import com.example.nexusnews.domain.model.SortType
import com.example.nexusnews.domain.repository.NewsRepository
import com.example.nexusnews.presentation.common.BaseViewModel
import com.example.nexusnews.presentation.common.UiState
import com.example.nexusnews.presentation.common.toUiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject

/**
 * ViewModel for the Search screen.
 * Manages search state with debouncing, filtering, and search history.
 */
@OptIn(FlowPreview::class)
@HiltViewModel
class SearchViewModel
    @Inject
    constructor(
        private val newsRepository: NewsRepository,
        private val searchHistoryDataStore: SearchHistoryDataStore,
    ) : BaseViewModel<UiState<List<Article>>>(UiState.Idle) {
        private val _searchQuery = MutableStateFlow("")
        val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()

        private val _selectedCategory = MutableStateFlow<NewsCategory?>(null)
        val selectedCategory: StateFlow<NewsCategory?> = _selectedCategory.asStateFlow()

        private val _selectedSort = MutableStateFlow(SortType.RELEVANCY)
        val selectedSort: StateFlow<SortType> = _selectedSort.asStateFlow()

        // Alias state to uiState for compatibility with View
        val uiState: StateFlow<UiState<List<Article>>> = state

        /**
         * Search history as a StateFlow.
         */
        val searchHistory: StateFlow<List<String>> =
            searchHistoryDataStore.searchHistory.stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(5000),
                initialValue = emptyList(),
            )

        init {
            // Set up debounced search
            viewModelScope.launch(exceptionHandler) {
                _searchQuery
                    .debounce(DEBOUNCE_DELAY_MS) // Wait 300ms after user stops typing
                    .distinctUntilChanged()
                    .filter { it.isNotBlank() } // Only search for non-empty queries
                    .collect { query ->
                        performSearch(query)
                    }
            }
        }

        /**
         * Update search query. Search will be triggered automatically after debounce.
         */
        fun updateSearchQuery(query: String) {
            _searchQuery.update { query }
            if (query.isBlank()) {
                updateState { UiState.Idle }
            }
        }

        /**
         * Select a query from history and trigger search.
         */
        fun selectHistoryItem(query: String) {
            _searchQuery.update { query }
            performSearch(query)
        }

        /**
         * Remove a query from search history.
         */
        fun removeFromHistory(query: String) {
            viewModelScope.launch(exceptionHandler) {
                searchHistoryDataStore.removeSearchQuery(query)
            }
        }

        /**
         * Clear all search history.
         */
        fun clearHistory() {
            viewModelScope.launch(exceptionHandler) {
                searchHistoryDataStore.clearSearchHistory()
            }
        }

        /**
         * Update selected category filter.
         */
        fun updateCategory(category: NewsCategory?) {
            _selectedCategory.update { category }
            // Re-trigger search if query exists
            if (_searchQuery.value.isNotBlank()) {
                performSearch(_searchQuery.value)
            }
        }

        /**
         * Update sort type.
         */
        fun updateSortType(sortType: SortType) {
            _selectedSort.update { sortType }
            // Re-trigger search if query exists
            if (_searchQuery.value.isNotBlank()) {
                performSearch(_searchQuery.value)
            }
        }

        /**
         * Clear all filters.
         */
        fun clearFilters() {
            _selectedCategory.update { null }
            _selectedSort.update { SortType.RELEVANCY }
            if (_searchQuery.value.isNotBlank()) {
                performSearch(_searchQuery.value)
            }
        }

        /**
         * Perform search with current query and filters.
         */
        private fun performSearch(query: String) {
            viewModelScope.launch(exceptionHandler) {
                Timber.d("Performing search with query: $query, category: ${_selectedCategory.value}, sort: ${_selectedSort.value}")

                updateState { UiState.Loading }

                // Save to search history
                searchHistoryDataStore.addSearchQuery(query)

                newsRepository
                    .searchArticles(query)
                    .collect { result ->
                        val uiState = result.toUiState()
                        updateState { uiState }

                        when (uiState) {
                            is UiState.Success -> {
                                val filteredArticles =
                                    filterAndSortArticles(
                                        uiState.data,
                                        _selectedCategory.value,
                                        _selectedSort.value,
                                    )
                                updateState { UiState.Success(filteredArticles) }
                                Timber.d("Search successful: ${filteredArticles.size} articles found")
                            }
                            is UiState.Error -> {
                                Timber.e(uiState.throwable, "Search failed: ${uiState.message}")
                            }
                            else -> {}
                        }
                    }
            }
        }

        /**
         * Filter and sort articles based on selected options.
         * Note: API-level filtering would be more efficient, but this provides client-side refinement.
         */
        @Suppress("UnusedPrivateMember")
        private fun filterAndSortArticles(
            articles: List<Article>,
            category: NewsCategory?,
            sortType: SortType,
        ): List<Article> {
            var filtered = articles

            // Filter by category if selected
            // Note: NewsAPI search doesn't return category info, so this is a placeholder
            // In a real app, you'd filter via API parameters or store category metadata
            if (category != null) {
                // Placeholder for category filtering
                // filtered = filtered.filter { it.category == category }
            }

            // Sort articles
            filtered =
                when (sortType) {
                    SortType.PUBLISHED_AT -> filtered.sortedByDescending { it.publishedAt }
                    SortType.RELEVANCY -> filtered // Already sorted by relevance from API
                    SortType.POPULARITY -> filtered // Would need popularity metric from API
                }

            return filtered
        }

        companion object {
            private const val DEBOUNCE_DELAY_MS = 300L
        }
    }
