package org.ghost.expensetracker.data.database.dao

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
import org.ghost.expensetracker.data.database.entity.CategoryEntity
import org.ghost.expensetracker.data.database.entity.ExpenseEntity
import org.ghost.expensetracker.data.database.entity.relation.CategoryEntityWithExpenseCount

@Dao
interface CategoryDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(category: CategoryEntity): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(categories: List<CategoryEntity>): List<Long>

    @Update
    suspend fun update(category: CategoryEntity): Int

    @Update
    suspend fun updateCategories(categories: List<CategoryEntity>) // <-- Add an update method

    @Delete
    suspend fun delete(category: CategoryEntity): Int

    @Query("DELETE FROM categories WHERE id = :id")
    suspend fun deleteCategoryById(id: Long): Int

    @Query("SELECT * FROM categories WHERE id = :id")
    fun getCategoryById(id: Long): Flow<CategoryEntity?>

    @Query("SELECT COUNT(id) FROM categories WHERE profile_owner_id = :profileOwnerId")
    suspend fun getCategoryCountForProfile(profileOwnerId: Long): Int

    @Query("SELECT * FROM categories WHERE name = :name AND profile_owner_id = :profileOwnerId LIMIT 1")
    fun getCategoryByNameAndProfile(name: String, profileOwnerId: Long): Flow<CategoryEntity?>

    @Query("SELECT * FROM categories where profile_owner_id = :profileOwnerId ORDER BY display_order ASC")
    fun getAllCategoriesForProfile(profileOwnerId: Long): Flow<List<CategoryEntity>>


    /**
     * This is the public function you will call from your repository.
     * It dynamically builds the SQL query string and the list of arguments
     * based on which filters (minDate, maxDate) are provided.
     */
    fun getCategoriesWithExpenseCount(
        profileOwnerId: Long,
        minDate: Long?,
        maxDate: Long?
    ): Flow<List<CategoryEntityWithExpenseCount>> {

        // The subquery to count expenses
        val countSubQuery =
            StringBuilder("SELECT COUNT(e.id) FROM expenses AS e WHERE e.category_id = c.id AND e.profile_owner_id = ?")
        val args = mutableListOf<Any>(profileOwnerId)

        // Dynamically add date filters to the subquery
        minDate?.let {
            countSubQuery.append(" AND e.date >= ?")
            args.add(it)
        }
        maxDate?.let {
            countSubQuery.append(" AND e.date <= ?")
            args.add(it)
        }

        // The main query that uses the subquery we just built
        val finalQuery =
            StringBuilder("SELECT c.*, ($countSubQuery) AS expenseCount FROM categories AS c WHERE c.profile_owner_id = ? ORDER BY c.display_order ASC")
        args.add(profileOwnerId) // Add the profileOwnerId for the main query's WHERE clause

        val supportQuery = SimpleSQLiteQuery(finalQuery.toString(), args.toTypedArray())

        return getCategoriesWithExpenseCountRaw(supportQuery)
    }

    /**
     * This internal function executes the dynamic query.
     * @RawQuery tells Room to run the provided query object.
     * `observedEntities` is CRUCIAL: it tells Room that this query depends on the
     * 'categories' and 'expenses' tables, so the Flow will automatically update
     * when data in either of those tables changes.
     */
    @RawQuery(observedEntities = [CategoryEntity::class, ExpenseEntity::class])
    fun getCategoriesWithExpenseCountRaw(query: SupportSQLiteQuery): Flow<List<CategoryEntityWithExpenseCount>>

}