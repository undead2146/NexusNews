package com.example.nexusnews.data.local.datastore

import android.content.Context
import android.content.SharedPreferences
import androidx.security.crypto.EncryptedSharedPreferences
import androidx.security.crypto.MasterKey
import dagger.hilt.android.qualifiers.ApplicationContext
import timber.log.Timber
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Secure storage for API keys using EncryptedSharedPreferences.
 * Uses AES256_GCM encryption for maximum security.
 */
@Singleton
class ApiKeyDataStore
    @Inject
    constructor(
        @ApplicationContext private val context: Context,
    ) {
        private val masterKey: MasterKey by lazy {
            MasterKey.Builder(context)
                .setKeyScheme(MasterKey.KeyScheme.AES256_GCM)
                .build()
        }

        private val encryptedPrefs: SharedPreferences by lazy {
            try {
                EncryptedSharedPreferences.create(
                    context,
                    PREFS_NAME,
                    masterKey,
                    EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
                    EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM,
                )
            } catch (e: Exception) {
                Timber.e(e, "Failed to create encrypted preferences")
                throw e
            }
        }

        /**
         * Gets the stored OpenRouter API key.
         * @return API key or null if not set
         */
        fun getOpenRouterApiKey(): String? =
            try {
                encryptedPrefs.getString(KEY_OPENROUTER_API_KEY, null)
            } catch (e: Exception) {
                Timber.e(e, "Failed to get API key")
                null
            }

        /**
         * Stores the OpenRouter API key securely.
         * @param key API key to store
         */
        fun setOpenRouterApiKey(key: String) {
            try {
                encryptedPrefs.edit()
                    .putString(KEY_OPENROUTER_API_KEY, key)
                    .apply()
                Timber.d("API key saved successfully")
            } catch (e: Exception) {
                Timber.e(e, "Failed to save API key")
                throw e
            }
        }

        /**
         * Clears the stored OpenRouter API key.
         */
        fun clearOpenRouterApiKey() {
            try {
                encryptedPrefs.edit()
                    .remove(KEY_OPENROUTER_API_KEY)
                    .apply()
                Timber.d("API key cleared")
            } catch (e: Exception) {
                Timber.e(e, "Failed to clear API key")
                throw e
            }
        }

        /**
         * Checks if an API key is currently stored.
         * @return true if key exists, false otherwise
         */
        fun hasApiKey(): Boolean = getOpenRouterApiKey() != null

        companion object {
            private const val PREFS_NAME = "api_keys_encrypted"
            private const val KEY_OPENROUTER_API_KEY = "openrouter_api_key"
        }
    }
