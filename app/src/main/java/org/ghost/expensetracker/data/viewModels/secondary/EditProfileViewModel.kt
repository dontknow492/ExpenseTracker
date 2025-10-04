package org.ghost.expensetracker.data.viewModels.secondary

import android.content.Context
import android.net.Uri
import android.util.Log
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import org.ghost.expensetracker.core.ui.states.EditProfileUiState
import org.ghost.expensetracker.core.utils.ImageUtils
import org.ghost.expensetracker.core.utils.isEmailValid
import org.ghost.expensetracker.data.models.Profile
import org.ghost.expensetracker.data.useCase.profile.GetProfileUseCase
import org.ghost.expensetracker.data.useCase.profile.UpdateProfileUseCase
import java.io.File
import javax.inject.Inject


@HiltViewModel
class EditProfileViewModel @Inject constructor(
    private val updateProfileUseCase: UpdateProfileUseCase,
    private val getProfileUseCase: GetProfileUseCase,
    private val savedStateHandle: SavedStateHandle,
    @param: ApplicationContext private val context: Context,
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
                    avatarFilePath = if (profile.avatarFilePath != null) File(profile.avatarFilePath) else null,
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
                        email = _profile.value?.email ?: currentState.email, // Assuming email can be updated
                        avatarFilePath = currentState.avatarFilePath?.absolutePath,
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


    fun onAvatarUriChange(avatarUri: Uri?) {
        Log.d("EditProfileViewModel", "Cached image path: ${avatarUri}")

        if(avatarUri == null) return
        viewModelScope.launch {
            // ... (Show a loading indicator)

            val cachedImageFile = ImageUtils.cacheImageFromUri(context, avatarUri)

            Log.d("EditProfileViewModel", "Cached image path: ${cachedImageFile?.absolutePath}")


            if (cachedImageFile != null) {
                // Now, save the *path* of the new file to your database
                val newAvatarPath = cachedImageFile.absolutePath


                // If there was an old avatar, delete it to save space
                val oldAvatarPath = _profile.value?.avatarFilePath
                if (oldAvatarPath != null) {
                    ImageUtils.deleteCacheImage(context, oldAvatarPath)
                }

                _uiState.update {
                    it.copy(
                        avatarFilePath = cachedImageFile,
                        isLoading = false,
                        isAvatarError = false
                    )
                }

            } else {
                _uiState.update {
                    it.copy(
                        errorMessage = "Failed to save avatar. Please try again.",
                        isLoading = false,
                        isAvatarError = true
                    )
                }
            }
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