package org.ghost.expensetracker.data.repository

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.emptyPreferences
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import org.ghost.expensetracker.data.models.AppSettings
import org.ghost.expensetracker.data.models.AppSettingsKeys
import org.ghost.expensetracker.data.models.AppTheme
import org.ghost.expensetracker.data.models.Language
import org.ghost.expensetracker.data.models.StartScreen
import java.io.IOException
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Repository for managing application settings stored in DataStore.
 * It provides methods to read settings as a Flow and update individual settings.
 *
 * @param dataStore The DataStore<Preferences> instance for app settings.
 */
@Singleton
class SettingsRepository @Inject constructor(
    private val dataStore: DataStore<Preferences>
) {

    /**
     * A flow that emits the user's current AppSettings.
     * It automatically emits a new value whenever the settings are updated.
     */
    val appSettingsFlow: Flow<AppSettings> = dataStore.data
        .catch { exception ->
            // DataStore throws an IOException if it can't read the data
            if (exception is IOException) {
                // Emit empty preferences if an error occurs, allowing the map operator to handle defaults
                emit(emptyPreferences())
            } else {
                throw exception
            }
        }
        .map { preferences ->
            // Read each setting from the Preferences object, providing a default if the key doesn't exist.
            val theme = AppTheme.valueOf(
                preferences[AppSettingsKeys.APP_THEME] ?: AppTheme.SYSTEM_DEFAULT.name
            )
            val isMaterialYouEnabled = preferences[AppSettingsKeys.MATERIAL_YOU] ?: true
            val startScreen = StartScreen.valueOf(
                preferences[AppSettingsKeys.START_SCREEN] ?: StartScreen.HOME.name
            )
            val language = Language.valueOf(
                preferences[AppSettingsKeys.LANGUAGE] ?: Language.ENGLISH.name
            )
            val onboardingCompleted = preferences[AppSettingsKeys.ONBOARDING_COMPLETED] ?: false
            val lastLoginProfileId = preferences[AppSettingsKeys.LAST_LOGIN_PROFILE_ID]

            AppSettings(
                theme,
                isMaterialYouEnabled,
                startScreen,
                language,
                onboardingCompleted,
                lastLoginProfileId
            )
        }

    /**
     * Updates the application theme.
     */
    suspend fun updateTheme(theme: AppTheme) {
        dataStore.edit { preferences ->
            preferences[AppSettingsKeys.APP_THEME] = theme.name
        }
    }

    /**
     * Enables or disables the Material You dynamic theming.
     */
    suspend fun updateMaterialYou(isEnabled: Boolean) {
        dataStore.edit { preferences ->
            preferences[AppSettingsKeys.MATERIAL_YOU] = isEnabled
        }
    }

    /**
     * Updates the default start screen of the app.
     */
    suspend fun updateStartScreen(screen: StartScreen) {
        dataStore.edit { preferences ->
            preferences[AppSettingsKeys.START_SCREEN] = screen.name
        }
    }

    /**
     * Updates the application language.
     */
    suspend fun updateLanguage(language: Language) {
        dataStore.edit { preferences ->
            preferences[AppSettingsKeys.LANGUAGE] = language.name
        }
    }

    suspend fun updateOnboardingCompleted(isCompleted: Boolean) {
        dataStore.edit { preferences ->
            preferences[AppSettingsKeys.ONBOARDING_COMPLETED] = isCompleted
        }
    }

    suspend fun updateLastLogin(profileId: Long?) {
        dataStore.edit { preferences ->
            if (profileId != null) {
                // If the ID is not null, set the value.
                preferences[AppSettingsKeys.LAST_LOGIN_PROFILE_ID] = profileId
            } else {
                // If the ID IS null, REMOVE the key.
                preferences.remove(AppSettingsKeys.LAST_LOGIN_PROFILE_ID)
            }
        }
    }
}