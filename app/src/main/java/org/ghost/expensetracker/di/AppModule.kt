package org.ghost.expensetracker.di

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
//import org.ghost.expensetracker.data.local.appSettingsStore // <- Adjust import
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import org.ghost.expensetracker.data.appSettingsStore
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Provides
    @Singleton
    fun provideAppSettingsDataStore(
        @ApplicationContext context: Context
    ): DataStore<Preferences> {
        // This uses the extension property we created earlier to get the DataStore instance
        return context.appSettingsStore
    }
}
