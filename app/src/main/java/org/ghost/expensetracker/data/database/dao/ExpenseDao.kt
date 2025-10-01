package org.ghost.expensetracker.data.database.dao

import androidx.paging.PagingSource
import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.RawQuery
import androidx.room.Update
import androidx.sqlite.db.SimpleSQLiteQuery
import androidx.sqlite.db.SupportSQLiteQuery
import kotlinx.coroutines.flow.Flow
import org.ghost.expensetracker.core.enums.ExpenseGroupBy
import org.ghost.expensetracker.core.enums.ExpenseSortBy
import org.ghost.expensetracker.core.enums.SortOrder
import org.ghost.expensetracker.data.database.entity.AccountEntity
import org.ghost.expensetracker.data.database.entity.CardEntity
import org.ghost.expensetracker.data.database.entity.CategoryEntity
import org.ghost.expensetracker.data.database.entity.ExpenseEntity
import org.ghost.expensetracker.data.database.models.ExpenseFilters
import org.ghost.expensetracker.data.models.ExpenseChartData

/**
 * A private helper that builds the common WHERE and ORDER BY clauses for expense queries.
 * @return A Pair containing the SQL query string and the list of arguments.
 */
private fun buildFilteredQueryArgs(
    profileOwnerId: Long,
    filters: ExpenseFilters,
    sortBy: ExpenseSortBy,
    sortOrder: SortOrder
): Pair<String, List<Any>> {
    val sqlBuilder = StringBuilder()
    // NOTE: We start with the WHERE clause, not SELECT
    sqlBuilder.append("WHERE profile_owner_id = ? ")
    val args = mutableListOf<Any>(profileOwnerId)

    // Add optional filters (this is your exact code from before)
    filters.isSend?.let { isSend: Boolean ->
        sqlBuilder.append("AND is_send = ? ")
        args.add(isSend)
    }

    filters.query?.let {
        sqlBuilder.append("AND (title LIKE '%' || ? || '%' OR description LIKE '%' || ? || '%') ")
        args.add(it)
        args.add(it)
    }
    filters.accountId?.let {
        sqlBuilder.append("AND account_id = ? ")
        args.add(it)
    }
    filters.cardId?.let {
        sqlBuilder.append("AND card_id = ? ")
        args.add(it)
    }
    filters.categoryId?.let {
        sqlBuilder.append("AND category_id = ? ")
        args.add(it)
    }
    filters.minDate?.let {
        sqlBuilder.append("AND date >= ? ")
        args.add(it)
    }
    filters.maxDate?.let {
        sqlBuilder.append("AND date <= ? ")
        args.add(it)
    }
    filters.minAmount?.let {
        sqlBuilder.append("AND amount >= ? ")
        args.add(it)
    }
    filters.maxAmount?.let {
        sqlBuilder.append("AND amount <= ? ")
        args.add(it)
    }

    // Add the dynamic ORDER BY clause
    val sortColumn = when (sortBy) {
        ExpenseSortBy.DATE -> "date"
        ExpenseSortBy.AMOUNT -> "amount"
        ExpenseSortBy.TITLE -> "title"
    }
    val order = if (sortOrder == SortOrder.ASCENDING) "ASC" else "DESC"
    sqlBuilder.append("ORDER BY $sortColumn $order")

    return Pair(sqlBuilder.toString(), args)
}

@Dao
interface ExpenseDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertExpense(expense: ExpenseEntity): Long


    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertExpenses(expenses: List<ExpenseEntity>): List<Long>

    @Update
    suspend fun updateExpense(expense: ExpenseEntity)


    @Delete
    suspend fun deleteExpense(expense: ExpenseEntity)

    @Delete
    suspend fun deleteExpenses(expenses: List<ExpenseEntity>)

    @Query("DELETE FROM expenses WHERE id = :expenseId")
    suspend fun deleteExpenseById(expenseId: Long): Int

    @Query("DELETE FROM expenses WHERE id IN (:expenseIds)")
    suspend fun deleteExpenseByIds(expenseIds: List<Long>): Int


    @Query("SELECT * FROM expenses WHERE id = :expenseId")
    fun getExpenseById(expenseId: Long): Flow<ExpenseEntity?>


    /**
     * The raw query endpoint that Room will use to execute our dynamic query.
     * We will not call this directly from our repository.
     */
    @RawQuery(observedEntities = [ExpenseEntity::class])
    fun filterExpensesRaw(query: SupportSQLiteQuery): PagingSource<Int, ExpenseEntity>

    @RawQuery(observedEntities = [ExpenseEntity::class])
    fun getAllExpenseIdsRaw(query: SupportSQLiteQuery): Flow<List<Long>>


    /**
     * This is the helper function we will call from our code. It builds the
     * complex query string and arguments, then calls the raw query function.
     */
    fun filterExpenses(
        profileOwnerId: Long,
        filters: ExpenseFilters,
        sortBy: ExpenseSortBy = ExpenseSortBy.DATE,
        sortOrder: SortOrder = SortOrder.DESCENDING
    ): PagingSource<Int, ExpenseEntity> {
        // 1. Get the common query parts from the helper
        val (query, args) = buildFilteredQueryArgs(profileOwnerId, filters, sortBy, sortOrder)

        // 2. Prepend the SELECT clause and create the final query
        val finalQueryString = "SELECT * FROM expenses $query"
        val supportQuery = SimpleSQLiteQuery(finalQueryString, args.toTypedArray())

        // 3. Execute it
        return filterExpensesRaw(supportQuery)
    }


    fun getAllExpenseIds(
        profileOwnerId: Long,
        filters: ExpenseFilters,
        sortBy: ExpenseSortBy = ExpenseSortBy.DATE,
        sortOrder: SortOrder = SortOrder.DESCENDING
    ): Flow<List<Long>> {
        // 1. Get the common query parts from the same helper
        val (query, args) = buildFilteredQueryArgs(profileOwnerId, filters, sortBy, sortOrder)

        // 2. Prepend a DIFFERENT SELECT clause and create the final query
        val finalQueryString = "SELECT id FROM expenses $query"
        val supportQuery = SimpleSQLiteQuery(finalQueryString, args.toTypedArray())

        // 3. Execute it with the appropriate DAO method
        return getAllExpenseIdsRaw(supportQuery)
    }


    @RawQuery(observedEntities = [ExpenseEntity::class, CategoryEntity::class, AccountEntity::class, CardEntity::class])
    fun getChartDataRaw(query: SupportSQLiteQuery): Flow<List<ExpenseChartData>>

    //CHART
    fun filterExpensesChartData(
        profileOwnerId: Long,
        grouping: ExpenseGroupBy,
        filters: ExpenseFilters,
    ): Flow<List<ExpenseChartData>> {

        val fromExpense = "From expenses"
        val groupBy = "GROUP BY label"

        val (selectClause, fromAndJoinClause, groupByClause) = when (grouping) {
            ExpenseGroupBy.YEAR -> Triple(
                // Corrected: Convert the Unix timestamp before formatting
                "strftime('%Y', date / 1000, 'unixepoch') as label, SUM(amount) as totalAmount",
                fromExpense,
                groupBy
            )

            ExpenseGroupBy.MONTH -> Triple(
                // Corrected: Convert the Unix timestamp before formatting
                "strftime('%Y-%m', date / 1000, 'unixepoch') as label, SUM(amount) as totalAmount",
                fromExpense,
                groupBy
            )

            ExpenseGroupBy.WEEK -> Triple(
                // Corrected: Convert the Unix timestamp before formatting
                "strftime('%Y-%W', date / 1000, 'unixepoch') as label, SUM(amount) as totalAmount",
                fromExpense,
                groupBy
            )

            ExpenseGroupBy.DAY -> Triple(
                // Corrected: Convert the Unix timestamp before formatting
                "strftime('%Y-%m-%d', date / 1000, 'unixepoch') as label, SUM(amount) as totalAmount",
                fromExpense,
                groupBy
            )

            ExpenseGroupBy.CATEGORY -> Triple(
                "C.name as label, SUM(E.amount) as totalAmount",
                "FROM expenses E JOIN categories C ON E.category_id = C.id",
                groupBy
            )

            ExpenseGroupBy.ACCOUNT -> Triple(
                "A.name as label, SUM(E.amount) as totalAmount",
                "FROM expenses E JOIN accounts A ON E.account_id = A.id",
                groupBy
            )

            ExpenseGroupBy.CARD -> Triple(
                "Card.holder_name as label, SUM(E.amount) as totalAmount",
                "FROM expenses E JOIN cards Card ON E.card_id = Card.id",
                groupBy
            )
        }

        // STEP 2: Reuse your existing function to build the WHERE clause
        // We ignore the ORDER BY part from this function for chart queries.
        val (whereClause, args) = buildFilteredQueryArgs(
            profileOwnerId,
            filters,
            ExpenseSortBy.DATE, // Default sort for filtering
            SortOrder.ASCENDING
        )

        val finalWhereClause = whereClause.substringBefore(" ORDER BY")
            // IMPORTANT: Alias the table in the WHERE clause if joins are used.
            // We can check if a JOIN is present to decide whether to add the alias 'E.'.
            .let {
                if (fromAndJoinClause.contains("JOIN")) {
                    it.replace("WHERE ", "WHERE E.")
                        .replace("AND ", "AND E.")
                } else {
                    it
                }
            }

        val orderByClause = "ORDER BY date ASC"


        val finalQueryString = """
            SELECT $selectClause
            $fromAndJoinClause
            $finalWhereClause
            $groupByClause
            $orderByClause
        """.trimIndent()

        val supportQuery = SimpleSQLiteQuery(finalQueryString, args.toTypedArray())

        return getChartDataRaw(supportQuery)
    }
}

