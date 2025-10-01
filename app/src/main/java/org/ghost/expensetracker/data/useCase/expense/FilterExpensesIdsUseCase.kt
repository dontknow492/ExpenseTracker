package org.ghost.expensetracker.data.useCase.expense


import kotlinx.coroutines.flow.Flow
import org.ghost.expensetracker.core.enums.ExpenseSortBy
import org.ghost.expensetracker.core.enums.SortOrder
import org.ghost.expensetracker.data.database.models.ExpenseFilters
import org.ghost.expensetracker.data.models.Expense
import org.ghost.expensetracker.data.repository.LedgerRepository
import javax.inject.Inject

class FilterExpensesIdsUseCase @Inject constructor(
    private val ledgerRepository: LedgerRepository
) {
    operator fun invoke(
        profileOwnerId: Long,
        filters: ExpenseFilters,
        sortBy: ExpenseSortBy = ExpenseSortBy.DATE,
        sortOrder: SortOrder = SortOrder.ASCENDING
    ): Flow<List<Long>> {
        return ledgerRepository.filterExpensesIds(
            profileOwnerId = profileOwnerId,
            filters = filters,
            sortBy = sortBy,
            sortOrder = sortOrder
        )
    }
}


class DeleteExpensesUseCase @Inject constructor(
    private val ledgerRepository: LedgerRepository
) {
    suspend operator fun invoke(expense: Expense): Boolean {
        return ledgerRepository.deleteExpenseById(expense.id)
    }

    suspend operator fun invoke(expenseIds: List<Long>): Boolean {
        return ledgerRepository.deleteExpensesById(expenseIds)
    }

}