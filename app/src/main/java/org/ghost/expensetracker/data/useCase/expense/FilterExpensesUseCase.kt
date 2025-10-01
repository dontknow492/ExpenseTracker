package org.ghost.expensetracker.data.useCase.expense

import androidx.paging.PagingData
import kotlinx.coroutines.flow.Flow
import org.ghost.expensetracker.core.enums.ExpenseSortBy
import org.ghost.expensetracker.core.enums.SortOrder
import org.ghost.expensetracker.data.database.entity.ExpenseEntity
import org.ghost.expensetracker.data.database.models.ExpenseFilters
import org.ghost.expensetracker.data.repository.LedgerRepository
import javax.inject.Inject

class FilterExpensesUseCase @Inject constructor(
    private val ledgerRepository: LedgerRepository
) {
    operator fun invoke(
        profileOwnerId: Long,
        filters: ExpenseFilters,
        sortBy: ExpenseSortBy = ExpenseSortBy.DATE,
        sortOrder: SortOrder = SortOrder.ASCENDING
    ): Flow<PagingData<ExpenseEntity>> {
        return ledgerRepository.filterExpenses(
            profileOwnerId = profileOwnerId,
            filters = filters,
            sortBy = sortBy,
            sortOrder = sortOrder
        )
    }
}