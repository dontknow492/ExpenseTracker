package org.ghost.expensetracker.data

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStore

// The name "app_settings" determines the file name
private const val APP_SETTINGS_NAME = "app_settings"

// This is the extension property definition
val Context.appSettingsStore: DataStore<Preferences> by preferencesDataStore(
    name = APP_SETTINGS_NAME
)