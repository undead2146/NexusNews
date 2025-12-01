package com.example.nexusnews.data.local.datastore

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringSetPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

private val Context.searchHistoryDataStore: DataStore<Preferences> by preferencesDataStore(
    name = "search_history",
)

/**
 * DataStore for managing search history.
 * Stores recent search queries with a maximum limit.
 */
@Singleton
class SearchHistoryDataStore
    @Inject
    constructor(
        @ApplicationContext private val context: Context,
    ) {
        private val searchHistoryKey = stringSetPreferencesKey("search_history")

        /**
         * Get search history as a Flow.
         * Returns list sorted by most recent first.
         */
        val searchHistory: Flow<List<String>> =
            context.searchHistoryDataStore.data.map { preferences ->
                val historySet: Set<String>? = preferences[searchHistoryKey]
                historySet?.toList()?.reversed() ?: emptyList()
            }

        /**
         * Add a search query to history.
         * Maintains a maximum of MAX_HISTORY_SIZE entries.
         */
        suspend fun addSearchQuery(query: String) {
            if (query.isBlank()) return

            context.searchHistoryDataStore.edit { preferences ->
                val currentHistory: MutableSet<String> =
                    preferences[searchHistoryKey]?.toMutableSet() ?: mutableSetOf()

                // Remove if already exists (to re-add at the end for recency)
                currentHistory.remove(query.trim())

                // Add the new query
                currentHistory.add(query.trim())

                // Trim to max size (keep most recent)
                val trimmedHistory: Set<String> =
                    if (currentHistory.size > MAX_HISTORY_SIZE) {
                        currentHistory.toList().takeLast(MAX_HISTORY_SIZE).toSet()
                    } else {
                        currentHistory
                    }

                preferences[searchHistoryKey] = trimmedHistory
            }
        }

        /**
         * Remove a specific query from history.
         */
        suspend fun removeSearchQuery(query: String) {
            context.searchHistoryDataStore.edit { preferences ->
                val currentHistory: MutableSet<String> =
                    preferences[searchHistoryKey]?.toMutableSet() ?: mutableSetOf()
                currentHistory.remove(query)
                preferences[searchHistoryKey] = currentHistory
            }
        }

        /**
         * Clear all search history.
         */
        suspend fun clearSearchHistory() {
            context.searchHistoryDataStore.edit { preferences ->
                preferences.remove(searchHistoryKey)
            }
        }

        companion object {
            private const val MAX_HISTORY_SIZE = 20
        }
    }
