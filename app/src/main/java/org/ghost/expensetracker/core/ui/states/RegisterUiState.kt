package org.ghost.expensetracker.core.ui.states

data class RegisterUiState(
    val firstName: String = "John",
    val lastName: String = "Doe",
    val email: String = "johndoe@gmail.com",
    val password: String = "john1@2v",
    val confirmPassword: String = "john1@2v",
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