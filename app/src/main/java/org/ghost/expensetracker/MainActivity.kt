package org.ghost.expensetracker

import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.runtime.getValue
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import dagger.hilt.android.AndroidEntryPoint
import org.ghost.expensetracker.data.models.AppTheme
import org.ghost.expensetracker.data.models.toNav
import org.ghost.expensetracker.data.viewModels.secondary.SettingsViewModel
import org.ghost.expensetracker.data.viewModels.secondary.StartupDestinationState
import org.ghost.expensetracker.ui.navigation.AuthRoute
import org.ghost.expensetracker.ui.navigation.ExpenseTrackerNavyGraph
import org.ghost.expensetracker.ui.theme.ExpensiveTheme

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    lateinit var navHostController: NavHostController

    private val settingsViewModel: SettingsViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        setContent {
            navHostController = rememberNavController()
            val settingsState by settingsViewModel.uiState.collectAsStateWithLifecycle()
            val startDestination by settingsViewModel.startupDestinationState.collectAsStateWithLifecycle()
            ExpensiveTheme(
                dynamicColor = settingsState.appSettings.isMaterialYouEnabled,
                darkTheme = when (settingsState.appSettings.theme) {
                    AppTheme.LIGHT -> false
                    AppTheme.DARK -> true
                    else -> isSystemInDarkTheme()
                }
            ) {

                val startupDestination = when (startDestination) {
                    StartupDestinationState.Loading -> {
                        null
                    }

                    StartupDestinationState.Login -> AuthRoute.Login
                    is StartupDestinationState.Main -> {
                        val profileId = (startDestination as StartupDestinationState.Main).profileId
                        settingsState.appSettings.startScreen.toNav(profileId)
                    }
                }

                Log.d("MainActivity", "onCreate: ${settingsState.appSettings}")

////                val startDestination = AddRoute.AddExpense(1, isSend = true)
//                val startDestination = MainRoute.Cards(1)
////                val startDestination = AuthRoute.Register
////                val startDestination = Main
////                val startDestination = SecondaryRoute.Expenses(1)
////                val startDestination = SecondaryRoute.Category(1)

                if (startupDestination == null) return@ExpensiveTheme

                ExpenseTrackerNavyGraph(
                    navHostController = navHostController,
                    startDestination = startupDestination,
                )
            }
        }
    }
}
