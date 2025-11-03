package com.example.nexusnews.presentation.common

/**
 * Sealed interface representing UI state.
 * Separates presentation state from business logic state.
 */
sealed interface UiState<out T> {
    data object Idle : UiState<Nothing>

    data object Loading : UiState<Nothing>

    data class Success<T>(
        val data: T,
    ) : UiState<T>

    data class Error(
        val message: String,
        val throwable: Throwable? = null,
    ) : UiState<Nothing>

    /**
     * Helper to check if state is loading.
     */
    fun isLoading(): Boolean = this is Loading

    /**
     * Helper to get data or null.
     */
    fun getDataOrNull(): T? =
        when (this) {
            is Success -> data
            else -> null
        }
}

/**
 * Extension function to convert Result to UiState.
 */
fun <T> com.example.nexusnews.util.Result<T>.toUiState(): UiState<T> =
    when (this) {
        is com.example.nexusnews.util.Result.Loading -> UiState.Loading
        is com.example.nexusnews.util.Result.Success -> UiState.Success(data)
        is com.example.nexusnews.util.Result.Error ->
            UiState.Error(
                message = exception.message ?: "Unknown error",
                throwable = exception,
            )
    }
