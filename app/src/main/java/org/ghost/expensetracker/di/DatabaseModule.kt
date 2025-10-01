package org.ghost.expensetracker.di

import android.content.Context
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import org.ghost.expensetracker.data.database.ExpenseTrackerDatabase
import javax.inject.Singleton


@Module
@InstallIn(SingletonComponent::class)
class DatabaseModule {
    @Provides
    @Singleton
    fun provideExpenseTrackerDatabase(@ApplicationContext context: Context): ExpenseTrackerDatabase {
        return ExpenseTrackerDatabase.getInstance(context)
    }

    @Provides
    fun provideAccountDao(database: ExpenseTrackerDatabase) = database.accountDao()

    @Provides
    fun provideCardDao(database: ExpenseTrackerDatabase) = database.cardDao()

    @Provides
    fun provideCategoryDao(database: ExpenseTrackerDatabase) = database.categoryDao()

    @Provides
    fun provideDueDao(database: ExpenseTrackerDatabase) = database.dueDao()

    @Provides
    fun provideExpenseDao(database: ExpenseTrackerDatabase) = database.expenseDao()

    @Provides
    fun provideProfileDao(database: ExpenseTrackerDatabase) = database.profileDao()

}