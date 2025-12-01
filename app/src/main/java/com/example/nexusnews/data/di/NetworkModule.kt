package com.example.nexusnews.data.di

import com.example.nexusnews.BuildConfig
import com.example.nexusnews.data.remote.NewsRemoteDataSource
import com.example.nexusnews.data.remote.RemoteDataSource
import com.example.nexusnews.data.remote.api.NewsApiService
import com.example.nexusnews.data.remote.interceptor.AuthInterceptor
import com.example.nexusnews.data.remote.interceptor.ErrorInterceptor
import com.example.nexusnews.data.remote.interceptor.RetryInterceptor
import com.example.nexusnews.data.util.NetworkMonitor
import com.example.nexusnews.data.util.RetryPolicy
import com.squareup.moshi.Moshi
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import java.util.concurrent.TimeUnit
import javax.inject.Singleton

/**
 * Hilt module providing network-related dependencies.
 */
@Module
@InstallIn(SingletonComponent::class)
object NetworkModule {
    private const val NEWS_API_BASE_URL = "https://newsapi.org/"
    private const val TIMEOUT_SECONDS = 30L

    @Provides
    @Singleton
    fun provideMoshi(): Moshi = Moshi.Builder().build()

    @Provides
    @Singleton
    fun provideHttpLoggingInterceptor(): HttpLoggingInterceptor =
        HttpLoggingInterceptor().apply {
            level = HttpLoggingInterceptor.Level.BODY
        }

    @Provides
    @Singleton
    fun provideAuthInterceptor(): AuthInterceptor =
        AuthInterceptor().apply {
            setApiKey(BuildConfig.NEWS_API_KEY)
        }

    @Provides
    @Singleton
    fun provideErrorInterceptor(moshi: Moshi): ErrorInterceptor = ErrorInterceptor(moshi)

    @Provides
    @Singleton
    fun provideRetryPolicy(): RetryPolicy = RetryPolicy()

    @Provides
    @Singleton
    fun provideRetryInterceptor(retryPolicy: RetryPolicy): RetryInterceptor = RetryInterceptor(retryPolicy)

    @Provides
    @Singleton
    fun provideOkHttpClient(
        loggingInterceptor: HttpLoggingInterceptor,
        authInterceptor: AuthInterceptor,
        errorInterceptor: ErrorInterceptor,
        retryInterceptor: RetryInterceptor,
    ): OkHttpClient =
        OkHttpClient
            .Builder()
            .addInterceptor(authInterceptor)
            .addInterceptor(errorInterceptor)
            .addInterceptor(retryInterceptor)
            .addInterceptor(loggingInterceptor)
            .connectTimeout(TIMEOUT_SECONDS, TimeUnit.SECONDS)
            .readTimeout(TIMEOUT_SECONDS, TimeUnit.SECONDS)
            .writeTimeout(TIMEOUT_SECONDS, TimeUnit.SECONDS)
            .build()

    @Provides
    @Singleton
    fun provideRetrofit(
        okHttpClient: OkHttpClient,
        moshi: Moshi,
    ): Retrofit =
        Retrofit
            .Builder()
            .baseUrl(NEWS_API_BASE_URL)
            .client(okHttpClient)
            .addConverterFactory(MoshiConverterFactory.create(moshi))
            .build()

    @Provides
    @Singleton
    fun provideNewsApiService(retrofit: Retrofit): NewsApiService = retrofit.create(NewsApiService::class.java)

    @Provides
    @Singleton
    fun provideNewsRemoteDataSource(
        newsApiService: NewsApiService,
        networkMonitor: NetworkMonitor,
    ): NewsRemoteDataSource = NewsRemoteDataSource(newsApiService, networkMonitor)

    @Provides
    @Singleton
    fun provideRemoteDataSource(newsRemoteDataSource: NewsRemoteDataSource): RemoteDataSource = newsRemoteDataSource
}
