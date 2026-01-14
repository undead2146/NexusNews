package com.example.nexusnews.data.local.datastore

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

/**
 * DataStore for AI feature preferences and settings.
 */
private val Context.aiFeatureDataStore: DataStore<Preferences> by
    preferencesDataStore(name = "ai_feature_preferences")

@Singleton
class AiFeaturePreferencesDataStore
    @Inject
    constructor(
        @ApplicationContext private val context: Context,
    ) {
        private object PreferencesKeys {
            // Feature toggles
            val ENABLE_SUMMARIZATION = booleanPreferencesKey("enable_summarization")
            val ENABLE_SENTIMENT_ANALYSIS = booleanPreferencesKey("enable_sentiment_analysis")
            val ENABLE_KEY_POINTS = booleanPreferencesKey("enable_key_points")
            val ENABLE_ENTITY_RECOGNITION = booleanPreferencesKey("enable_entity_recognition")
            val ENABLE_TOPIC_CLASSIFICATION = booleanPreferencesKey("enable_topic_classification")
            val ENABLE_BIAS_DETECTION = booleanPreferencesKey("enable_bias_detection")
            val ENABLE_CHAT_ASSISTANT = booleanPreferencesKey("enable_chat_assistant")
            val ENABLE_RECOMMENDATIONS = booleanPreferencesKey("enable_recommendations")
            val ENABLE_CONTENT_GENERATION = booleanPreferencesKey("enable_content_generation")

            // Quality settings
            val AI_QUALITY_LEVEL = stringPreferencesKey("ai_quality_level") // "low", "medium", "high"
            val MAX_TOKENS_SUMMARY = stringPreferencesKey("max_tokens_summary")
            val MAX_TOKENS_ANALYSIS = stringPreferencesKey("max_tokens_analysis")

            // Behavior customization
            val AUTO_GENERATE_SUMMARY = booleanPreferencesKey("auto_generate_summary")
            val SHOW_AI_INDICATORS = booleanPreferencesKey("show_ai_indicators")
            val CACHE_AI_RESPONSES = booleanPreferencesKey("cache_ai_responses")
            val ENABLE_AI_NOTIFICATIONS = booleanPreferencesKey("enable_ai_notifications")
        }

        // ==================== Feature Toggles ====================

        suspend fun setFeatureEnabled(
            feature: AiFeature,
            enabled: Boolean,
        ) {
            context.aiFeatureDataStore.edit { preferences ->
                when (feature) {
                    AiFeature.SUMMARIZATION ->
                        preferences[PreferencesKeys.ENABLE_SUMMARIZATION] = enabled
                    AiFeature.SENTIMENT_ANALYSIS ->
                        preferences[PreferencesKeys.ENABLE_SENTIMENT_ANALYSIS] = enabled
                    AiFeature.KEY_POINTS ->
                        preferences[PreferencesKeys.ENABLE_KEY_POINTS] = enabled
                    AiFeature.ENTITY_RECOGNITION ->
                        preferences[PreferencesKeys.ENABLE_ENTITY_RECOGNITION] = enabled
                    AiFeature.TOPIC_CLASSIFICATION ->
                        preferences[PreferencesKeys.ENABLE_TOPIC_CLASSIFICATION] = enabled
                    AiFeature.BIAS_DETECTION ->
                        preferences[PreferencesKeys.ENABLE_BIAS_DETECTION] = enabled
                    AiFeature.CHAT_ASSISTANT ->
                        preferences[PreferencesKeys.ENABLE_CHAT_ASSISTANT] = enabled
                    AiFeature.RECOMMENDATIONS ->
                        preferences[PreferencesKeys.ENABLE_RECOMMENDATIONS] = enabled
                    AiFeature.CONTENT_GENERATION ->
                        preferences[PreferencesKeys.ENABLE_CONTENT_GENERATION] = enabled
                }
            }
        }

        fun isFeatureEnabled(feature: AiFeature): Flow<Boolean> =
            when (feature) {
                AiFeature.SUMMARIZATION ->
                    context.aiFeatureDataStore.data.map {
                        it[PreferencesKeys.ENABLE_SUMMARIZATION] ?: true
                    }
                AiFeature.SENTIMENT_ANALYSIS ->
                    context.aiFeatureDataStore.data.map {
                        it[PreferencesKeys.ENABLE_SENTIMENT_ANALYSIS] ?: true
                    }
                AiFeature.KEY_POINTS ->
                    context.aiFeatureDataStore.data.map {
                        it[PreferencesKeys.ENABLE_KEY_POINTS] ?: true
                    }
                AiFeature.ENTITY_RECOGNITION ->
                    context.aiFeatureDataStore.data.map {
                        it[PreferencesKeys.ENABLE_ENTITY_RECOGNITION] ?: true
                    }
                AiFeature.TOPIC_CLASSIFICATION ->
                    context.aiFeatureDataStore.data.map {
                        it[PreferencesKeys.ENABLE_TOPIC_CLASSIFICATION] ?: true
                    }
                AiFeature.BIAS_DETECTION ->
                    context.aiFeatureDataStore.data.map {
                        it[PreferencesKeys.ENABLE_BIAS_DETECTION] ?: true
                    }
                AiFeature.CHAT_ASSISTANT ->
                    context.aiFeatureDataStore.data.map {
                        it[PreferencesKeys.ENABLE_CHAT_ASSISTANT] ?: true
                    }
                AiFeature.RECOMMENDATIONS ->
                    context.aiFeatureDataStore.data.map {
                        it[PreferencesKeys.ENABLE_RECOMMENDATIONS] ?: true
                    }
                AiFeature.CONTENT_GENERATION ->
                    context.aiFeatureDataStore.data.map {
                        it[PreferencesKeys.ENABLE_CONTENT_GENERATION] ?: true
                    }
            }

        // ==================== Quality Settings ====================

        suspend fun setQualityLevel(level: AiQualityLevel) {
            context.aiFeatureDataStore.edit { preferences ->
                preferences[PreferencesKeys.AI_QUALITY_LEVEL] = level.name.lowercase()
            }
        }

        fun getQualityLevel(): Flow<AiQualityLevel> =
            context.aiFeatureDataStore.data.map { preferences ->
                val level = preferences[PreferencesKeys.AI_QUALITY_LEVEL] ?: "medium"
                try {
                    AiQualityLevel.valueOf(level.uppercase())
                } catch (e: Exception) {
                    AiQualityLevel.MEDIUM
                }
            }

        suspend fun setMaxTokensSummary(maxTokens: Int) {
            context.aiFeatureDataStore.edit { preferences ->
                preferences[PreferencesKeys.MAX_TOKENS_SUMMARY] = maxTokens.toString()
            }
        }

        fun getMaxTokensSummary(): Flow<Int> =
            context.aiFeatureDataStore.data.map { preferences ->
                preferences[PreferencesKeys.MAX_TOKENS_SUMMARY]?.toIntOrNull() ?: 150
            }

        suspend fun setMaxTokensAnalysis(maxTokens: Int) {
            context.aiFeatureDataStore.edit { preferences ->
                preferences[PreferencesKeys.MAX_TOKENS_ANALYSIS] = maxTokens.toString()
            }
        }

        fun getMaxTokensAnalysis(): Flow<Int> =
            context.aiFeatureDataStore.data.map { preferences ->
                preferences[PreferencesKeys.MAX_TOKENS_ANALYSIS]?.toIntOrNull() ?: 500
            }

        // ==================== Behavior Customization ====================

        suspend fun setAutoGenerateSummary(enabled: Boolean) {
            context.aiFeatureDataStore.edit { preferences ->
                preferences[PreferencesKeys.AUTO_GENERATE_SUMMARY] = enabled
            }
        }

        fun getAutoGenerateSummary(): Flow<Boolean> =
            context.aiFeatureDataStore.data.map {
                it[PreferencesKeys.AUTO_GENERATE_SUMMARY] ?: false
            }

        suspend fun setShowAiIndicators(enabled: Boolean) {
            context.aiFeatureDataStore.edit { preferences ->
                preferences[PreferencesKeys.SHOW_AI_INDICATORS] = enabled
            }
        }

        fun getShowAiIndicators(): Flow<Boolean> =
            context.aiFeatureDataStore.data.map {
                it[PreferencesKeys.SHOW_AI_INDICATORS] ?: true
            }

        suspend fun setCacheAiResponses(enabled: Boolean) {
            context.aiFeatureDataStore.edit { preferences ->
                preferences[PreferencesKeys.CACHE_AI_RESPONSES] = enabled
            }
        }

        fun getCacheAiResponses(): Flow<Boolean> =
            context.aiFeatureDataStore.data.map {
                it[PreferencesKeys.CACHE_AI_RESPONSES] ?: true
            }

        suspend fun setEnableAiNotifications(enabled: Boolean) {
            context.aiFeatureDataStore.edit { preferences ->
                preferences[PreferencesKeys.ENABLE_AI_NOTIFICATIONS] = enabled
            }
        }

        fun getEnableAiNotifications(): Flow<Boolean> =
            context.aiFeatureDataStore.data.map {
                it[PreferencesKeys.ENABLE_AI_NOTIFICATIONS] ?: false
            }

        // ==================== Batch Operations ====================

        suspend fun resetToDefaults() {
            context.aiFeatureDataStore.edit { preferences ->
                // Reset feature toggles
                preferences[PreferencesKeys.ENABLE_SUMMARIZATION] = true
                preferences[PreferencesKeys.ENABLE_SENTIMENT_ANALYSIS] = true
                preferences[PreferencesKeys.ENABLE_KEY_POINTS] = true
                preferences[PreferencesKeys.ENABLE_ENTITY_RECOGNITION] = true
                preferences[PreferencesKeys.ENABLE_TOPIC_CLASSIFICATION] = true
                preferences[PreferencesKeys.ENABLE_BIAS_DETECTION] = true
                preferences[PreferencesKeys.ENABLE_CHAT_ASSISTANT] = true
                preferences[PreferencesKeys.ENABLE_RECOMMENDATIONS] = true
                preferences[PreferencesKeys.ENABLE_CONTENT_GENERATION] = true

                // Reset quality settings
                preferences[PreferencesKeys.AI_QUALITY_LEVEL] = "medium"
                preferences[PreferencesKeys.MAX_TOKENS_SUMMARY] = "150"
                preferences[PreferencesKeys.MAX_TOKENS_ANALYSIS] = "500"

                // Reset behavior
                preferences[PreferencesKeys.AUTO_GENERATE_SUMMARY] = false
                preferences[PreferencesKeys.SHOW_AI_INDICATORS] = true
                preferences[PreferencesKeys.CACHE_AI_RESPONSES] = true
                preferences[PreferencesKeys.ENABLE_AI_NOTIFICATIONS] = false
            }
        }
    }

/**
 * AI features that can be toggled.
 */
enum class AiFeature {
    SUMMARIZATION,
    SENTIMENT_ANALYSIS,
    KEY_POINTS,
    ENTITY_RECOGNITION,
    TOPIC_CLASSIFICATION,
    BIAS_DETECTION,
    CHAT_ASSISTANT,
    RECOMMENDATIONS,
    CONTENT_GENERATION,
}

/**
 * AI quality levels.
 */
enum class AiQualityLevel {
    LOW, // Faster, less detailed
    MEDIUM, // Balanced
    HIGH, // Slower, more detailed
}
