package com.example.nexusnews.presentation.screens.settings

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.nexusnews.data.local.datastore.ThemeMode

/**
 * Settings screen for app configuration.
 *
 * @param viewModel ViewModel for managing settings
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(
    modifier: Modifier = Modifier,
    viewModel: SettingsViewModel = hiltViewModel(),
) {
    val themeMode by viewModel.themeMode.collectAsStateWithLifecycle()

    Scaffold(
        modifier = modifier,
        topBar = {
            TopAppBar(
                title = { Text("Settings") },
            )
        },
    ) { paddingValues ->
        LazyColumn(
            modifier =
                Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),
        ) {
            // Appearance Section
            item {
                SettingsSection(title = "Appearance") {
                    ThemeSelector(
                        currentTheme = themeMode,
                        onThemeSelected = { viewModel.setThemeMode(it) },
                    )
                }
            }

            // Notifications Section
            item {
                val breakingNewsEnabled by viewModel.breakingNewsEnabled.collectAsStateWithLifecycle()
                val dailyDigestEnabled by viewModel.dailyDigestEnabled.collectAsStateWithLifecycle()
                val digestTimeHour by viewModel.digestTimeHour.collectAsStateWithLifecycle()
                val soundEnabled by viewModel.soundEnabled.collectAsStateWithLifecycle()
                val vibrationEnabled by viewModel.vibrationEnabled.collectAsStateWithLifecycle()

                SettingsSection(title = "Notifications") {
                    NotificationSettings(
                        breakingNewsEnabled = breakingNewsEnabled,
                        onBreakingNewsToggle = { viewModel.setBreakingNewsEnabled(it) },
                        dailyDigestEnabled = dailyDigestEnabled,
                        onDailyDigestToggle = { viewModel.setDailyDigestEnabled(it) },
                        digestTimeHour = digestTimeHour,
                        onDigestTimeChange = { viewModel.setDigestTime(java.time.LocalTime.of(it, 0)) },
                        soundEnabled = soundEnabled,
                        onSoundToggle = { viewModel.setSoundEnabled(it) },
                        vibrationEnabled = vibrationEnabled,
                        onVibrationToggle = { viewModel.setVibrationEnabled(it) },
                    )
                }
            }

            // Storage Section
            item {
                val cacheStats by viewModel.cacheStatistics.collectAsStateWithLifecycle()

                androidx.compose.runtime.LaunchedEffect(Unit) {
                    viewModel.refreshCacheStatistics()
                }

                SettingsSection(title = "Storage") {
                    StorageSettings(
                        cacheStatistics = cacheStats,
                        onClearArticleCache = { viewModel.clearArticleCache() },
                        onClearImageCache = { viewModel.clearImageCache() },
                        onClearAllCache = { viewModel.clearAllCache() },
                        onRefresh = { viewModel.refreshCacheStatistics() },
                    )
                }
            }

            // About Section
            item {
                SettingsSection(title = "About") {
                    AboutItem(label = "Version", value = "1.0.0")
                    Spacer(modifier = Modifier.height(8.dp))
                    AboutItem(label = "Build", value = "Debug")
                }
            }
        }
    }
}

/**
 * Settings section with title and content.
 */
@Composable
private fun SettingsSection(
    title: String,
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit,
) {
    Column(modifier = modifier) {
        Text(
            text = title,
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.primary,
            modifier = Modifier.padding(bottom = 8.dp),
        )
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors =
                CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surface,
                ),
            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        ) {
            Column(
                modifier = Modifier.padding(16.dp),
            ) {
                content()
            }
        }
    }
}

/**
 * Theme selector with chips for Light, Dark, and System modes.
 */
@Composable
private fun ThemeSelector(
    currentTheme: ThemeMode,
    onThemeSelected: (ThemeMode) -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(modifier = modifier) {
        Text(
            text = "Theme",
            style = MaterialTheme.typography.bodyLarge,
            fontWeight = FontWeight.Medium,
            modifier = Modifier.padding(bottom = 8.dp),
        )
        Row(
            horizontalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            ThemeMode.entries.forEach { mode ->
                FilterChip(
                    selected = currentTheme == mode,
                    onClick = { onThemeSelected(mode) },
                    label = {
                        Text(
                            text =
                                when (mode) {
                                    ThemeMode.LIGHT -> "Light"
                                    ThemeMode.DARK -> "Dark"
                                    ThemeMode.SYSTEM -> "System"
                                },
                        )
                    },
                    leadingIcon =
                        if (currentTheme == mode) {
                            {
                                Icon(
                                    imageVector = Icons.Filled.Check,
                                    contentDescription = null,
                                )
                            }
                        } else {
                            null
                        },
                )
            }
        }
    }
}

/**
 * Notification settings with toggles and time picker.
 */
@Composable
private fun NotificationSettings(
    breakingNewsEnabled: Boolean,
    onBreakingNewsToggle: (Boolean) -> Unit,
    dailyDigestEnabled: Boolean,
    onDailyDigestToggle: (Boolean) -> Unit,
    digestTimeHour: Int,
    onDigestTimeChange: (Int) -> Unit,
    soundEnabled: Boolean,
    onSoundToggle: (Boolean) -> Unit,
    vibrationEnabled: Boolean,
    onVibrationToggle: (Boolean) -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        // Breaking News Toggle
        SettingToggleItem(
            label = "Breaking News",
            description = "Get notified about breaking news",
            checked = breakingNewsEnabled,
            onCheckedChange = onBreakingNewsToggle,
        )

        // Daily Digest Toggle
        SettingToggleItem(
            label = "Daily Digest",
            description = "Receive a daily summary at ${digestTimeHour}:00",
            checked = dailyDigestEnabled,
            onCheckedChange = onDailyDigestToggle,
        )

        // Sound Toggle
        SettingToggleItem(
            label = "Sound",
            description = "Play sound for notifications",
            checked = soundEnabled,
            onCheckedChange = onSoundToggle,
        )

        // Vibration Toggle
        SettingToggleItem(
            label = "Vibration",
            description = "Vibrate for notifications",
            checked = vibrationEnabled,
            onCheckedChange = onVibrationToggle,
        )
    }
}

/**
 * Setting toggle item with label, description, and switch.
 */
@Composable
private fun SettingToggleItem(
    label: String,
    description: String,
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = label,
                style = MaterialTheme.typography.bodyLarge,
                fontWeight = FontWeight.Medium,
            )
            Text(
                text = description,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
        }
        androidx.compose.material3.Switch(
            checked = checked,
            onCheckedChange = onCheckedChange,
        )
    }
}

/**
 * Storage settings with cache statistics and clear buttons.
 */
@Composable
private fun StorageSettings(
    cacheStatistics: com.example.nexusnews.data.cache.CacheStatistics?,
    onClearArticleCache: () -> Unit,
    onClearImageCache: () -> Unit,
    onClearAllCache: () -> Unit,
    onRefresh: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        if (cacheStatistics != null) {
            // Total cache size
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
            ) {
                Text(
                    text = "Total Cache",
                    style = MaterialTheme.typography.bodyLarge,
                    fontWeight = FontWeight.Medium,
                )
                Text(
                    text = String.format("%.2f MB", cacheStatistics.totalSizeMB),
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.primary,
                )
            }

            // Article count
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
            ) {
                Text(
                    text = "Cached Articles",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
                Text(
                    text = "${cacheStatistics.articleCount} articles",
                    style = MaterialTheme.typography.bodyMedium,
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Clear buttons
            androidx.compose.material3.Button(
                onClick = onClearAllCache,
                modifier = Modifier.fillMaxWidth(),
            ) {
                Text("Clear All Cache")
            }
        } else {
            Text(
                text = "Loading cache statistics...",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
        }
    }
}

/**
 * About item displaying label and value.
 */
@Composable
private fun AboutItem(
    label: String,
    value: String,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
        )
        Text(
            text = value,
            style = MaterialTheme.typography.bodyMedium,
            fontWeight = FontWeight.Medium,
        )
    }
}
