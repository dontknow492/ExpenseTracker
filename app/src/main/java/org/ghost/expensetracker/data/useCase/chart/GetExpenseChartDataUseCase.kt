package org.ghost.expensetracker.data.useCase.chart

import kotlinx.coroutines.flow.Flow
import org.ghost.expensetracker.core.enums.ExpenseGroupBy
import org.ghost.expensetracker.data.database.models.ExpenseFilters
import org.ghost.expensetracker.data.models.ExpenseChartData
import org.ghost.expensetracker.data.repository.ChartRepository
import javax.inject.Inject

class GetExpenseChartDataUseCase @Inject constructor(
    private val chartRepository: ChartRepository
) {
    operator fun invoke(
        profileOwnerId: Long,
        groupBy: ExpenseGroupBy,
        filters: ExpenseFilters,
    ): Flow<List<ExpenseChartData>> {
        return chartRepository.filterExpensesChartData(
            profileOwnerId = profileOwnerId,
            grouping = groupBy,
            filters = filters
        )
    }

}