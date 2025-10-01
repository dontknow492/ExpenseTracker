package org.ghost.expensetracker.data.repository

import androidx.paging.Pager
import androidx.paging.PagingConfig
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import org.ghost.expensetracker.core.enums.DueSortBy
import org.ghost.expensetracker.core.enums.ExpenseSortBy
import org.ghost.expensetracker.core.enums.SortOrder
import org.ghost.expensetracker.data.database.dao.DueDao
import org.ghost.expensetracker.data.database.dao.ExpenseDao
import org.ghost.expensetracker.data.database.models.DueFilters
import org.ghost.expensetracker.data.database.models.ExpenseFilters
import org.ghost.expensetracker.data.mappers.toDomainModel
import org.ghost.expensetracker.data.mappers.toEntity
import org.ghost.expensetracker.data.models.Due
import org.ghost.expensetracker.data.models.Expense
import javax.inject.Inject

class LedgerRepository @Inject constructor(
    private val expenseDao: ExpenseDao,
    private val dueDao: DueDao
) {
    private val pageSize = 20
    suspend fun addExpense(
        expense: Expense,
    ) {
        expenseDao.insertExpense(
            expense.toEntity()
        )
    }

    suspend fun updateExpense(
        expense: Expense,
    ) {
        expenseDao.updateExpense(
            expense.toEntity()
        )
    }

    suspend fun deleteExpenseById(expenseId: Long): Boolean {
        return expenseDao.deleteExpenseById(expenseId) > 0
    }

    suspend fun deleteExpensesById(expenseIds: List<Long>): Boolean {
        return expenseDao.deleteExpenseByIds(expenseIds) > 0
    }

    fun getExpenseById(expenseId: Long): Flow<Expense?> {
        return expenseDao.getExpenseById(expenseId)
            .map { expenseEntity -> expenseEntity?.toDomainModel() }
    }

    fun filterExpenses(
        profileOwnerId: Long,
        filters: ExpenseFilters,
        sortBy: ExpenseSortBy = ExpenseSortBy.DATE,
        sortOrder: SortOrder = SortOrder.DESCENDING
    ) = Pager(
        config = PagingConfig(pageSize = this.pageSize),
        pagingSourceFactory = {
            expenseDao.filterExpenses(
                profileOwnerId = profileOwnerId,
                filters = filters,
                sortBy = sortBy,
                sortOrder = sortOrder
            )
        }
    ).flow

    fun filterExpensesIds(
        profileOwnerId: Long,
        filters: ExpenseFilters,
        sortBy: ExpenseSortBy = ExpenseSortBy.DATE,
        sortOrder: SortOrder = SortOrder.DESCENDING
    ): Flow<List<Long>> {
        return expenseDao.getAllExpenseIds(
            profileOwnerId = profileOwnerId,
            filters = filters,
            sortBy = sortBy,
            sortOrder = sortOrder
        )
    }

    suspend fun addDue(
        due: Due,
    ): Long {
        return dueDao.insertDue(
            due.toEntity()
        )
    }

    suspend fun updateDue(
        due: Due,
    ) {
        dueDao.updateDue(
            due.toEntity()
        )
    }

    suspend fun deleteDueById(dueId: Long): Boolean {
        return dueDao.deleteDueById(dueId) > 0
    }

    suspend fun deleteDuesById(dueIds: List<Long>): Boolean {
        return dueDao.deleteDueByIds(dueIds) > 0
    }

    fun getDueById(dueId: Long): Flow<Due?> {
        return dueDao.getDueById(dueId).map { dueEntity -> dueEntity?.toDomainModel() }
    }

    fun filterDues(
        profileOwnerId: Long,
        filters: DueFilters,
        sortBy: DueSortBy = DueSortBy.DATE,
        sortOrder: SortOrder = SortOrder.DESCENDING
    ) = Pager(
        config = PagingConfig(pageSize = this.pageSize),
        pagingSourceFactory = {
            dueDao.filterDues(
                profileOwnerId = profileOwnerId,
                filters = filters,
                sortBy = sortBy,
                sortOrder = sortOrder
            )
        }
    )


}