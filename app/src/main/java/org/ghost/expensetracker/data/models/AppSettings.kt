package org.ghost.expensetracker.data.models

import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.longPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import org.ghost.expensetracker.ui.navigation.MainRoute

/**
 * A data class representing the user's application settings.
 * It includes default values for when the app is first launched.
 */
data class AppSettings(
    val theme: AppTheme = AppTheme.SYSTEM_DEFAULT,
    val isMaterialYouEnabled: Boolean = true,
    val startScreen: StartScreen = StartScreen.HOME,
    val language: Language = Language.ENGLISH,
    val isOnboarded: Boolean = false, // New setting
    val lastLoginProfileId: Long? = null // New setting
)

/**
 * Enum for the available app themes.
 */
enum class AppTheme {
    LIGHT,
    DARK,
    SYSTEM_DEFAULT
}

/**
 * Enum for the possible start screens of the app.
 * The names should correspond to your navigation routes.
 */
enum class StartScreen(val route: String) {
    HOME("home"),
    ANALYTICS("analytics"),
    CARDS("cards"),
    PROFILE("profile"),
    // Add other potential start screens here
}

fun StartScreen.toNav(profileOwnerId: Long): MainRoute {
    return when (this) {
        StartScreen.HOME -> MainRoute.Home(profileOwnerId)
        StartScreen.ANALYTICS -> MainRoute.Analytics(profileOwnerId)
        StartScreen.CARDS -> MainRoute.Cards(profileOwnerId)
        StartScreen.PROFILE -> MainRoute.Profile(profileOwnerId)
    }
}

/**
 * Enum for supported languages, storing the standard IETF language tag.
 */
enum class Language(val code: String) {
    ENGLISH("en"),
    SPANISH("es"),
    GERMAN("de"),
    FRENCH("fr")
    // Add other supported languages here
}

/**
 * Defines the keys used to store settings in Jetpack DataStore.
 * Using an object ensures that these keys are singletons and prevents typos.
 */
object AppSettingsKeys {
    val APP_THEME = stringPreferencesKey("app_theme")
    val MATERIAL_YOU = booleanPreferencesKey("material_you")
    val START_SCREEN = stringPreferencesKey("start_screen")
    val LANGUAGE = stringPreferencesKey("language")
    val ONBOARDING_COMPLETED = booleanPreferencesKey("onboarding_completed")
    val LAST_LOGIN_PROFILE_ID = longPreferencesKey("last_login_profile_id")
}
