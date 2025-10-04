package org.ghost.expensetracker.data.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import org.ghost.expensetracker.data.database.dao.AccountDao
import org.ghost.expensetracker.data.database.dao.CardDao
import org.ghost.expensetracker.data.database.dao.CategoryDao
import org.ghost.expensetracker.data.database.dao.DueDao
import org.ghost.expensetracker.data.database.dao.ExpenseDao
import org.ghost.expensetracker.data.database.dao.ProfileDao
import org.ghost.expensetracker.data.database.entity.AccountEntity
import org.ghost.expensetracker.data.database.entity.CardEntity
import org.ghost.expensetracker.data.database.entity.CategoryEntity
import org.ghost.expensetracker.data.database.entity.DueEntity
import org.ghost.expensetracker.data.database.entity.ExpenseEntity
import org.ghost.expensetracker.data.database.entity.ProfileEntity

@Database(
    entities = [
        ProfileEntity::class,
        CategoryEntity::class,
        AccountEntity::class,
        CardEntity::class,
        DueEntity::class,
        ExpenseEntity::class,
    ],
    version = 1,
)
abstract class ExpenseTrackerDatabase : RoomDatabase() {

    abstract fun cardDao(): CardDao
    abstract fun profileDao(): ProfileDao
    abstract fun categoryDao(): CategoryDao
    abstract fun accountDao(): AccountDao
    abstract fun expenseDao(): ExpenseDao
    abstract fun dueDao(): DueDao

    companion object {
        private var INSTANCE: ExpenseTrackerDatabase? = null
        fun getInstance(context: Context): ExpenseTrackerDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context = context.applicationContext,
                    klass = ExpenseTrackerDatabase::class.java,
                    name = "expense_tracker_database"
                ).build()
                INSTANCE = instance


//                runBlocking {
//                    // 1. Create all mock objects first
//                    val mockProfiles = createMockProfiles(10)
//
//                    // 2. Perform a single bulk insert for profiles to get their real, generated IDs
//                    val profileIds = instance.profileDao().insertAll(mockProfiles)
//
//                    // Create master lists for all other entities
//                    val allCategories = mutableListOf<CategoryEntity>()
//                    val allAccounts = mutableListOf<AccountEntity>()
//                    val allCards = mutableListOf<CardEntity>()
//
//                    // 3. Associate profiles with their new IDs and generate their related data
//                    val profilesWithIds = mockProfiles.zip(profileIds) { profile, id ->
//                        profile.copy(id = id)
//                    }
//
//                    profilesWithIds.forEach { profile ->
//                        allCategories.addAll(createMockCategories(profile.id))
//                        allAccounts.addAll(createMockAccounts(profile.id))
//                        allCards.addAll(createMockCards(profile.id, profile.firstName))
//                    }
//
//                    // 4. Perform bulk inserts for all other entities to get their IDs
//                    val categoryIds = instance.categoryDao().insertAll(allCategories)
//                    val accountIds = instance.accountDao().insertAll(allAccounts)
//                    val cardIds = instance.cardDao().insertAll(allCards)
//
//                    // 5. Create valid ID ranges from the insertion results
//                    // Added .takeIf { it.isNotEmpty() } to handle cases where no items were inserted
//                    val profileRange = profileIds.minOrNull()?.rangeTo(profileIds.maxOrNull()!!)
//                    val categoryRange = categoryIds.minOrNull()?.rangeTo(categoryIds.maxOrNull()!!)
//                    val accountRange = accountIds.minOrNull()?.rangeTo(accountIds.maxOrNull()!!)
//                    val cardRange = cardIds.minOrNull()?.rangeTo(cardIds.maxOrNull()!!)
//
//                    // 6. Generate expenses using the correct, dynamic ID ranges
//                    val expenses = createMockExpenses(
//                        count = 10000,
//                        profileRange = profileRange ?: 0L..0L,
//                        categoryRange = categoryRange ?: 0L..0L,
//                        accountRange = accountRange ?: 0L..0L,
//                        cardRange = cardRange ?: 0L..0L
//                    )
//
//                    // 7. Perform a final bulk insert for all expenses
//                    instance.expenseDao().insertExpenses(expenses)
//                }

                instance
            }
        }
    }
}