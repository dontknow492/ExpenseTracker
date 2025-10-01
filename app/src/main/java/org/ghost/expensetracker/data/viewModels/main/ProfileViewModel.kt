package org.ghost.expensetracker.data.viewModels.main

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import org.ghost.expensetracker.data.repository.SettingsRepository
import org.ghost.expensetracker.data.useCase.profile.GetProfileUseCase
import javax.inject.Inject

@HiltViewModel
class ProfileViewModel @Inject constructor(
    private val savedStateHandle: SavedStateHandle,
    private val getProfileUseCase: GetProfileUseCase,
    private val settingsRepository: SettingsRepository,
) : ViewModel() {
    private val _profileOwnerId: Long = checkNotNull(savedStateHandle["profileOwnerId"])
    val profileOwnerId: Long = _profileOwnerId

    val profileState = getProfileUseCase(_profileOwnerId).stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5_000),
        initialValue = null
    )

    fun onLogout() {
        viewModelScope.launch {
            settingsRepository.updateLastLogin(null)
        }
    }


}