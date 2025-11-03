package com.example.nexusnews.presentation.common

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.nexusnews.util.ErrorHandler
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.launch
import timber.log.Timber

/**
 * Base ViewModel providing common functionality:
 * - Error handling
 * - Event management (one-time UI events)
 * - Loading state management
 */
abstract class BaseViewModel : ViewModel() {
    private val _events = MutableSharedFlow<UiEvent>()
    val events: SharedFlow<UiEvent> = _events.asSharedFlow()

    /**
     * Coroutine exception handler for viewModelScope.
     */
    protected val exceptionHandler =
        CoroutineExceptionHandler { _, throwable ->
            Timber.e(
                throwable,
                "Uncaught exception in ViewModel",
            )

            handleError(throwable)
        }

    /**
     * Handle errors and convert to user-friendly messages.
     */
    protected open fun handleError(throwable: Throwable) {
        val message = ErrorHandler.getErrorMessage(throwable)
        viewModelScope.launch {
            _events.emit(UiEvent.ShowError(message))
        }
    }

    /**
     * Emit a one-time UI event.
     */
    protected fun emitEvent(event: UiEvent) {
        viewModelScope.launch {
            _events.emit(event)
        }
    }
}

/**
 * Sealed interface for one-time UI events.
 */
sealed interface UiEvent {
    data class ShowError(
        val message: String,
    ) : UiEvent

    data class ShowSuccess(
        val message: String,
    ) : UiEvent

    data class Navigate(
        val route: String,
    ) : UiEvent
}
