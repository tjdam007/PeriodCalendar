package com.mjandroiddev.periodcalendar.data.database

import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Dao
interface CycleDao {
    @Query("SELECT * FROM cycles ORDER BY startDate DESC")
    fun getAllCycles(): Flow<List<CycleEntity>>

    @Query("SELECT * FROM cycles WHERE id = :id")
    suspend fun getCycleById(id: Long): CycleEntity?

    @Insert
    suspend fun insertCycle(cycle: CycleEntity): Long

    @Update
    suspend fun updateCycle(cycle: CycleEntity)

    @Delete
    suspend fun deleteCycle(cycle: CycleEntity)

    @Query("SELECT AVG(cycleLength) FROM cycles WHERE cycleLength > 0")
    suspend fun getAverageCycleLength(): Double?

    @Query("SELECT AVG(periodLength) FROM cycles WHERE periodLength > 0")
    suspend fun getAveragePeriodLength(): Double?
}