package org.ghost.expensetracker.data.database.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.RawQuery
import androidx.room.Update
import androidx.sqlite.db.SupportSQLiteQuery
import kotlinx.coroutines.flow.Flow
import org.ghost.expensetracker.core.enums.CardSortBy
import org.ghost.expensetracker.core.enums.SortOrder
import org.ghost.expensetracker.data.database.entity.CardEntity


@Dao
interface CardDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(card: CardEntity): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(cards: List<CardEntity>): List<Long>

    @Update
    suspend fun update(card: CardEntity)

    @Update
    suspend fun updateCards(cards: List<CardEntity>)

    @Delete
    suspend fun delete(card: CardEntity)

    @Query("DELETE FROM cards WHERE id = :id")
    suspend fun deleteCardById(id: Long): Int


    @Query("SELECT * FROM cards WHERE id = :id")
    fun getCardById(id: Long): Flow<CardEntity>

    @Query("SELECT * FROM cards WHERE profile_owner_id = :profileId AND card_company = :company AND card_last_four_digits = :lastFourDigits Limit 1")
    fun getCardByProfileAndCompanyAndLastFourDigits(
        profileId: Long,
        company: String,
        lastFourDigits: Int
    ): Flow<CardEntity?>


    @Query("SELECT COUNT(id) FROM cards")
    suspend fun getCardsCount(): Int


    /**
     * The raw query endpoint that Room will use. The observedEntities parameter is
     * crucial for making the returned Flow update automatically when the account table changes.
     */
    @RawQuery(observedEntities = [CardEntity::class])
    fun filterCardsRaw(query: SupportSQLiteQuery): Flow<List<CardEntity>>

    /**
     * This is the helper function we will call from our code. It builds the
     * complex query string and arguments, then calls the raw query function.
     */
    fun filterCards(
        profileOwnerId: Long,
        name: String? = null,
        type: String? = null,
        sortBy: CardSortBy = CardSortBy.HOLDER_NAME,
        sortOrder: SortOrder = SortOrder.ASCENDING
    ): Flow<List<CardEntity>> {
        // 1. Start building the query string
        val sqlBuilder = StringBuilder()
        sqlBuilder.append("SELECT * FROM cards WHERE profile_owner_id = ? ")
        val args = mutableListOf<Any>(profileOwnerId)

        // 2. Add optional filters
        name?.let {
            sqlBuilder.append("AND name LIKE '%' || ? || '%' ")
            args.add(it)
        }
        type?.let {
            sqlBuilder.append("AND type = ? ")
            args.add(it)
        }

        // 3. Add the dynamic ORDER BY clause
        val sortColumn = when (sortBy) {
            CardSortBy.HOLDER_NAME -> "holder_name"
            CardSortBy.TYPE -> "type"
            CardSortBy.ADDED_AT -> "added_at"
            CardSortBy.COMPANY -> "card_company"
            CardSortBy.DISPLAY_ORDER -> "display_order"
        }
        val order = if (sortOrder == SortOrder.ASCENDING) "ASC" else "DESC"
        sqlBuilder.append("ORDER BY $sortColumn $order")

        // 4. Create the final query object and execute it
        val supportQuery =
            androidx.sqlite.db.SimpleSQLiteQuery(sqlBuilder.toString(), args.toTypedArray())
        return filterCardsRaw(supportQuery)
    }
}