package org.ghost.expensetracker.data.database.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow
import org.ghost.expensetracker.data.database.entity.AccountEntity

@Dao
interface AccountDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(account: AccountEntity): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(accounts: List<AccountEntity>): List<Long>

    @Update
    suspend fun update(account: AccountEntity): Int

    @Update
    suspend fun updateAccounts(accounts: List<AccountEntity>)

    @Delete
    suspend fun delete(account: AccountEntity): Int

    @Query("DELETE FROM accounts WHERE id = :id")
    suspend fun deleteAccountById(id: Long): Int

    @Query("SELECT * FROM accounts WHERE id = :id")
    fun getAccountById(id: Long): Flow<AccountEntity?>

    @Query("SELECT * FROM accounts WHERE profile_owner_id = :profileId AND name = :name Limit 1")
    fun getAccountByProfileAndName(profileId: Long, name: String): Flow<AccountEntity?>

    @Query("SELECT COUNT(id) FROM accounts")
    suspend fun getAccountsCount(): Int

    @Query("SELECT * FROM accounts where profile_owner_id = :profileId ORDER BY display_order")
    fun getAllAccountsForProfile(profileId: Long): Flow<List<AccountEntity>>


    @Query("SELECT * FROM accounts where profile_owner_id = :profileId AND is_default = 1 LIMIT 1")
    fun getDefaultAccountForProfile(profileId: Long): Flow<AccountEntity?>


}