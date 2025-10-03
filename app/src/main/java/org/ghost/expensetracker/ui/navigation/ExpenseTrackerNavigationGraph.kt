package org.ghost.expensetracker.ui.navigation

import android.util.Log
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.dialog
import org.ghost.expensetracker.core.ui.actions.HomeScreenActions
import org.ghost.expensetracker.data.database.models.ExpenseFilters
import org.ghost.expensetracker.data.models.Card
import org.ghost.expensetracker.data.models.Expense
import org.ghost.expensetracker.ui.screens.addScreen.AddCardScreen
import org.ghost.expensetracker.ui.screens.addScreen.AddCategoryDialog
import org.ghost.expensetracker.ui.screens.addScreen.AddExpenseScreen
import org.ghost.expensetracker.ui.screens.auth.ForgetPasswordScreen
import org.ghost.expensetracker.ui.screens.auth.LoginScreen
import org.ghost.expensetracker.ui.screens.auth.RegisterScreen
import org.ghost.expensetracker.ui.screens.main.AnalyticsScreen
import org.ghost.expensetracker.ui.screens.main.AnalyticsScreenActions
import org.ghost.expensetracker.ui.screens.main.CardsScreen
import org.ghost.expensetracker.ui.screens.main.CardsScreenActions
import org.ghost.expensetracker.ui.screens.main.HomeScreen
import org.ghost.expensetracker.ui.screens.main.ProfileScreen
import org.ghost.expensetracker.ui.screens.main.ProfileScreenActions
import org.ghost.expensetracker.ui.screens.secondary.AboutUsScreen
import org.ghost.expensetracker.ui.screens.secondary.AccountsScreen
import org.ghost.expensetracker.ui.screens.secondary.CategoryScreen
import org.ghost.expensetracker.ui.screens.secondary.ChangePasswordScreen
import org.ghost.expensetracker.ui.screens.secondary.EditProfileScreen
import org.ghost.expensetracker.ui.screens.secondary.ExpensesScreen
import org.ghost.expensetracker.ui.screens.secondary.SettingsScreen

@Composable
fun ExpenseTrackerNavyGraph(
    modifier: Modifier = Modifier,
    navHostController: NavHostController,
    startDestination: AppRoute,
) {

    val onNavigationItemClick: (AppRoute) -> Unit = { navHostController.navigate(it) }
    val onExpenseClick: (Expense) -> Unit = {}
    val onAddNewCardClick: (Long) -> Unit = {
        navHostController.navigate(AddRoute.AddCard(it))
    }

    val onNavigateToExpenses: (Long, ExpenseFilters) -> Unit = { profileOwnerId, filters ->
        val route = SecondaryRoute.Expenses(
            profileOwnerId = profileOwnerId,
            query = filters.query,
            accountId = filters.accountId,
            cardId = filters.cardId,
            categoryId = filters.categoryId,
            minDate = filters.minDate,
            maxDate = filters.maxDate,
            minAmount = filters.minAmount,
            maxAmount = filters.maxAmount,
        )
        navHostController.navigate(route)
    }

    val onCardClick: (Card) -> Unit = {
        navHostController.navigate(SecondaryRoute.Expenses(it.profileOwnerId, cardId = it.id))
    }
    val onNotificationClick: () -> Unit = {}
    val onNavigateBackClick: () -> Unit = { navHostController.popBackStack() }
    val onSendClick: (Long) -> Unit = {
        navHostController.navigate(AddRoute.AddExpense(it, true))
    }

    val onAddCategory: (Long) -> Unit = {
        navHostController.navigate(AddRoute.AddCategory(it))
    }
    val onRequestClick: (Long) -> Unit = {
        navHostController.navigate(AddRoute.AddExpense(it, false))
    }

    val onLogoutClick: () -> Unit = {
        navHostController.navigate(AuthRoute.Login) {
            // Pop up to the start destination of the graph to clear the back stack
            popUpTo(navHostController.graph.findStartDestination().id) {
                inclusive = true // Also remove the start destination
            }
            // Ensure this navigation action is a single top-level destination
            launchSingleTop = true
        }
    }

    NavHost(
        navController = navHostController,
        startDestination = startDestination,
        modifier = modifier
    ) {
        authGraph(navHostController)
        mainGraph(
            onExpenseClick = onExpenseClick,
            onAddNewCardClick = onAddNewCardClick,
            onCardClick = onCardClick,
            onSendClick = onSendClick,
            onRequestClick = onRequestClick,
            onNotificationClick = onNotificationClick,
            onNavigationItemClick = onNavigationItemClick,
            onNavigateToExpenses = onNavigateToExpenses,
            onLogoutClick = onLogoutClick,
        )
        secondaryGraph(
            onNavigateBackClick = onNavigateBackClick,
            onAddNewCategoryClick = onAddCategory,
            onNavigateToExpenses = onNavigateToExpenses,
            onExpenseClick = onExpenseClick,
            onNavigateToScreen = onNavigationItemClick,
        )
        addGraph(
            onNavigateBackClick = onNavigateBackClick,
            onCardSaved = onNavigateBackClick,
            onExpenseSaved = onNavigateBackClick,
            onAddNewCategoryClick = onAddCategory,
            onAddNewCardClick = onAddNewCardClick,
            onAddNewAccountClick = {}
        )
    }
}

private fun NavGraphBuilder.authGraph(
    navHostController: NavHostController,
) {

    composable<AuthRoute.Login> {
        LoginScreen(
            onNavigateToRegister = {
                navHostController.navigate(AuthRoute.Register)
            },
            onLoginSuccess = { id ->

                navHostController.navigate(MainRoute.Home(id)) {
                    // Pop up to the start destination of the graph to clear the back stack
                    popUpTo(navHostController.graph.findStartDestination().id) {
                        inclusive = true // Also remove the start destination
                    }
                    // Ensure this navigation action is a single top-level destination
                    launchSingleTop = true
                }
                Log.d("NavigationGraph", "onLoginSuccess: $id")
            },
            onForgotPassword = {
                navHostController.navigate(AuthRoute.ForgotPassword)
            }
        )
    }
    composable<AuthRoute.Register> {
        RegisterScreen(
            onNavigateToLogin = { navHostController.navigate(AuthRoute.Login) },
            onRegisterSuccess = { navHostController.navigate(AuthRoute.Login) }
        )
    }
    composable<AuthRoute.ForgotPassword> {
        ForgetPasswordScreen(
            onPasswordResetSuccess = { navHostController.navigate(AuthRoute.Login) },
            onNavigateToLogin = { navHostController.navigate(AuthRoute.Login) }
        )
    }

}


private fun NavGraphBuilder.mainGraph(
    onExpenseClick: (Expense) -> Unit,
    onAddNewCardClick: (Long) -> Unit,
    onCardClick: (Card) -> Unit,
    onSendClick: (Long) -> Unit,
    onRequestClick: (Long) -> Unit,
    onNotificationClick: () -> Unit,
    onNavigateToExpenses: (Long, ExpenseFilters) -> Unit,
    onNavigationItemClick: (AppRoute) -> Unit,
    onLogoutClick: () -> Unit
) {
    composable<MainRoute.Home> {
        val actions = HomeScreenActions(
            onSeeAllExpenseClick = onNavigateToExpenses,
            onExpenseClick = onExpenseClick,
            onAddNewCardClick = onAddNewCardClick,
            onCardClick = onCardClick,
            onSendClick = onSendClick,
            onRequestClick = onRequestClick,
            onNotificationClick = onNotificationClick,
            onNavigationItemClick = onNavigationItemClick,
        )
        HomeScreen(
            actions = actions,
        )
    }
    composable<MainRoute.Analytics> {
        val actions = AnalyticsScreenActions(
            onNavigationItemClick = onNavigationItemClick,
            onCategoryCardClick = {}
        )
        AnalyticsScreen(
            actions = actions
        )
    }
    composable<MainRoute.Cards> {
        val actions = CardsScreenActions(
            onNavigationItemClick = onNavigationItemClick,
            onAddCardClick = onAddNewCardClick,
            onCardClick = onCardClick
        )
        CardsScreen(
            actions = actions
        )
    }
    composable<MainRoute.Profile> {
        val actions = ProfileScreenActions(
            onNavigationItemClick = onNavigationItemClick,
            onEditProfileClick = { onNavigationItemClick(SecondaryRoute.EditProfile(it)) },
            onChangePasswordClick = { onNavigationItemClick(SecondaryRoute.ChangePassword(it)) },
            onAccountsClick = { onNavigationItemClick(SecondaryRoute.Accounts(it)) },
            onSettingsClick = { onNavigationItemClick(SecondaryRoute.Settings) },
            onAboutUsClick = { onNavigationItemClick(SecondaryRoute.AboutUs) },
            onLogoutClick = onLogoutClick
        )
        ProfileScreen(
            actions = actions
        )
    }
}


private fun NavGraphBuilder.addGraph(
    onNavigateBackClick: () -> Unit,
    onCardSaved: () -> Unit,
    onExpenseSaved: () -> Unit,
    onAddNewCategoryClick: (Long) -> Unit,
    onAddNewCardClick: (Long) -> Unit,
    onAddNewAccountClick: (Long) -> Unit,
) {
    composable<AddRoute.AddCard> {
        AddCardScreen(
            onNavigateBackClick = onNavigateBackClick,
            onCardSaved = onCardSaved,
        )
    }

    composable<AddRoute.AddExpense> {
        AddExpenseScreen(
            modifier = Modifier,
            onExpenseSaved = onExpenseSaved,
            onNavigateBackClick = onNavigateBackClick,
            onAddNewCategory = onAddNewCategoryClick,
            onAddNewAccount = onAddNewAccountClick,
            onAddNewCard = onAddNewCardClick,

            )
    }

    dialog<AddRoute.AddCategory> {
        AddCategoryDialog(
            onDismissRequest = onNavigateBackClick,
        )

    }

}

private fun NavGraphBuilder.secondaryGraph(
    onNavigateBackClick: () -> Unit,
    onNavigateToExpenses: (Long, ExpenseFilters) -> Unit,
    onAddNewCategoryClick: (Long) -> Unit,
    onExpenseClick: (Expense) -> Unit,
    onNavigateToScreen: (AppRoute) -> Unit,
) {
    composable<SecondaryRoute.Category> {
        CategoryScreen(
            onNavigateBackClick = onNavigateBackClick,
            onAddCategoryClick = onAddNewCategoryClick,
            onCategoryClick = onNavigateToExpenses
        )
    }
    composable<SecondaryRoute.Expenses> {
        ExpensesScreen(
            onNavigateBackClick = onNavigateBackClick,
            onExpenseCardClick = onExpenseClick,
            onAddExpenseClick = { id ->
                onNavigateToScreen(AddRoute.AddExpense(id, isSend = true))
            }
        )
    }

    composable<SecondaryRoute.EditProfile> {
        EditProfileScreen(
            onNavigateBack = onNavigateBackClick
        )
    }
    composable<SecondaryRoute.ChangePassword> {
        ChangePasswordScreen(
            onNavigateBack = onNavigateBackClick,
            onPasswordChanged = onNavigateBackClick
        )

    }
    composable<SecondaryRoute.Accounts> {
        AccountsScreen(
            onNavigateBack = onNavigateBackClick,
            onNavigateToAccount = {}
        )
    }

    composable<SecondaryRoute.AboutUs> {
        AboutUsScreen(
            onNavigateBack = onNavigateBackClick
        )
    }

    composable<SecondaryRoute.Settings> {
        SettingsScreen(
            onNavigateBack = onNavigateBackClick
        )
    }

}