package com.example.nexusnews.util.constants

/**
 * Network-related constants for API configuration, timeouts, and retry policies.
 */
object NetworkConstants {
    const val BASE_URL = "https://api.example.com/" // Placeholder - will be updated per API
    const val CONNECT_TIMEOUT_SECONDS = 30L
    const val READ_TIMEOUT_SECONDS = 30L
    const val WRITE_TIMEOUT_SECONDS = 30L
    const val MAX_RETRY_ATTEMPTS = 3
    const val INITIAL_RETRY_DELAY_MS = 1000L
    const val MAX_RETRY_DELAY_MS = 10000L
    const val RETRY_BACKOFF_MULTIPLIER = 2.0
    const val USER_AGENT = "NexusNews-Android/1.0"
}