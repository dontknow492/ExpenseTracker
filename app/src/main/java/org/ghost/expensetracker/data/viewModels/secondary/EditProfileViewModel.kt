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
import org.ghost.expensetracker.core.utils.isEmailValid
import org.ghost.expensetracker.data.models.Profile
import org.ghost.expensetracker.data.useCase.profile.GetProfileUseCase
import org.ghost.expensetracker.data.useCase.profile.UpdateProfileUseCase
import javax.inject.Inject

data class EditProfileUiState(
    val email: String = "",
    val firstName: String = "",
    val lastName: String = "",
    val avatarUri: String? = null,
    val avatarUrl: String? = null,

    // Specific error flags for better UI feedback
    val isFirstNameError: Boolean = false,
    val isLastNameError: Boolean = false,
    val isEmailError: Boolean = false,

    val isLoading: Boolean = false,
    val errorMessage: String? = null,
    val isProfileSaved: Boolean = false // Flag for navigation or showing a success message
)


@HiltViewModel
class EditProfileViewModel @Inject constructor(
    private val updateProfileUseCase: UpdateProfileUseCase,
    private val getProfileUseCase: GetProfileUseCase,
    private val savedStateHandle: SavedStateHandle
) : ViewModel() {

    private val _profileOwnerId = checkNotNull(savedStateHandle.get<Long>("profileOwnerId"))

    private val _profile: StateFlow<Profile?> = getProfileUseCase(_profileOwnerId).stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5_000),
        initialValue = null // Start with null to know when real data arrives
    )

    private val _uiState = MutableStateFlow(EditProfileUiState())
    val uiState: StateFlow<EditProfileUiState> = _uiState.asStateFlow()

    init {
        // Observe the profile flow and update the UI state when data is loaded.
        viewModelScope.launch {
            val profile = _profile.filterNotNull().first() // Wait for the first non-null profile
            _uiState.update {
                it.copy(
                    email = profile.email,
                    firstName = profile.firstName,
                    lastName = profile.lastName,
                    avatarUri = profile.avatarUri,
                    avatarUrl = profile.avatarUrl
                )
            }
        }
    }


    fun updateProfile() {
        if (_uiState.value.isLoading) return

        if (validateInput()) {
            viewModelScope.launch {
                _uiState.update { it.copy(isLoading = true, errorMessage = null) }
                try {
                    val currentState = _uiState.value
                    // Create a Profile object from the current state to pass to the use case.
                    val updatedProfile = Profile(
                        id = _profileOwnerId,
                        firstName = currentState.firstName.trim(),
                        lastName = currentState.lastName.trim(),
                        email = currentState.email.trim(), // Assuming email can be updated
                        avatarUri = currentState.avatarUri,
                        avatarUrl = currentState.avatarUrl
                    )

                    updateProfileUseCase(updatedProfile)

                    _uiState.update { it.copy(isLoading = false, isProfileSaved = true) }

                } catch (e: Exception) {
                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            isEmailError = true,
                            errorMessage = e.message ?: "An unexpected error occurred."
                        )
                    }
                }
            }
        }
    }

    private fun validateInput(): Boolean {
        val state = _uiState.value
        val isFirstNameValid = state.firstName.isNotBlank()
        val isLastNameValid = state.lastName.isNotBlank()
        val isEmailValid = isEmailValid(state.email)

        _uiState.update {
            it.copy(
                isFirstNameError = !isFirstNameValid,
                isLastNameError = !isLastNameValid,
                isEmailError = !isEmailValid,
            )
        }

        return isFirstNameValid && isLastNameValid
    }

    fun onFirstNameChange(firstName: String) {
        _uiState.update {
            it.copy(firstName = firstName, isFirstNameError = false, errorMessage = null)
        }
    }

    fun onLastNameChange(lastName: String) {
        _uiState.update {
            it.copy(lastName = lastName, isLastNameError = false, errorMessage = null)
        }
    }

    fun onEmailChange(email: String) {
        _uiState.update {
            it.copy(email = email, isEmailError = false, errorMessage = null)
        }
    }


    fun onAvatarUriChange(avatarUri: String?) {
        _uiState.update {
            it.copy(avatarUri = avatarUri)
        }
    }

    // This is not typically set directly by user input, but by a file upload success callback.
    // Kept for completeness.
    fun onAvatarUrlChange(avatarUrl: String?) {
        _uiState.update {
            it.copy(avatarUrl = avatarUrl)
        }
    }

}