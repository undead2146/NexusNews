package com.example.nexusnews.data.local.datastore

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.stringSetPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.example.nexusnews.domain.model.NewsCategory
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import java.time.LocalTime
import javax.inject.Inject
import javax.inject.Singleton

/**
 * DataStore for managing notification preferences.
 * Persists user's notification settings across app restarts.
 */
@Singleton
class NotificationPreferencesDataStore
    @Inject
    constructor(
        @ApplicationContext private val context: Context,
    ) {
        private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(
            name = NOTIFICATION_PREFERENCES_NAME,
        )

        /**
         * Flow of breaking news notifications enabled state.
         */
        val breakingNewsEnabled: Flow<Boolean> =
            context.dataStore.data.map { preferences ->
                preferences[BREAKING_NEWS_ENABLED_KEY] ?: true
            }

        /**
         * Flow of daily digest enabled state.
         */
        val dailyDigestEnabled: Flow<Boolean> =
            context.dataStore.data.map { preferences ->
                preferences[DAILY_DIGEST_ENABLED_KEY] ?: false
            }

        /**
         * Flow of daily digest time (hour of day, 0-23).
         */
        val digestTimeHour: Flow<Int> =
            context.dataStore.data.map { preferences ->
                preferences[DIGEST_TIME_HOUR_KEY] ?: 8
            }

        /**
         * Flow of enabled notification categories.
         */
        val enabledCategories: Flow<Set<NewsCategory>> =
            context.dataStore.data.map { preferences ->
                val categoryNames = preferences[ENABLED_CATEGORIES_KEY] ?: emptySet()
                if (categoryNames.isEmpty()) {
                    NewsCategory.entries.toSet()
                } else {
                    categoryNames
                        .mapNotNull { name ->
                            try {
                                NewsCategory.valueOf(name)
                            } catch (e: IllegalArgumentException) {
                                null
                            }
                        }.toSet()
                }
            }

        /**
         * Flow of notification sound enabled state.
         */
        val soundEnabled: Flow<Boolean> =
            context.dataStore.data.map { preferences ->
                preferences[SOUND_ENABLED_KEY] ?: true
            }

        /**
         * Flow of notification vibration enabled state.
         */
        val vibrationEnabled: Flow<Boolean> =
            context.dataStore.data.map { preferences ->
                preferences[VIBRATION_ENABLED_KEY] ?: true
            }

        /**
         * Sets the breaking news notifications enabled state.
         */
        suspend fun setBreakingNewsEnabled(enabled: Boolean) {
            context.dataStore.edit { preferences ->
                preferences[BREAKING_NEWS_ENABLED_KEY] = enabled
            }
        }

        /**
         * Sets the daily digest enabled state.
         */
        suspend fun setDailyDigestEnabled(enabled: Boolean) {
            context.dataStore.edit { preferences ->
                preferences[DAILY_DIGEST_ENABLED_KEY] = enabled
            }
        }

        /**
         * Sets the daily digest time.
         */
        suspend fun setDigestTime(time: LocalTime) {
            context.dataStore.edit { preferences ->
                preferences[DIGEST_TIME_HOUR_KEY] = time.hour
            }
        }

        /**
         * Sets the enabled notification categories.
         */
        suspend fun setEnabledCategories(categories: Set<NewsCategory>) {
            context.dataStore.edit { preferences ->
                preferences[ENABLED_CATEGORIES_KEY] = categories.map { it.name }.toSet()
            }
        }

        /**
         * Sets the notification sound enabled state.
         */
        suspend fun setSoundEnabled(enabled: Boolean) {
            context.dataStore.edit { preferences ->
                preferences[SOUND_ENABLED_KEY] = enabled
            }
        }

        /**
         * Sets the notification vibration enabled state.
         */
        suspend fun setVibrationEnabled(enabled: Boolean) {
            context.dataStore.edit { preferences ->
                preferences[VIBRATION_ENABLED_KEY] = enabled
            }
        }

        companion object {
            private const val NOTIFICATION_PREFERENCES_NAME = "notification_preferences"
            private val BREAKING_NEWS_ENABLED_KEY = booleanPreferencesKey("breaking_news_enabled")
            private val DAILY_DIGEST_ENABLED_KEY = booleanPreferencesKey("daily_digest_enabled")
            private val DIGEST_TIME_HOUR_KEY = intPreferencesKey("digest_time_hour")
            private val ENABLED_CATEGORIES_KEY = stringSetPreferencesKey("enabled_categories")
            private val SOUND_ENABLED_KEY = booleanPreferencesKey("sound_enabled")
            private val VIBRATION_ENABLED_KEY = booleanPreferencesKey("vibration_enabled")
        }
    }
