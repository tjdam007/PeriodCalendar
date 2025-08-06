package com.mjandroiddev.periodcalendar.data.database

import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import android.content.Context

@Database(
    entities = [PeriodEntity::class, CycleEntity::class],
    version = 1,
    exportSchema = false
)
@TypeConverters(Converters::class)
abstract class PeriodDatabase : RoomDatabase() {
    abstract fun periodDao(): PeriodDao
    abstract fun cycleDao(): CycleDao

    companion object {
        const val DATABASE_NAME = "period_database"
    }
}