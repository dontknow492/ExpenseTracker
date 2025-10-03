package org.ghost.expensetracker.core.ui.actions

import org.ghost.expensetracker.core.enums.ExpenseSortBy
import org.ghost.expensetracker.core.enums.SortOrder
import org.ghost.expensetracker.data.database.models.ExpenseFilters
import org.ghost.expensetracker.data.models.Expense

data class ExpensesScreenActions(
    val onNavigateBackClick: () -> Unit,
    val onAddExpenseClick: () -> Unit,
    val onExpenseCardClick: (Expense) -> Unit,
    val onExpenseLongClick: (Expense) -> Unit,
    val onUpdateQuery: (String?) -> Unit,
    val onUpdateAccountId: (Long?) -> Unit,
    val onUpdateCategory: (Long?) -> Unit,
    val onUpdateDateRange: (Pair<Long?, Long?>) -> Unit,
    val onMinAmountInputChange: (String) -> Unit,
    val onMaxAmountInputChange: (String) -> Unit,
    val onUpdateExpenseFilters: (ExpenseFilters) -> Unit,
    val onUpdateSortBy: (ExpenseSortBy) -> Unit,
    val onUpdateSortOrder: (SortOrder) -> Unit,
    val onDeleteExpense: (Expense) -> Unit,
    val onSelectAll: () -> Unit,
    val onDeselectAll: () -> Unit,
    val onInvertSelection: () -> Unit,
    val onClearFilters: () -> Unit,
    val onDeleteSelected: () -> Unit,
)