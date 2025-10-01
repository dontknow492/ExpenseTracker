package org.ghost.expensetracker.data.viewModels.auth

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import org.ghost.expensetracker.core.exceptions.InvalidEmailFormatException
import org.ghost.expensetracker.core.exceptions.InvalidNameException
import org.ghost.expensetracker.core.exceptions.InvalidPasswordFormatException
import org.ghost.expensetracker.core.ui.states.RegisterUiState
import org.ghost.expensetracker.data.models.Profile
import org.ghost.expensetracker.data.useCase.profile.CreateProfileUseCase
import javax.inject.Inject


@HiltViewModel
class RegisterViewModel @Inject constructor(
    private val createProfileUseCase: CreateProfileUseCase
) : ViewModel() {
    private val _uiState = MutableStateFlow(RegisterUiState())
    val uiState = _uiState.asStateFlow()

    fun onFirstNameChange(firstName: String) {
        _uiState.update {
            it.copy(
                firstName = firstName, error = null,
                isFirstNameError = false, isLastNameError = false, isEmailError = false
            )
        }
    }

    fun onLastNameChange(lastName: String) {
        _uiState.update {
            it.copy(
                lastName = lastName, error = null,
                isFirstNameError = false, isLastNameError = false, isEmailError = false
            )
        }
    }

    fun onEmailChange(email: String) {
        _uiState.update {
            it.copy(
                email = email, error = null,
                isLastNameError = false, isFirstNameError = false, isEmailError = false
            )
        }
    }

    fun onPasswordChange(password: String) {
        _uiState.update {
            it.copy(
                password = password, error = null,
                isPasswordError = false, isConfirmPasswordError = false
            )
        }
    }

    fun onConfirmPasswordChange(confirmPassword: String) {
        _uiState.update {
            it.copy(
                confirmPassword = confirmPassword, error = null,
                isPasswordError = false, isConfirmPasswordError = false
            )
        }
    }

    fun onRegisterClicked() {
        // --- 1. Client-Side Validation ---
        val currentState = _uiState.value

        if (currentState.firstName.isBlank() || !(currentState.firstName.length <= 16 && currentState.firstName.length >= 4)) {
            _uiState.update {
                it.copy(
                    error = "First name must be between 4 and 16 characters.",
                    isLastNameError = true
                )
            }
            return
        }

        if (currentState.lastName.isBlank() || !(currentState.lastName.length <= 16 && currentState.lastName.isNotEmpty())) {
            _uiState.update {
                it.copy(
                    error = "Last name must be between 1 and 16 characters.",
                    isLastNameError = true
                )
            }
            return
        }

        _uiState.update {
            it.copy(
                isLoading = true,
                error = null,
                isLastNameError = false,
                isFirstNameError = false,
                isEmailError = false,
                isPasswordError = false,
                isConfirmPasswordError = false
            )
        }

        // --- 2. Construct the Profile Object ---
        val newProfile = Profile(
            id = 0,
            firstName = currentState.firstName.trim(),
            lastName = currentState.lastName.trim(),
            avatarUri = null,
            avatarUrl = null,
            email = "",
        )

        // --- 3. Call the Use Case ---
        viewModelScope.launch {
            val result = createProfileUseCase(
                newProfile,
                currentState.email,
                currentState.password,
                currentState.confirmPassword
            )

            result.onSuccess { _ ->
                _uiState.update { it.copy(isLoading = false, isRegistrationSuccess = true) }
            }.onFailure { exception ->
                when (exception) {
                    is InvalidEmailFormatException -> {
                        _uiState.update {
                            it.copy(
                                isLoading = false,
                                error = exception.message,
                                isEmailError = true
                            )
                        }
                    }


                    is IllegalStateException -> {
                        _uiState.update {
                            it.copy(
                                isLoading = false,
                                error = exception.message,
                                isEmailError = true,
                                isFirstNameError = true,
                                isLastNameError = true,
                            )
                        }
                    }

                    is InvalidNameException -> {
                        _uiState.update {
                            it.copy(
                                isLoading = false,
                                error = exception.message,
                                isFirstNameError = true,
                                isLastNameError = true,
                            )
                        }
                    }

                    is InvalidPasswordFormatException -> {
                        _uiState.update {
                            it.copy(
                                isLoading = false,
                                error = exception.message,
                                isPasswordError = true,
                                isConfirmPasswordError = true
                            )
                        }
                    }

                    else -> {
                        _uiState.update { it.copy(isLoading = false, error = exception.message) }
                    }
                }
            }
        }
    }
}