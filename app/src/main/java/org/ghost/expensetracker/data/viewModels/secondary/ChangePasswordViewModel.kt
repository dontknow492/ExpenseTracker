package org.ghost.expensetracker.data.viewModels.secondary

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import org.ghost.expensetracker.core.ui.states.ChangePasswordUiState
import org.ghost.expensetracker.data.models.Profile
import org.ghost.expensetracker.data.useCase.profile.GetProfileUseCase
import org.ghost.expensetracker.data.useCase.profile.UpdateProfileUseCase
import javax.inject.Inject

@HiltViewModel
class ChangePasswordViewModel @Inject constructor(
    private val getProfileUseCase: GetProfileUseCase,
    private val updateProfileUseCase: UpdateProfileUseCase,
    private val savedStateHandle: SavedStateHandle
) : ViewModel() {
    private val _profileOwnerId = checkNotNull(savedStateHandle.get<Long>("profileOwnerId"))

    private val _profile: StateFlow<Profile?> = getProfileUseCase(_profileOwnerId).stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5_000),
        initialValue = null // Start with null to know when real data arrives
    )
    private val _uiState = MutableStateFlow(ChangePasswordUiState())
    val uiState: StateFlow<ChangePasswordUiState> = _uiState.asStateFlow()

    init {
        viewModelScope.launch {
            val profile = _profile.filterNotNull().first()
            _uiState.update { it.copy(email = profile.email) }
        }
    }

    /**
     * Validates input fields and triggers the password update process.
     */
    fun changePassword() {
        if (_uiState.value.isLoading) return

        if (validateInput()) {
            viewModelScope.launch {
                _uiState.update { it.copy(isLoading = true, errorMessage = null) }
                try {
                    val currentState = _uiState.value
                    val profile =
                        _profile.value ?: throw IllegalStateException("Profile not loaded.")

                    updateProfileUseCase(
                        profile = profile,
                        email = currentState.email,
                        oldPassword = currentState.oldPassword,
                        plainTextPassword = currentState.newPassword
                    )

                    _uiState.update { it.copy(isLoading = false, isPasswordChanged = true) }

                } catch (e: Exception) {
                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            errorMessage = e.message ?: "An unexpected error occurred."
                        )
                    }
                }
            }
        }
    }

    private fun validateInput(): Boolean {
        val state = _uiState.value
        // Reset errors before re-validating
        _uiState.update {
            it.copy(
                isOldPasswordError = false,
                isNewPasswordError = false,
                isConfirmPasswordError = false
            )
        }

        if (state.oldPassword.isBlank()) {
            _uiState.update { it.copy(isOldPasswordError = true) }
            return false
        }
        if (state.newPassword.length !in 8..16) { // Example validation: minimum length
            _uiState.update {
                it.copy(
                    isNewPasswordError = true,
                    errorMessage = "New password must be between 8 - 16 characters."
                )
            }
            return false
        }
        if (state.newPassword != state.confirmPassword) {
            _uiState.update { it.copy(isConfirmPasswordError = true) }
            return false
        }
        return true
    }

    /**
     * Resets one-time event flags in the state after they have been handled by the UI.
     */
    fun onMessageShown() {
        _uiState.update { it.copy(errorMessage = null, isPasswordChanged = false) }
    }

    // --- Event Handlers for UI Input ---
    fun onOldPasswordChange(password: String) {
        _uiState.update { it.copy(oldPassword = password) }
    }

    fun onNewPasswordChange(password: String) {
        _uiState.update { it.copy(newPassword = password) }
    }

    fun onConfirmPasswordChange(password: String) {
        _uiState.update { it.copy(confirmPassword = password) }
    }
}