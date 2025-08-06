package com.mjandroiddev.periodcalendar.data.database

import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import android.content.Context

@Database(
    entities = [
        CycleEntry::class,
        UserSettings::class,
        // Legacy entities (to be deprecated)
        PeriodEntity::class,
        CycleEntity::class
    ],
    version = 2,
    exportSchema = false
)
@TypeConverters(Converters::class)
abstract class PeriodDatabase : RoomDatabase() {
    // New DAOs
    abstract fun cycleEntryDao(): CycleEntryDao
    abstract fun userSettingsDao(): UserSettingsDao
    
    // Legacy DAOs (to be deprecated)
    abstract fun periodDao(): PeriodDao
    abstract fun cycleDao(): CycleDao

    companion object {
        const val DATABASE_NAME = "period_database"
    }
}