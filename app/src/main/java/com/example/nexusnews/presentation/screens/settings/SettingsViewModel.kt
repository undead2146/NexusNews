package com.example.nexusnews.presentation.screens.settings

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
 * ViewModel for the Settings screen.
 * Manages all app settings and preferences.
 */
@HiltViewModel
class SettingsViewModel
    @Inject
    constructor(
        private val themePreferences: ThemePreferencesDataStore,
    ) : ViewModel() {
        /**
         * Current theme mode.
         */
        val themeMode: StateFlow<ThemeMode> =
            themePreferences.themeMode.stateIn(
                viewModelScope,
                SharingStarted.WhileSubscribed(5000),
                ThemeMode.SYSTEM,
            )

        /**
         * Sets the theme mode.
         */
        fun setThemeMode(mode: ThemeMode) {
            viewModelScope.launch {
                try {
                    themePreferences.setThemeMode(mode)
                    Timber.d("Theme mode changed to: $mode")
                } catch (e: Exception) {
                    Timber.e(e, "Failed to set theme mode")
                }
            }
        }
    }
