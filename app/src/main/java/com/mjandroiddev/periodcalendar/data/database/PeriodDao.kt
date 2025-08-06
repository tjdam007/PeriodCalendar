package com.mjandroiddev.periodcalendar.data.database

import androidx.room.*
import kotlinx.coroutines.flow.Flow
import java.time.LocalDate

@Dao
interface PeriodDao {
    @Query("SELECT * FROM periods ORDER BY startDate DESC")
    fun getAllPeriods(): Flow<List<PeriodEntity>>

    @Query("SELECT * FROM periods WHERE startDate BETWEEN :startDate AND :endDate")
    fun getPeriodsInRange(startDate: LocalDate, endDate: LocalDate): Flow<List<PeriodEntity>>

    @Query("SELECT * FROM periods WHERE id = :id")
    suspend fun getPeriodById(id: Long): PeriodEntity?

    @Insert
    suspend fun insertPeriod(period: PeriodEntity): Long

    @Update
    suspend fun updatePeriod(period: PeriodEntity)

    @Delete
    suspend fun deletePeriod(period: PeriodEntity)

    @Query("SELECT * FROM periods ORDER BY startDate DESC LIMIT 1")
    suspend fun getLastPeriod(): PeriodEntity?
}