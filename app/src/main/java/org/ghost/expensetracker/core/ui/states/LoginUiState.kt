package org.ghost.expensetracker.core.ui.states

import org.ghost.expensetracker.data.models.Profile

// Represents the state of the login form on the screen
data class LoginUiState(
    val email: String = "",
    val password: String = "",
    val isLoading: Boolean = false,
    val isEmailError: Boolean = false,
    val isPasswordError: Boolean = false,
    val error: String? = null,
    val loggedInProfile: Profile? = null,
    val isLoginSuccess: Boolean = false
)