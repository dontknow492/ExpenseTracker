package org.ghost.expensetracker.core.ui.states

import java.io.File

data class EditProfileUiState(
    val email: String = "",
    val firstName: String = "",
    val lastName: String = "",
    val avatarUrl: String? = null,
    val avatarFilePath: File? = null,


    // Specific error flags for better UI feedback
    val isFirstNameError: Boolean = false,
    val isLastNameError: Boolean = false,
    val isEmailError: Boolean = false,
    val isAvatarError: Boolean = false,


    val isLoading: Boolean = false,
    val errorMessage: String? = null,
    val isProfileSaved: Boolean = false // Flag for navigation or showing a success message
)