package com.example.nexusnews.domain.usecase

import com.example.nexusnews.util.Result
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flowOn
import timber.log.Timber

/**
 * Base use case for executing business logic operations.
 * Provides consistent error handling and threading.
 * 
 * @param In Input parameter type
 * @param Out Output result type
 */
abstract class BaseUseCase<In, Out>(
    private val dispatcher: CoroutineDispatcher
) {
    /**
     * Executes the use case operation.
     * Automatically handles threading and error catching.
     */
    operator fun invoke(params: In): Flow<Result<Out>> {
        return execute(params)
            .catch { e ->
                Timber.e(e, "Error in use case: ${this::class.simpleName}")
                emit(Result.Error(e))
            }
            .flowOn(dispatcher)
    }
    
    /**
     * Implement business logic here.
     */
    protected abstract fun execute(params: In): Flow<Result<Out>>
}
