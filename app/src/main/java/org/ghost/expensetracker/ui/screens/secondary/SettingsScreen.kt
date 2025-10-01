package org.ghost.expensetracker.ui.screens.secondary

import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SegmentedButton
import androidx.compose.material3.SegmentedButtonDefaults
import androidx.compose.material3.SingleChoiceSegmentedButtonRow
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import org.ghost.expensetracker.data.models.AppSettings
import org.ghost.expensetracker.data.models.AppTheme
import org.ghost.expensetracker.data.models.Language
import org.ghost.expensetracker.data.models.StartScreen
import org.ghost.expensetracker.data.viewModels.secondary.SettingsUiState
import org.ghost.expensetracker.data.viewModels.secondary.SettingsViewModel
import org.ghost.expensetracker.ui.components.SettingsCollapsibleEnumItem
import org.ghost.expensetracker.ui.components.SettingsSwitchItem

data class SettingsScreenActions(
    val onNavigateBack: () -> Unit,
    val setTheme: (AppTheme) -> Unit,
    val setLanguage: (Language) -> Unit,
    val setMaterialYou: (Boolean) -> Unit,
    val setStartScreen: (StartScreen) -> Unit
)

@Composable
fun SettingsScreen(
    modifier: Modifier = Modifier,
    viewModel: SettingsViewModel = hiltViewModel(),
    onNavigateBack: () -> Unit,
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val actions = SettingsScreenActions(
        onNavigateBack = onNavigateBack,
        setTheme = viewModel::onThemeChange,
        setLanguage = viewModel::onLanguageChange,
        setMaterialYou = viewModel::onMaterialYouChange,
        setStartScreen = viewModel::onStartScreenChange
    )

    SettingsScreenContent(
        modifier = modifier,
        uiState = uiState,
        actions = actions
    )

}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreenContent(
    modifier: Modifier = Modifier,
    uiState: SettingsUiState,
    actions: SettingsScreenActions
) {
    val appSettings = uiState.appSettings
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(text = "Settings") },
                navigationIcon = {
                    IconButton(
                        onClick = actions.onNavigateBack
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back"
                        )
                    }
                    // Back button
                }
            )
        }
    ) { innerPadding ->
        LazyColumn(
            modifier = modifier.padding(innerPadding)
        ) {
            item {
                SingleChoiceSegmentedButtonRow(
                    modifier = Modifier
                ) {
                    AppTheme.entries.forEachIndexed { index, theme ->
                        SegmentedButton(
                            onClick = { actions.setTheme(theme) },
                            selected = theme == appSettings.theme,
                            shape = SegmentedButtonDefaults.itemShape(
                                index = index,
                                count = AppTheme.entries.size
                            ),
                            label = { Text(theme.name, maxLines = 1) }
                        )
                    }
                }
            }
            item {
                SettingsSwitchItem(
                    title = "Use Material You",
                    description = "Enable or disable Material You colors",
                    checked = appSettings.isMaterialYouEnabled,
                    searchQuery = "",
                    onCheckedChange = actions.setMaterialYou
                )
            }
            item {
                SettingsCollapsibleEnumItem(
                    title = "Start Screen",
                    description = "Set the screen to start on",
                    options = StartScreen.entries,
                    currentSelection = appSettings.startScreen,
                    onSelectionChange = actions.setStartScreen,
                    searchQuery = ""
                )
            }
            item {
                SettingsCollapsibleEnumItem(
                    title = "Language",
                    description = "Set the screen to language",
                    options = Language.entries,
                    currentSelection = appSettings.language,
                    onSelectionChange = actions.setLanguage,
                    searchQuery = ""
                )
            }
        }
    }
}

@Preview
@Composable
fun SettingsScreenContentPreview() {
    val uiState = SettingsUiState(
        isLoading = false,
        appSettings = AppSettings(
            theme = AppTheme.DARK,
            isMaterialYouEnabled = true,
            startScreen = StartScreen.HOME
        )
    )
    val actions = SettingsScreenActions(
        setTheme = {},
        setLanguage = {},
        setMaterialYou = {},
        setStartScreen = {},
        onNavigateBack = {}
    )
    SettingsScreenContent(uiState = uiState, actions = actions)
}