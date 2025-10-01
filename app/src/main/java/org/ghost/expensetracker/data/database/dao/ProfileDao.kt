package org.ghost.expensetracker.data.database.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow
import org.ghost.expensetracker.data.database.entity.ProfileEntity

@Dao
interface ProfileDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(profile: ProfileEntity): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(profiles: List<ProfileEntity>): List<Long>

    @Update
    suspend fun update(profile: ProfileEntity): Int

    @Delete
    suspend fun delete(profile: ProfileEntity): Int

    @Query(
        """
        DELETE FROM profiles 
        WHERE id = :id AND email = :email AND password_hash = :passwordHash
    """
    )
    suspend fun deleteProfileWithCredentials(id: Long, email: String, passwordHash: String): Int

    @Query("SELECT * FROM profiles")
    fun getAllProfiles(): Flow<List<ProfileEntity>>

    @Query("SELECT * FROM profiles WHERE id = :id")
    fun getProfileById(id: Long): Flow<ProfileEntity?>

    @Query("SELECT * FROM profiles WHERE email = :email AND password_hash = :passwordHash")
    fun getProfileByEmailAndPasswordHash(
        email: String,
        passwordHash: String,
    ): Flow<ProfileEntity?>

    @Query("SELECT * FROM profiles WHERE email = :email AND first_name = :firstName AND last_name = :lastName")
    fun getProfileByEmailAndName(
        email: String,
        firstName: String,
        lastName: String
    ): Flow<ProfileEntity?>


}