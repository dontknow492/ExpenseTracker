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
import org.ghost.expensetracker.core.enums.DueSortBy
import org.ghost.expensetracker.core.enums.RecurringUnit
import org.ghost.expensetracker.core.enums.SortOrder
import org.ghost.expensetracker.data.database.entity.DueEntity
import org.ghost.expensetracker.data.database.models.DueFilters

@Dao
interface DueDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertDue(due: DueEntity): Long


    @Update
    suspend fun updateDue(due: DueEntity)

    @Delete
    suspend fun deleteDue(due: DueEntity)

    @Query("DELETE FROM dues WHERE id = :dueId")
    suspend fun deleteDueById(dueId: Long): Int

    @Query("DELETE FROM dues WHERE id IN (:dueIds)")
    suspend fun deleteDueByIds(dueIds: List<Long>): Int

    @Query("SELECT * FROM dues WHERE id = :dueId")
    fun getDueById(dueId: Long): Flow<DueEntity?>

    /**
     * The raw query endpoint that Room will use. The observedEntities parameter is
     * crucial for making the returned Flow update automatically when the account table changes.
     */
    @RawQuery(observedEntities = [DueEntity::class])
    fun filterDuesRaw(query: SupportSQLiteQuery): PagingSource<Int, DueEntity>


    /**
     * This is the helper function we will call from our code. It builds the
     * complex query string and arguments, then calls the raw query function.
     */
    fun filterDues(
        profileOwnerId: Long,
        filters: DueFilters,
        sortBy: DueSortBy = DueSortBy.LAST_PAYMENT_DATE,
        sortOrder: SortOrder = SortOrder.DESCENDING
    ): PagingSource<Int, DueEntity> {
        val sqlBuilder = StringBuilder()
        sqlBuilder.append("SELECT * FROM dues WHERE profile_owner_id = ? ")
        val args = mutableListOf<Any>(profileOwnerId)


        filters.query?.let {
            sqlBuilder.append("AND (name LIKE '%' || ? || '%' OR description LIKE '%' || ? || '%') ")
            args.add(it)
            args.add(it)
        }

        filters.categoryId?.let {
            sqlBuilder.append("AND category_id = ? ")
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
        filters.currency?.let {
            sqlBuilder.append("AND currency = ? ")
            args.add(it)
        }
        filters.minDate?.let {
            sqlBuilder.append("AND creation_timestamp >= ? ")
            args.add(it)
        }
        filters.maxDate?.let {
            sqlBuilder.append("AND creation_timestamp <= ? ")
            args.add(it)
        }
        filters.minLastPaymentDate?.let {
            sqlBuilder.append("AND last_payment_date >= ? ")
            args.add(it)
        }
        filters.maxLastPaymentDate?.let {
            sqlBuilder.append("AND last_payment_date <= ? ")
            args.add(it)
        }
        if (filters.isRecurring) {
            sqlBuilder.append("AND is_recurring = 1 ")
            filters.recurrenceInterval?.let {
                sqlBuilder.append("AND recurrence_interval = ? ")
                args.add(it)
            }

            filters.recurrenceUnit?.let {
                val unit = when (it) {
                    RecurringUnit.DAILY -> "daily"
                    RecurringUnit.WEEKLY -> "weekly"
                    RecurringUnit.MONTHLY -> "monthly"
                    RecurringUnit.YEARLY -> "yearly"
                }
                sqlBuilder.append("AND recurrence_unit = ? ")
                args.add(unit)
            }
        } else {
            sqlBuilder.append("AND is_recurring = 0 ")
        }


        val sortColumn = when (sortBy) {
            DueSortBy.NAME -> "name"
            DueSortBy.AMOUNT -> "amount"
            DueSortBy.DATE -> "date"
            DueSortBy.LAST_PAYMENT_DATE -> "last_payment_date"
            DueSortBy.RECURRENCE_INTERVAL -> "recurrence_interval"
        }

        val sortOrderString = if (sortOrder == SortOrder.ASCENDING) "ASC" else "DESC"

        sqlBuilder.append("ORDER BY $sortColumn $sortOrderString")
        val supportQuery = SimpleSQLiteQuery(sqlBuilder.toString(), args.toTypedArray())
        return filterDuesRaw(supportQuery)

    }

}

