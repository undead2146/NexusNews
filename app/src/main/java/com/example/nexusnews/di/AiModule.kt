package com.example.nexusnews.di

import com.example.nexusnews.data.ai.OpenRouterAiService
import com.example.nexusnews.domain.ai.AiService
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

/**
 * Hilt module for AI-related dependencies.
 * Uses the clean architecture implementation.
 */
@Module
@InstallIn(SingletonComponent::class)
abstract class AiModule {
    @Binds
    @Singleton
    abstract fun bindAiService(impl: OpenRouterAiService): AiService
}
