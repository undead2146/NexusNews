package com.example.nexusnews.domain.usecase.ai

import com.example.nexusnews.domain.ai.AiService
import com.example.nexusnews.domain.usecase.BaseUseCase
import com.example.nexusnews.util.Result
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

/**
 * Use case for translating articles.
 */
class TranslateArticleUseCase
    @Inject
    constructor(
        private val aiService: AiService,
        dispatcher: kotlinx.coroutines.CoroutineDispatcher,
    ) : BaseUseCase<TranslateArticleUseCase.Params, String>(dispatcher) {
        data class Params(
            val articleContent: String,
            val targetLanguage: String,
        )

        override fun execute(params: Params): Flow<Result<String>> = flow {
            val kotlinResult = aiService.translateArticle(params.articleContent, params.targetLanguage)
            val result =
                if (kotlinResult.isSuccess) {
                    Result.Success(kotlinResult.getOrThrow())
                } else {
                    Result.Error(kotlinResult.exceptionOrNull() ?: Exception("Unknown error"))
                }
            emit(result)
        }
    }
