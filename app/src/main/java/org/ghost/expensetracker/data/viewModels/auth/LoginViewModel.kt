package org.ghost.expensetracker.data.viewModels.auth

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import org.ghost.expensetracker.core.exceptions.InvalidCredentialsException
import org.ghost.expensetracker.core.exceptions.InvalidEmailFormatException
import org.ghost.expensetracker.core.exceptions.InvalidPasswordFormatException
import org.ghost.expensetracker.core.ui.states.LoginUiState
import org.ghost.expensetracker.data.repository.SettingsRepository
import org.ghost.expensetracker.data.useCase.LoginUseCase
import javax.inject.Inject

@HiltViewModel
class LoginViewModel @Inject constructor(
    private val loginUseCase: LoginUseCase,
    private val settingsRepository: SettingsRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(LoginUiState())
    val uiState = _uiState.asStateFlow()

    fun onEmailChange(email: String) {
        _uiState.update {
            it.copy(
                email = email,
                error = null,
                isEmailError = false,
                isPasswordError = false
            )
        }
    }

    fun onPasswordChange(password: String) {
        _uiState.update {
            it.copy(
                password = password,
                error = null,
                isPasswordError = false,
                isEmailError = false
            )
        }
    }

    fun onLoginClicked() {
        _uiState.update { it.copy(isLoading = true) }

        val currentState = _uiState.value
        viewModelScope.launch {
            val result = loginUseCase(
                email = currentState.email.trim(),
                plainTextPassword = currentState.password
            )

            result.onSuccess { loggedInProfile ->
                // Login was successful!
                settingsRepository.updateLastLogin(loggedInProfile.id)
                settingsRepository.updateOnboardingCompleted(true)

                _uiState.update {
                    it.copy(
                        isLoading = false,
                        isLoginSuccess = true,
                        loggedInProfile = loggedInProfile
                    )
                }
            }.onFailure { exception ->
                // Login failed. Show the error message from the exception.
                when (exception) {
                    is InvalidEmailFormatException -> {
                        _uiState.update {
                            it.copy(
                                isEmailError = true,
                                error = exception.message,
                                isLoading = false
                            )
                        }
                    }

                    is InvalidPasswordFormatException -> {
                        _uiState.update {
                            it.copy(
                                isPasswordError = true,
                                error = exception.message,
                                isLoading = false
                            )
                        }
                    }

                    is InvalidCredentialsException -> {
                        _uiState.update {
                            it.copy(
                                error = exception.message,
                                isLoading = false
                            )
                        }
                    }

                    else -> {
                        _uiState.update {
                            it.copy(
                                error = exception.message,
                                isLoading = false,
                            )
                        }
                    }
                }
            }
        }
    }

    fun saveLastLoginProfileId(profileId: Long) {
        viewModelScope.launch {
            settingsRepository.updateLastLogin(profileId)
        }
        Log.d("LoginViewModel", "saveLastLoginProfileId: $profileId")
    }
}