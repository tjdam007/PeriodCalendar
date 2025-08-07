package com.mjandroiddev.periodcalendar.di

import android.content.Context
import androidx.room.Room
import com.mjandroiddev.periodcalendar.data.database.*
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {

    @Provides
    @Singleton
    fun provideContext(@ApplicationContext context: Context): Context = context

    @Provides
    @Singleton
    fun providePeriodDatabase(@ApplicationContext context: Context): PeriodDatabase {
        return Room.databaseBuilder(
            context,
            PeriodDatabase::class.java,
            PeriodDatabase.DATABASE_NAME
        ).fallbackToDestructiveMigration() // For development - remove in production
            .build()
    }

    // New DAOs
    @Provides
    fun provideCycleEntryDao(database: PeriodDatabase): CycleEntryDao {
        return database.cycleEntryDao()
    }

    @Provides
    fun provideUserSettingsDao(database: PeriodDatabase): UserSettingsDao {
        return database.userSettingsDao()
    }

    // Legacy DAOs (to be deprecated)
    @Provides
    fun providePeriodDao(database: PeriodDatabase): PeriodDao {
        return database.periodDao()
    }

    @Provides
    fun provideCycleDao(database: PeriodDatabase): CycleDao {
        return database.cycleDao()
    }
}