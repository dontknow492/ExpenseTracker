package org.ghost.expensetracker.data.viewModels.secondary

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import org.ghost.expensetracker.data.models.AppSettings
import org.ghost.expensetracker.data.models.AppTheme
import org.ghost.expensetracker.data.models.Language
import org.ghost.expensetracker.data.models.StartScreen
import org.ghost.expensetracker.data.repository.SettingsRepository
import org.ghost.expensetracker.data.useCase.profile.GetProfileUseCase
import javax.inject.Inject

data class SettingsUiState(
    val isLoading: Boolean = true,
    val appSettings: AppSettings = AppSettings() // Default settings
)

sealed interface StartupDestinationState {
    object Loading : StartupDestinationState
    data class Main(val profileId: Long) : StartupDestinationState
    object Login : StartupDestinationState
    object Onboarding : StartupDestinationState
}

@HiltViewModel
class SettingsViewModel @Inject constructor(
    private val settingsRepository: SettingsRepository,
    private val getProfileUseCase: GetProfileUseCase,
) : ViewModel() {

    /**
     * A StateFlow that holds the current UI state for the settings screen.
     * It maps the AppSettings flow from the repository into a SettingsUiState.
     */
    val uiState: StateFlow<SettingsUiState> = settingsRepository.appSettingsFlow
        .map { appSettings ->
            // The mapping function transforms the data layer model to a UI state
            SettingsUiState(isLoading = false, appSettings = appSettings)
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000), // Keep the flow active for 5s after the last collector disappears
            initialValue = SettingsUiState(isLoading = true) // The initial state is loading
        )

    @OptIn(ExperimentalCoroutinesApi::class)
    val startupDestinationState: StateFlow<StartupDestinationState> =
        settingsRepository.appSettingsFlow
            .flatMapLatest { settings ->
                val lastProfileId = settings.lastLoginProfileId
                val onboarded = settings.isOnboarded

                if (!onboarded){
                    flowOf(StartupDestinationState.Onboarding)
                }
                else if (lastProfileId == null) {
                    // Case 1: No last user was saved. Go directly to Login.
                    // We wrap the result in a flow so flatMapLatest can use it.
                    flowOf(StartupDestinationState.Login)
                } else {
                    // Case 2: A user ID was saved. We must now observe the database
                    // to see if this user still exists.
                    getProfileUseCase(lastProfileId)
                        .map { profile ->
                            Log.d("MainActivity", "startupDestinationState: $profile")
                            if (profile != null) {
                                // Profile exists in the database. Go to Home.
                                StartupDestinationState.Main(lastProfileId)
                            } else {
                                // The ID was saved but the profile is gone. This is an invalid state.
                                // We "self-heal" by clearing the bad ID from settings.
                                settingsRepository.updateLastLogin(null)
                                // Then, we must go to Login.
                                StartupDestinationState.Login
                            }
                        }
                }
            }
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(5_000),
                initialValue = StartupDestinationState.Loading
            )

    /**
     * Handles the theme change event from the UI.
     */
    fun onThemeChange(theme: AppTheme) {
        viewModelScope.launch {
            settingsRepository.updateTheme(theme)
        }
    }

    /**
     * Handles the Material You toggle event from the UI.
     */
    fun onMaterialYouChange(isEnabled: Boolean) {
        viewModelScope.launch {
            settingsRepository.updateMaterialYou(isEnabled)
        }
    }

    /**
     * Handles the start screen change event from the UI.
     */
    fun onStartScreenChange(screen: StartScreen) {
        viewModelScope.launch {
            settingsRepository.updateStartScreen(screen)
        }
    }

    /**
     * Handles the language change event from the UI.
     */
    fun onLanguageChange(language: Language) {
        viewModelScope.launch {
            settingsRepository.updateLanguage(language)
        }
    }


}