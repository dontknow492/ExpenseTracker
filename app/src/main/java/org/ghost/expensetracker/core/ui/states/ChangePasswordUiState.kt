package org.ghost.expensetracker.core.ui.states

data class ChangePasswordUiState(
    val email: String = "", // for checking
    val oldPassword: String = "", // for checking
    val newPassword: String = "",
    val confirmPassword: String = "",

    // Specific error flags for better UI feedback
    val isOldPasswordError: Boolean = false,
    val isNewPasswordError: Boolean = false,
    val isConfirmPasswordError: Boolean = false,

    val isLoading: Boolean = false,
    val errorMessage: String? = null,
    val isPasswordChanged: Boolean = false // Flag for navigation or showing a success message
)