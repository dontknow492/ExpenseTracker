package org.ghost.expensetracker.core.ui.actions

import org.ghost.expensetracker.data.models.AppTheme
import org.ghost.expensetracker.data.models.Language
import org.ghost.expensetracker.data.models.StartScreen

data class SettingsScreenActions(
    val onNavigateBack: () -> Unit,
    val setTheme: (AppTheme) -> Unit,
    val setLanguage: (Language) -> Unit,
    val setMaterialYou: (Boolean) -> Unit,
    val setStartScreen: (StartScreen) -> Unit
)