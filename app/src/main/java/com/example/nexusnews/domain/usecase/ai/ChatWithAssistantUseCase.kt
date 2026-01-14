package com.example.nexusnews.domain.usecase.ai

import com.example.nexusnews.domain.ai.AiService
import com.example.nexusnews.domain.ai.ChatMessage
import com.example.nexusnews.domain.ai.ChatResponse
import com.example.nexusnews.domain.usecase.BaseUseCase
import com.example.nexusnews.util.Result
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

/**
 * Use case for chatting with AI assistant.
 */
class ChatWithAssistantUseCase
    @Inject
    constructor(
        private val aiService: AiService,
        dispatcher: kotlinx.coroutines.CoroutineDispatcher,
    ) : BaseUseCase<ChatWithAssistantUseCase.Params, ChatResponse>(dispatcher) {
        data class Params(
            val conversationHistory: List<ChatMessage>,
            val userMessage: String,
            val articleContext: String? = null,
        )

        override fun execute(params: Params): Flow<Result<ChatResponse>> = flow {
            val kotlinResult =
                aiService.chatWithAssistant(
                    params.conversationHistory,
                    params.userMessage,
                    params.articleContext,
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
