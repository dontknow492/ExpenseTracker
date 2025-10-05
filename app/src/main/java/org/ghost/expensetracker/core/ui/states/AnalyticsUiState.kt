package org.ghost.expensetracker.core.ui.states

import org.ghost.expensetracker.core.enums.ExpenseType
import org.ghost.expensetracker.core.enums.TimeFilter
import org.ghost.expensetracker.data.models.Account
import org.ghost.expensetracker.data.models.Card
import org.ghost.expensetracker.data.models.Category
import org.ghost.expensetracker.data.models.ExpenseChartData

data class AnalyticsUiState(
    val isLoading: Boolean = true,
    val isIncomeChartLoading: Boolean = true,
    val isSpendChartLoading: Boolean = true,
    val error: String? = null,
    val isIncomeChartError: Boolean = false,
    val isSpendChartError: Boolean = false,

    //category
    val category: Category? = null,
    val categoryExpenseType: ExpenseType = ExpenseType.RECIVE,
    val categoryError: String? = null,
    val isCategoryLoading: Boolean = false,
    val isCategoryChartError: Boolean = false,

    //card
    val cards: Card? = null,
    val cardsError: String? = null,
    val isCardLoading: Boolean = false,
    val isCardChartError: Boolean = false,

    //account
    val account: Account? = null,
    val accountExpenseType: ExpenseType = ExpenseType.RECIVE,
    val accountsError: String? = null,
    val isAccountLoading: Boolean = false,
    val isAccountChartError: Boolean = false,


    //filter
    val incomeTimeFilter: TimeFilter,
    val expenseTimeFilter: TimeFilter,
    val categoryTimeFilter: TimeFilter,
    val cardTimeFilter: TimeFilter,
    val accountTimeFilter: TimeFilter,

    val categoryExpenseData: List<ExpenseChartData> = emptyList(), // Assuming your use case returns List<ChartData>
    val cardExpenseData: List<ExpenseChartData> = emptyList(),
    val accountExpenseData: List<ExpenseChartData> = emptyList(),
    val spendData: List<ExpenseChartData> = emptyList(),
    val incomeData: List<ExpenseChartData> = emptyList()
    // Add other chart data or UI properties here
)