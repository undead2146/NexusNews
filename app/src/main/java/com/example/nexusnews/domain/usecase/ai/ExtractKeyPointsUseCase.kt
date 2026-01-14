package com.example.nexusnews.domain.usecase.ai

import com.example.nexusnews.domain.ai.AiService
import com.example.nexusnews.domain.ai.KeyPointsResult
import com.example.nexusnews.domain.usecase.BaseUseCase
import com.example.nexusnews.util.Result
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

/**
 * Use case for extracting key points from articles.
 */
class ExtractKeyPointsUseCase
    @Inject
    constructor(
        private val aiService: AiService,
        dispatcher: kotlinx.coroutines.CoroutineDispatcher,
    ) : BaseUseCase<ExtractKeyPointsUseCase.Params, KeyPointsResult>(dispatcher) {
        data class Params(
            val articleContent: String,
            val maxPoints: Int = 5,
        )

        override fun execute(params: Params): Flow<Result<KeyPointsResult>> = flow {
            val kotlinResult = aiService.extractKeyPoints(params.articleContent, params.maxPoints)
            val result =
                if (kotlinResult.isSuccess) {
                    Result.Success(kotlinResult.getOrThrow())
                } else {
                    Result.Error(kotlinResult.exceptionOrNull() ?: Exception("Unknown error"))
                }
            emit(result)
        }
    }
