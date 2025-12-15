package com.example.nexusnews.data.local.datastore

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.floatPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

/**
 * DataStore for managing accessibility preferences.
 * Persists user's accessibility settings across app restarts.
 */
@Singleton
class AccessibilityPreferencesDataStore
    @Inject
    constructor(
        @ApplicationContext private val context: Context,
    ) {
        private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(
            name = ACCESSIBILITY_PREFERENCES_NAME,
        )

        /**
         * Flow of the reduce animations preference.
         * When true, animations should be minimized or disabled.
         */
        val reduceAnimations: Flow<Boolean> =
            context.dataStore.data.map { preferences ->
                preferences[REDUCE_ANIMATIONS_KEY] ?: false
            }

        /**
         * Flow of the high contrast mode preference.
         * When true, UI should use high contrast colors.
         */
        val highContrastMode: Flow<Boolean> =
            context.dataStore.data.map { preferences ->
                preferences[HIGH_CONTRAST_MODE_KEY] ?: false
            }

        /**
         * Flow of the font size multiplier.
         * Default is 1.0, range is 0.8 to 2.0.
         */
        val fontSizeMultiplier: Flow<Float> =
            context.dataStore.data.map { preferences ->
                preferences[FONT_SIZE_MULTIPLIER_KEY] ?: 1.0f
            }

        /**
         * Sets the reduce animations preference.
         */
        suspend fun setReduceAnimations(reduce: Boolean) {
            context.dataStore.edit { preferences ->
                preferences[REDUCE_ANIMATIONS_KEY] = reduce
            }
        }

        /**
         * Sets the high contrast mode preference.
         */
        suspend fun setHighContrastMode(enabled: Boolean) {
            context.dataStore.edit { preferences ->
                preferences[HIGH_CONTRAST_MODE_KEY] = enabled
            }
        }

        /**
         * Sets the font size multiplier.
         * Value is clamped between 0.8 and 2.0.
         */
        suspend fun setFontSizeMultiplier(multiplier: Float) {
            val clampedMultiplier = multiplier.coerceIn(0.8f, 2.0f)
            context.dataStore.edit { preferences ->
                preferences[FONT_SIZE_MULTIPLIER_KEY] = clampedMultiplier
            }
        }

        companion object {
            private const val ACCESSIBILITY_PREFERENCES_NAME = "accessibility_preferences"
            private val REDUCE_ANIMATIONS_KEY = booleanPreferencesKey("reduce_animations")
            private val HIGH_CONTRAST_MODE_KEY = booleanPreferencesKey("high_contrast_mode")
            private val FONT_SIZE_MULTIPLIER_KEY = floatPreferencesKey("font_size_multiplier")
        }
    }
