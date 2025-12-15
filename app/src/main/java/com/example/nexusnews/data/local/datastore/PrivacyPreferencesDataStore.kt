package com.example.nexusnews.data.local.datastore

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.preferencesDataStore
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

/**
 * DataStore for managing privacy preferences.
 */
@Singleton
class PrivacyPreferencesDataStore
    @Inject
    constructor(
        @ApplicationContext private val context: Context,
    ) {
        private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(
            name = PRIVACY_PREFERENCES_NAME,
        )

        val analyticsEnabled: Flow<Boolean> =
            context.dataStore.data.map { preferences ->
                preferences[ANALYTICS_ENABLED_KEY] ?: true
            }

        val crashReportingEnabled: Flow<Boolean> =
            context.dataStore.data.map { preferences ->
                preferences[CRASH_REPORTING_ENABLED_KEY] ?: true
            }

        val personalizationEnabled: Flow<Boolean> =
            context.dataStore.data.map { preferences ->
                preferences[PERSONALIZATION_ENABLED_KEY] ?: true
            }

        suspend fun setAnalyticsEnabled(enabled: Boolean) {
            context.dataStore.edit { preferences ->
                preferences[ANALYTICS_ENABLED_KEY] = enabled
            }
        }

        suspend fun setCrashReportingEnabled(enabled: Boolean) {
            context.dataStore.edit { preferences ->
                preferences[CRASH_REPORTING_ENABLED_KEY] = enabled
            }
        }

        suspend fun setPersonalizationEnabled(enabled: Boolean) {
            context.dataStore.edit { preferences ->
                preferences[PERSONALIZATION_ENABLED_KEY] = enabled
            }
        }

        companion object {
            private const val PRIVACY_PREFERENCES_NAME = "privacy_preferences"
            private val ANALYTICS_ENABLED_KEY = booleanPreferencesKey("analytics_enabled")
            private val CRASH_REPORTING_ENABLED_KEY = booleanPreferencesKey("crash_reporting_enabled")
            private val PERSONALIZATION_ENABLED_KEY = booleanPreferencesKey("personalization_enabled")
        }
    }
