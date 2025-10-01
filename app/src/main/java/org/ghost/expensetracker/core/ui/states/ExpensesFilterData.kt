package org.ghost.expensetracker.core.ui.states

import org.ghost.expensetracker.core.enums.ExpenseSortBy
import org.ghost.expensetracker.core.enums.SortOrder
import org.ghost.expensetracker.core.ui.UiState
import org.ghost.expensetracker.data.database.models.ExpenseFilters
import org.ghost.expensetracker.data.models.Category

data class ExpensesFilterData(
    val filters: ExpenseFilters,
    val categories: UiState<List<Category>>,
    val sortBy: ExpenseSortBy,
    val sortOrder: SortOrder,
    val minAmount: String,
    val maxAmount: String,
)