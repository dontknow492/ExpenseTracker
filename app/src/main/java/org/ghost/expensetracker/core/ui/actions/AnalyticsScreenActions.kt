package org.ghost.expensetracker.core.ui.actions

import org.ghost.expensetracker.core.enums.ExpenseType
import org.ghost.expensetracker.core.enums.TimeFilter

data class AnalyticsScreenActions(
    val onCategoryItemClick: (String) -> Unit,
    val onAccountItemClick: (String) -> Unit,
    val onCategoryFilterChange: (TimeFilter) -> Unit,
    val onCategoryTypeFilterChange: (ExpenseType) -> Unit,

    val onAccountFilterChange: (TimeFilter) -> Unit,
    val onAccountTypeFilterChange: (ExpenseType) -> Unit,

    val onCardFilterChange: (String) -> Unit,
    val onCardItemClick: (String) -> Unit,
    val onIncomeFilterChange: (TimeFilter) -> Unit,
    val onExpenseFilterChange: (TimeFilter) -> Unit,
)