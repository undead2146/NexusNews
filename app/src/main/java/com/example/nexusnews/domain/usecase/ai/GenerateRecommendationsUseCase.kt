package com.example.nexusnews.domain.usecase.ai

import com.example.nexusnews.domain.ai.AiService
import com.example.nexusnews.domain.ai.RecommendationResult
import com.example.nexusnews.domain.ai.UserInterest
import com.example.nexusnews.domain.usecase.BaseUseCase
import com.example.nexusnews.util.Result
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

/**
 * Use case for generating article recommendations.
 */
class GenerateRecommendationsUseCase
    @Inject
    constructor(
        private val aiService: AiService,
        dispatcher: kotlinx.coroutines.CoroutineDispatcher,
    ) : BaseUseCase<GenerateRecommendationsUseCase.Params, RecommendationResult>(dispatcher) {
        data class Params(
            val userInterests: List<UserInterest>,
            val availableArticles: Map<String, String>,
            val limit: Int = 10,
        )

        override fun execute(params: Params): Flow<Result<RecommendationResult>> = flow {
            val kotlinResult =
                aiService.generateRecommendations(
                    params.userInterests,
                    params.availableArticles,
                    params.limit,
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
