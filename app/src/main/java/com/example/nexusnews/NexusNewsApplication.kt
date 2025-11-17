package com.example.nexusnews

import android.app.Application
import dagger.hilt.android.HiltAndroidApp
import timber.log.Timber

/**
 * Application class for NexusNews.
 * Initializes Timber logging and Hilt dependency injection.
 */
@HiltAndroidApp
class NexusNewsApplication : Application() {
    override fun onCreate() {
        super.onCreate()

        // Initialize Timber for logging
        if (BuildConfig.DEBUG) {
            Timber.plant(Timber.DebugTree())
        }
    }
}
