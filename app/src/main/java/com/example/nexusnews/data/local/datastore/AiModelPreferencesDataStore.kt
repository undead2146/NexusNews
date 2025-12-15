package com.example.nexusnews.data.local.datastore

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.doublePreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.example.nexusnews.domain.ai.FreeAiModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

/**
 * DataStore for AI model preferences.
 */
@Singleton
class AiModelPreferencesDataStore
    @Inject
    constructor(
        @ApplicationContext private val context: Context,
    ) {
        private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(
            name = AI_MODEL_PREFERENCES_NAME,
        )

        val defaultModel: Flow<String> =
            context.dataStore.data.map { preferences ->
                preferences[DEFAULT_MODEL_KEY] ?: FreeAiModel.getDefault().id
            }

        val fallbackModel: Flow<String> =
            context.dataStore.data.map { preferences ->
                preferences[FALLBACK_MODEL_KEY] ?: FreeAiModel.GEMMA_2_9B.id
            }

        val maxTokensPerRequest: Flow<Int> =
            context.dataStore.data.map { preferences ->
                preferences[MAX_TOKENS_KEY] ?: 500
            }

        val temperature: Flow<Double> =
            context.dataStore.data.map { preferences ->
                preferences[TEMPERATURE_KEY] ?: 0.3
            }

        suspend fun setDefaultModel(modelId: String) {
            context.dataStore.edit { preferences ->
                preferences[DEFAULT_MODEL_KEY] = modelId
            }
        }

        suspend fun setFallbackModel(modelId: String) {
            context.dataStore.edit { preferences ->
                preferences[FALLBACK_MODEL_KEY] = modelId
            }
        }

        suspend fun setMaxTokens(tokens: Int) {
            context.dataStore.edit { preferences ->
                preferences[MAX_TOKENS_KEY] = tokens
            }
        }

        suspend fun setTemperature(temp: Double) {
            context.dataStore.edit { preferences ->
                preferences[TEMPERATURE_KEY] = temp
            }
        }

        companion object {
            private const val AI_MODEL_PREFERENCES_NAME = "ai_model_preferences"
            private val DEFAULT_MODEL_KEY = stringPreferencesKey("default_model")
            private val FALLBACK_MODEL_KEY = stringPreferencesKey("fallback_model")
            private val MAX_TOKENS_KEY = intPreferencesKey("max_tokens")
            private val TEMPERATURE_KEY = doublePreferencesKey("temperature")
        }
    }
