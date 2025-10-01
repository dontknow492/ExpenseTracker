package org.ghost.expensetracker.core.ui.actions

import org.ghost.expensetracker.data.database.models.ExpenseFilters
import org.ghost.expensetracker.data.models.Card
import org.ghost.expensetracker.data.models.Expense
import org.ghost.expensetracker.ui.navigation.AppRoute

data class HomeScreenActions(
    val onSeeAllExpenseClick: (Long, ExpenseFilters) -> Unit,
    val onExpenseClick: (Expense) -> Unit,
    val onAddNewCardClick: (Long) -> Unit,
    val onCardClick: (Card) -> Unit,
    val onSendClick: (Long) -> Unit,
    val onRequestClick: (Long) -> Unit,
    val onNotificationClick: () -> Unit,
    val onNavigationItemClick: (AppRoute) -> Unit,
)