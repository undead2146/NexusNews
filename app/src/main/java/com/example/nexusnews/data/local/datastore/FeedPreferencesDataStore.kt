package com.example.nexusnews.data.local.datastore

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.example.nexusnews.domain.model.NewsCategory
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

/**
 * DataStore for managing feed preferences.
 */
@Singleton
class FeedPreferencesDataStore
    @Inject
    constructor(
        @ApplicationContext private val context: Context,
    ) {
        private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(
            name = FEED_PREFERENCES_NAME,
        )

        val defaultCategory: Flow<NewsCategory> =
            context.dataStore.data.map { preferences ->
                val categoryName = preferences[DEFAULT_CATEGORY_KEY] ?: NewsCategory.GENERAL.name
                try {
                    NewsCategory.valueOf(categoryName)
                } catch (e: IllegalArgumentException) {
                    NewsCategory.GENERAL
                }
            }

        val articlesPerPage: Flow<Int> =
            context.dataStore.data.map { preferences ->
                preferences[ARTICLES_PER_PAGE_KEY] ?: 20
            }

        val autoRefreshMinutes: Flow<Int> =
            context.dataStore.data.map { preferences ->
                preferences[AUTO_REFRESH_MINUTES_KEY] ?: 30
            }

        val showImages: Flow<Boolean> =
            context.dataStore.data.map { preferences ->
                preferences[SHOW_IMAGES_KEY] ?: true
            }

        suspend fun setDefaultCategory(category: NewsCategory) {
            context.dataStore.edit { preferences ->
                preferences[DEFAULT_CATEGORY_KEY] = category.name
            }
        }

        suspend fun setArticlesPerPage(count: Int) {
            context.dataStore.edit { preferences ->
                preferences[ARTICLES_PER_PAGE_KEY] = count
            }
        }

        suspend fun setAutoRefreshMinutes(minutes: Int) {
            context.dataStore.edit { preferences ->
                preferences[AUTO_REFRESH_MINUTES_KEY] = minutes
            }
        }

        suspend fun setShowImages(show: Boolean) {
            context.dataStore.edit { preferences ->
                preferences[SHOW_IMAGES_KEY] = show
            }
        }

        companion object {
            private const val FEED_PREFERENCES_NAME = "feed_preferences"
            private val DEFAULT_CATEGORY_KEY = stringPreferencesKey("default_category")
            private val ARTICLES_PER_PAGE_KEY = intPreferencesKey("articles_per_page")
            private val AUTO_REFRESH_MINUTES_KEY = intPreferencesKey("auto_refresh_minutes")
            private val SHOW_IMAGES_KEY = booleanPreferencesKey("show_images")
        }
    }
