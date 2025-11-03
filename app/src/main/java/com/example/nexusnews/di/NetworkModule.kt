package com.example.nexusnews.di

import com.example.nexusnews.BuildConfig
import com.example.nexusnews.data.remote.interceptor.AuthInterceptor
import com.example.nexusnews.data.remote.interceptor.ErrorInterceptor
import com.example.nexusnews.data.remote.interceptor.RetryInterceptor
import com.example.nexusnews.data.util.RetryPolicy
import com.example.nexusnews.util.constants.NetworkConstants
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import timber.log.Timber
import java.util.concurrent.TimeUnit
import javax.inject.Singleton

/**
 * Hilt module providing network-related dependencies.
 * Configures Retrofit, OkHttp, Moshi, and network monitoring components.
 */
@Module
@InstallIn(SingletonComponent::class)
object NetworkModule {

    /**
     * Provides Moshi instance for JSON parsing with Kotlin support.
     */
    @Provides
    @Singleton
    fun provideMoshi(): Moshi {
        return Moshi.Builder()
            .add(KotlinJsonAdapterFactory())
            .build()
    }

    /**
     * Provides retry policy configuration for network requests.
     */
    @Provides
    @Singleton
    fun provideRetryPolicy(): RetryPolicy {
        return RetryPolicy(
            maxAttempts = NetworkConstants.MAX_RETRY_ATTEMPTS,
            initialDelayMs = NetworkConstants.INITIAL_RETRY_DELAY_MS,
            maxDelayMs = NetworkConstants.MAX_RETRY_DELAY_MS,
            backoffMultiplier = NetworkConstants.RETRY_BACKOFF_MULTIPLIER
        )
    }

    /**
     * Provides HTTP logging interceptor for debugging.
     * Only logs in debug builds to avoid leaking sensitive information.
     */
    @Provides
    @Singleton
    fun provideLoggingInterceptor(): HttpLoggingInterceptor {
        return HttpLoggingInterceptor { message ->
            Timber.tag("OkHttp").d(message)
        }.apply {
            level = if (BuildConfig.DEBUG) {
                HttpLoggingInterceptor.Level.BODY
            } else {
                HttpLoggingInterceptor.Level.NONE
            }
        }
    }

    /**
     * Provides configured OkHttpClient with all necessary interceptors.
     * Interceptor order: Retry → Auth → Error → Logging
     */
    @Provides
    @Singleton
    fun provideOkHttpClient(
        loggingInterceptor: HttpLoggingInterceptor,
        authInterceptor: AuthInterceptor,
        errorInterceptor: ErrorInterceptor,
        retryInterceptor: RetryInterceptor
    ): OkHttpClient {
        return OkHttpClient.Builder()
            .connectTimeout(NetworkConstants.CONNECT_TIMEOUT_SECONDS, TimeUnit.SECONDS)
            .readTimeout(NetworkConstants.READ_TIMEOUT_SECONDS, TimeUnit.SECONDS)
            .writeTimeout(NetworkConstants.WRITE_TIMEOUT_SECONDS, TimeUnit.SECONDS)
            // Order matters: retry first, then auth, then error handling, finally logging
            .addInterceptor(retryInterceptor)
            .addInterceptor(authInterceptor)
            .addInterceptor(errorInterceptor)
            .addInterceptor(loggingInterceptor)
            .build()
    }

    /**
     * Provides base Retrofit instance for API communication.
     * Specific API clients can extend this configuration.
     */
    @Provides
    @Singleton
    fun provideRetrofit(
        okHttpClient: OkHttpClient,
        moshi: Moshi
    ): Retrofit {
        return Retrofit.Builder()
            .baseUrl(NetworkConstants.BASE_URL)
            .client(okHttpClient)
            .addConverterFactory(MoshiConverterFactory.create(moshi))
            .build()
    }
}