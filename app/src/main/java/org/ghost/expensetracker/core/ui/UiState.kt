package org.ghost.expensetracker.core.ui

sealed interface UiState<out T> {
    // Represents the loading state
    data object Loading : UiState<Nothing>

    // Represents the success state and holds the data
    data class Success<T>(val data: T) : UiState<T>

    // Represents the error state and holds an error message
    data class Error(val message: String) : UiState<Nothing>
}