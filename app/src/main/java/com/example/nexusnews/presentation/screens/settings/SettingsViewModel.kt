package com.example.nexusnews.presentation.screens.settings

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.nexusnews.data.cache.CacheManager
import com.example.nexusnews.data.cache.CacheStatistics
import com.example.nexusnews.data.local.datastore.NotificationPreferencesDataStore
import com.example.nexusnews.data.local.datastore.ThemeMode
import com.example.nexusnews.data.local.datastore.ThemePreferencesDataStore
import com.example.nexusnews.domain.model.NewsCategory
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import timber.log.Timber
import java.time.LocalTime
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
        private val notificationPreferences: NotificationPreferencesDataStore,
        private val cacheManager: CacheManager,
    ) : ViewModel() {
        // Theme preferences
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

        // Notification preferences

        /**
         * Breaking news notifications enabled state.
         */
        val breakingNewsEnabled: StateFlow<Boolean> =
            notificationPreferences.breakingNewsEnabled.stateIn(
                viewModelScope,
                SharingStarted.WhileSubscribed(5000),
                true,
            )

        /**
         * Daily digest enabled state.
         */
        val dailyDigestEnabled: StateFlow<Boolean> =
            notificationPreferences.dailyDigestEnabled.stateIn(
                viewModelScope,
                SharingStarted.WhileSubscribed(5000),
                false,
            )

        /**
         * Daily digest time (hour of day).
         */
        val digestTimeHour: StateFlow<Int> =
            notificationPreferences.digestTimeHour.stateIn(
                viewModelScope,
                SharingStarted.WhileSubscribed(5000),
                8,
            )

        /**
         * Enabled notification categories.
         */
        val enabledCategories: StateFlow<Set<NewsCategory>> =
            notificationPreferences.enabledCategories.stateIn(
                viewModelScope,
                SharingStarted.WhileSubscribed(5000),
                NewsCategory.entries.toSet(),
            )

        /**
         * Notification sound enabled state.
         */
        val soundEnabled: StateFlow<Boolean> =
            notificationPreferences.soundEnabled.stateIn(
                viewModelScope,
                SharingStarted.WhileSubscribed(5000),
                true,
            )

        /**
         * Notification vibration enabled state.
         */
        val vibrationEnabled: StateFlow<Boolean> =
            notificationPreferences.vibrationEnabled.stateIn(
                viewModelScope,
                SharingStarted.WhileSubscribed(5000),
                true,
            )

        /**
         * Sets breaking news notifications enabled state.
         */
        fun setBreakingNewsEnabled(enabled: Boolean) {
            viewModelScope.launch {
                try {
                    notificationPreferences.setBreakingNewsEnabled(enabled)
                    Timber.d("Breaking news notifications: $enabled")
                } catch (e: Exception) {
                    Timber.e(e, "Failed to set breaking news notifications")
                }
            }
        }

        /**
         * Sets daily digest enabled state.
         */
        fun setDailyDigestEnabled(enabled: Boolean) {
            viewModelScope.launch {
                try {
                    notificationPreferences.setDailyDigestEnabled(enabled)
                    Timber.d("Daily digest: $enabled")
                } catch (e: Exception) {
                    Timber.e(e, "Failed to set daily digest")
                }
            }
        }

        /**
         * Sets daily digest time.
         */
        fun setDigestTime(time: LocalTime) {
            viewModelScope.launch {
                try {
                    notificationPreferences.setDigestTime(time)
                    Timber.d("Digest time: $time")
                } catch (e: Exception) {
                    Timber.e(e, "Failed to set digest time")
                }
            }
        }

        /**
         * Sets enabled notification categories.
         */
        fun setEnabledCategories(categories: Set<NewsCategory>) {
            viewModelScope.launch {
                try {
                    notificationPreferences.setEnabledCategories(categories)
                    Timber.d("Enabled categories: $categories")
                } catch (e: Exception) {
                    Timber.e(e, "Failed to set enabled categories")
                }
            }
        }

        /**
         * Sets notification sound enabled state.
         */
        fun setSoundEnabled(enabled: Boolean) {
            viewModelScope.launch {
                try {
                    notificationPreferences.setSoundEnabled(enabled)
                    Timber.d("Notification sound: $enabled")
                } catch (e: Exception) {
                    Timber.e(e, "Failed to set notification sound")
                }
            }
        }

        /**
         * Sets notification vibration enabled state.
         */
        fun setVibrationEnabled(enabled: Boolean) {
            viewModelScope.launch {
                try {
                    notificationPreferences.setVibrationEnabled(enabled)
                    Timber.d("Notification vibration: $enabled")
                } catch (e: Exception) {
                    Timber.e(e, "Failed to set notification vibration")
                }
            }
        }

        // Cache management
        private val _cacheStatistics = kotlinx.coroutines.flow.MutableStateFlow<CacheStatistics?>(null)
        val cacheStatistics: StateFlow<CacheStatistics?> = _cacheStatistics

        /**
         * Refreshes cache statistics.
         */
        fun refreshCacheStatistics() {
            viewModelScope.launch {
                try {
                    val stats = cacheManager.getCacheStatistics()
                    _cacheStatistics.value = stats
                    Timber.d("Cache statistics refreshed: $stats")
                } catch (e: Exception) {
                    Timber.e(e, "Failed to refresh cache statistics")
                }
            }
        }

        /**
         * Clears article cache.
         */
        fun clearArticleCache() {
            viewModelScope.launch {
                try {
                    cacheManager.clearArticleCache()
                    refreshCacheStatistics()
                    Timber.d("Article cache cleared")
                } catch (e: Exception) {
                    Timber.e(e, "Failed to clear article cache")
                }
            }
        }

        /**
         * Clears image cache.
         */
        fun clearImageCache() {
            viewModelScope.launch {
                try {
                    cacheManager.clearImageCache()
                    refreshCacheStatistics()
                    Timber.d("Image cache cleared")
                } catch (e: Exception) {
                    Timber.e(e, "Failed to clear image cache")
                }
            }
        }

        /**
         * Clears all cache.
         */
        fun clearAllCache() {
            viewModelScope.launch {
                try {
                    cacheManager.clearAllCache()
                    refreshCacheStatistics()
                    Timber.d("All cache cleared")
                } catch (e: Exception) {
                    Timber.e(e, "Failed to clear all cache")
                }
            }
        }
    }
