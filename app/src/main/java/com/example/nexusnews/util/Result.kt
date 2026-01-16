package com.example.nexusnews.util

/**
 * Sealed class representing the result of an operation.
 * Provides type-safe handling of loading, success, and error states.
 */
sealed class Result<out T> {
    data class Success<T>(
        val data: T,
    ) : Result<T>()

    data class Error(
        val exception: Throwable,
    ) : Result<Nothing>()

    data object Loading : Result<Nothing>()

    /**
     * Returns data if Success, null otherwise.
     */
    fun getOrNull(): T? =
        when (this) {
            is Success -> data
            else -> null
        }

    /**
     * Returns true if this is a Success result.
     */
    fun isSuccess(): Boolean = this is Success

    /**
     * Returns true if this is an Error result.
     */
    fun isError(): Boolean = this is Error

    /**
     * Returns true if this is a Loading result.
     */
    fun isLoading(): Boolean = this is Loading

    /**
     * Performs the given action if this is a Success result.
     */
    inline fun onSuccess(action: (T) -> Unit): Result<T> {
        if (this is Success) {
            action(data)
        }
        return this
    }

    /**
     * Performs the given action if this is an Error result.
     */
    inline fun onFailure(action: (Throwable) -> Unit): Result<T> {
        if (this is Error) {
            action(exception)
        }
        return this
    }
}
