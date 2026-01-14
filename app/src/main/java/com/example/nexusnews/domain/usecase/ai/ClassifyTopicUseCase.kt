package com.example.nexusnews.domain.usecase.ai

import com.example.nexusnews.domain.ai.AiService
import com.example.nexusnews.domain.ai.TopicClassificationResult
import com.example.nexusnews.domain.usecase.BaseUseCase
import com.example.nexusnews.util.Result
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

/**
 * Use case for classifying article topics.
 */
class ClassifyTopicUseCase
    @Inject
    constructor(
        private val aiService: AiService,
        dispatcher: kotlinx.coroutines.CoroutineDispatcher,
    ) : BaseUseCase<ClassifyTopicUseCase.Params, TopicClassificationResult>(dispatcher) {
        data class Params(
            val articleContent: String,
            val articleTitle: String? = null,
        )

        override fun execute(params: Params): Flow<Result<TopicClassificationResult>> = flow {
            val kotlinResult = aiService.classifyTopic(params.articleContent, params.articleTitle)
            val result =
                if (kotlinResult.isSuccess) {
                    Result.Success(kotlinResult.getOrThrow())
                } else {
                    Result.Error(kotlinResult.exceptionOrNull() ?: Exception("Unknown error"))
                }
            emit(result)
        }
    }
