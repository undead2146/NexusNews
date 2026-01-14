package com.example.nexusnews.domain.usecase.ai

import com.example.nexusnews.domain.ai.AiService
import com.example.nexusnews.domain.ai.ContentGenerationResult
import com.example.nexusnews.domain.ai.ContentType
import com.example.nexusnews.domain.usecase.BaseUseCase
import com.example.nexusnews.util.Result
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

/**
 * Use case for generating content based on articles.
 */
class GenerateContentUseCase
    @Inject
    constructor(
        private val aiService: AiService,
        dispatcher: kotlinx.coroutines.CoroutineDispatcher,
    ) : BaseUseCase<GenerateContentUseCase.Params, ContentGenerationResult>(dispatcher) {
        data class Params(
            val articleContent: String,
            val contentType: ContentType,
            val customPrompt: String? = null,
        )

        override fun execute(params: Params): Flow<Result<ContentGenerationResult>> = flow {
            val kotlinResult =
                aiService.generateContent(
                    params.articleContent,
                    params.contentType,
                    params.customPrompt,
                )
            val result =
                if (kotlinResult.isSuccess) {
                    Result.Success(kotlinResult.getOrThrow())
                } else {
                    Result.Error(kotlinResult.exceptionOrNull() ?: Exception("Unknown error"))
                }
            emit(result)
        }
    }
