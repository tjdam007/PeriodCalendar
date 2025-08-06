package com.mjandroiddev.periodcalendar.di

import android.content.Context
import androidx.room.Room
import com.mjandroiddev.periodcalendar.data.database.CycleDao
import com.mjandroiddev.periodcalendar.data.database.PeriodDao
import com.mjandroiddev.periodcalendar.data.database.PeriodDatabase
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
    fun providePeriodDatabase(@ApplicationContext context: Context): PeriodDatabase {
        return Room.databaseBuilder(
            context,
            PeriodDatabase::class.java,
            PeriodDatabase.DATABASE_NAME
        ).build()
    }

    @Provides
    fun providePeriodDao(database: PeriodDatabase): PeriodDao {
        return database.periodDao()
    }

    @Provides
    fun provideCycleDao(database: PeriodDatabase): CycleDao {
        return database.cycleDao()
    }
}