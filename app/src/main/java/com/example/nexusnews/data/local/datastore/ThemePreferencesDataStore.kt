package com.example.nexusnews.data.local.datastore

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Theme modes available in the app.
 */
enum class ThemeMode {
    LIGHT,
    DARK,
    SYSTEM,
}

/**
 * DataStore for managing theme preferences.
 * Persists user's theme selection across app restarts.
 */
@Singleton
class ThemePreferencesDataStore
    @Inject
    constructor(
        @ApplicationContext private val context: Context,
    ) {
        private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(
            name = THEME_PREFERENCES_NAME,
        )

        /**
         * Flow of the current theme mode.
         * Defaults to SYSTEM if not set.
         */
        val themeMode: Flow<ThemeMode> =
            context.dataStore.data.map { preferences ->
                val themeName = preferences[THEME_MODE_KEY] ?: ThemeMode.SYSTEM.name
                try {
                    ThemeMode.valueOf(themeName)
                } catch (e: IllegalArgumentException) {
                    ThemeMode.SYSTEM
                }
            }

        /**
         * Sets the theme mode.
         *
         * @param mode The theme mode to set
         */
        suspend fun setThemeMode(mode: ThemeMode) {
            context.dataStore.edit { preferences ->
                preferences[THEME_MODE_KEY] = mode.name
            }
        }

        companion object {
            private const val THEME_PREFERENCES_NAME = "theme_preferences"
            private val THEME_MODE_KEY = stringPreferencesKey("theme_mode")
        }
    }
