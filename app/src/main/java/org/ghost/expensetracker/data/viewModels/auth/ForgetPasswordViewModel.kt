package org.ghost.expensetracker.data.viewModels.auth

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import org.ghost.expensetracker.core.exceptions.FailedToUpdateDB
import org.ghost.expensetracker.core.exceptions.InvalidEmailFormatException
import org.ghost.expensetracker.core.exceptions.InvalidNameException
import org.ghost.expensetracker.core.exceptions.InvalidPasswordFormatException
import org.ghost.expensetracker.core.ui.states.ForgetPasswordUiState
import org.ghost.expensetracker.data.models.Profile
import org.ghost.expensetracker.data.useCase.ResetPasswordUseCase
import javax.inject.Inject


@HiltViewModel
class ForgetPasswordViewModel @Inject constructor(
    private val resetPasswordUseCase: ResetPasswordUseCase
) : ViewModel() {
    private val _uiState = MutableStateFlow(ForgetPasswordUiState())
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

    fun onForgetPasswordClicked() {
        // --- 1. Client-Side Validation ---
        val currentState = _uiState.value
        _uiState.update {
            it.copy(
                isLoading = true,
                isEmailError = false,
                isPasswordError = false,
                isConfirmPasswordError = false,
                isFirstNameError = false,
                isLastNameError = false,
                error = null
            )
        }

        // --- 2. Construct the Profile Object ---
        val newProfile = Profile(
            id = 0,
            firstName = currentState.firstName.trim(),
            lastName = currentState.lastName.trim(),
            avatarFilePath = null,
            avatarUrl = null,
            email = "",
        )

        // --- 3. Call the Use Case ---
        viewModelScope.launch {
            val result = resetPasswordUseCase(
                newProfile,
                currentState.email,
                currentState.password,
                currentState.confirmPassword
            )

            result.onSuccess { _ ->
                _uiState.update { it.copy(isLoading = false, isPasswordResetSuccess = true) }
                Log.d("ForgetPasswordScreen", "Password reset successfully.")
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

                    is FailedToUpdateDB -> {
                        _uiState.update {
                            it.copy(
                                isLoading = false,
                                error = exception.message,
                                isDBError = true
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