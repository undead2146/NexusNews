package com.example.nexusnews.presentation.theme

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.nexusnews.data.local.datastore.ThemeMode
import com.example.nexusnews.data.local.datastore.ThemePreferencesDataStore
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject

/**
 * ViewModel for managing app-wide theme state.
 * Exposes current theme mode and provides function to update it.
 */
@HiltViewModel
class ThemeViewModel
    @Inject
    constructor(
        private val themePreferences: ThemePreferencesDataStore,
    ) : ViewModel() {
        /**
         * Current theme mode as StateFlow.
         * Defaults to SYSTEM mode.
         */
        val themeMode: StateFlow<ThemeMode> =
            themePreferences.themeMode.stateIn(
                viewModelScope,
                SharingStarted.WhileSubscribed(5000),
                ThemeMode.SYSTEM,
            )

        /**
         * Sets the theme mode.
         *
         * @param mode The theme mode to set
         */
        fun setThemeMode(mode: ThemeMode) {
            viewModelScope.launch {
                try {
                    themePreferences.setThemeMode(mode)
                    Timber.d("Theme mode set to: $mode")
                } catch (e: Exception) {
                    Timber.e(e, "Failed to set theme mode")
                }
            }
        }
    }
