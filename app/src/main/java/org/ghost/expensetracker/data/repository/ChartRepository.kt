package org.ghost.expensetracker.data.repository

import kotlinx.coroutines.flow.Flow
import org.ghost.expensetracker.core.enums.ExpenseGroupBy
import org.ghost.expensetracker.data.database.dao.ExpenseDao
import org.ghost.expensetracker.data.database.models.ExpenseFilters
import org.ghost.expensetracker.data.models.ExpenseChartData
import javax.inject.Inject

class ChartRepository @Inject constructor(
    private val expenseDao: ExpenseDao,
) {
    fun filterExpensesChartData(
        profileOwnerId: Long,
        grouping: ExpenseGroupBy,
        filters: ExpenseFilters,
    ): Flow<List<ExpenseChartData>> {
        return expenseDao.filterExpensesChartData(
            profileOwnerId = profileOwnerId,
            grouping = grouping,
            filters = filters,
        )
    }
}