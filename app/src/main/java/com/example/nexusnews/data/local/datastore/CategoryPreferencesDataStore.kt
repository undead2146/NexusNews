package com.example.nexusnews.data.local.datastore

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.core.stringSetPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.example.nexusnews.domain.model.NewsCategory
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

/**
 * DataStore for managing category preferences.
 * Stores user's selected category and favorite categories.
 */
@Singleton
class CategoryPreferencesDataStore
    @Inject
    constructor(
        @ApplicationContext private val context: Context,
    ) {
        private val Context.categoryDataStore: DataStore<Preferences> by preferencesDataStore(
            name = CATEGORY_PREFERENCES_NAME,
        )

        /**
         * Flow of the currently selected category.
         * Returns null if "All" categories are selected.
         */
        val selectedCategory: Flow<NewsCategory?> =
            context.categoryDataStore.data.map { preferences ->
                preferences[SELECTED_CATEGORY_KEY]?.let { categoryValue ->
                    try {
                        NewsCategory.entries.find { it.value == categoryValue }
                    } catch (e: IllegalArgumentException) {
                        null
                    }
                }
            }

        /**
         * Flow of favorite category values.
         * Returns a set of category value strings.
         */
        val favoriteCategories: Flow<Set<String>> =
            context.categoryDataStore.data.map { preferences ->
                preferences[FAVORITE_CATEGORIES_KEY] ?: emptySet()
            }

        /**
         * Sets the selected category.
         *
         * @param category The category to select, or null for "All"
         */
        suspend fun setSelectedCategory(category: NewsCategory?) {
            context.categoryDataStore.edit { preferences ->
                if (category != null) {
                    preferences[SELECTED_CATEGORY_KEY] = category.value
                } else {
                    preferences.remove(SELECTED_CATEGORY_KEY)
                }
            }
        }

        /**
         * Toggles a category in the favorites list.
         * If the category is already a favorite, it will be removed.
         * If it's not a favorite, it will be added.
         *
         * @param category The category to toggle
         */
        suspend fun toggleFavoriteCategory(category: NewsCategory) {
            context.categoryDataStore.edit { preferences ->
                val currentFavorites = preferences[FAVORITE_CATEGORIES_KEY] ?: emptySet()
                val newFavorites =
                    if (currentFavorites.contains(category.value)) {
                        currentFavorites - category.value
                    } else {
                        currentFavorites + category.value
                    }
                preferences[FAVORITE_CATEGORIES_KEY] = newFavorites
            }
        }

        /**
         * Clears all category preferences.
         */
        suspend fun clearPreferences() {
            context.categoryDataStore.edit { preferences ->
                preferences.clear()
            }
        }

        companion object {
            private const val CATEGORY_PREFERENCES_NAME = "category_preferences"
            private val SELECTED_CATEGORY_KEY = stringPreferencesKey("selected_category")
            private val FAVORITE_CATEGORIES_KEY = stringSetPreferencesKey("favorite_categories")
        }
    }
