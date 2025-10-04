package org.ghost.expensetracker.core.ui.states

data class RegisterUiState(
    val firstName: String = "",
    val lastName: String = "",
    val email: String = "",
    val password: String = "",
    val confirmPassword: String = "",
    val isLoading: Boolean = false,
    val error: String? = null,
    val isEmailError: Boolean = false,
    val isPasswordError: Boolean = false,
    val isConfirmPasswordError: Boolean = false,
    val isFirstNameError: Boolean = false,
    val isLastNameError: Boolean = false,
    val isDBError: Boolean = false,
    val isRegistrationSuccess: Boolean = false
)