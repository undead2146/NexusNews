package com.example.nexusnews.domain.usecase.ai

import com.example.nexusnews.domain.ai.AiService
import com.example.nexusnews.domain.ai.EntityRecognitionResult
import com.example.nexusnews.domain.usecase.BaseUseCase
import com.example.nexusnews.util.Result
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

/**
 * Use case for recognizing entities in articles.
 */
class RecognizeEntitiesUseCase
    @Inject
    constructor(
        private val aiService: AiService,
        dispatcher: kotlinx.coroutines.CoroutineDispatcher,
    ) : BaseUseCase<RecognizeEntitiesUseCase.Params, EntityRecognitionResult>(dispatcher) {
        data class Params(
            val articleContent: String,
        )

        override fun execute(params: Params): Flow<Result<EntityRecognitionResult>> = flow {
            val kotlinResult = aiService.recognizeEntities(params.articleContent)
            val result =
                if (kotlinResult.isSuccess) {
                    Result.Success(kotlinResult.getOrThrow())
                } else {
                    Result.Error(kotlinResult.exceptionOrNull() ?: Exception("Unknown error"))
                }
            emit(result)
        }
    }
