package org.ghost.expensetracker.core.ui.states

data class ForgetPasswordUiState(
    val firstName: String = "",
    val lastName: String = "",
    val email: String = "",
    val password: String = "",
    val confirmPassword: String = "",
    val passwordMatch: Boolean = true,
    val isLoading: Boolean = false,
    val error: String? = null,
    val isEmailError: Boolean = false,
    val isPasswordError: Boolean = false,
    val isConfirmPasswordError: Boolean = false,
    val isFirstNameError: Boolean = false,
    val isLastNameError: Boolean = false,
    val isPasswordResetSuccess: Boolean = false,
    val isDBError: Boolean = false
)