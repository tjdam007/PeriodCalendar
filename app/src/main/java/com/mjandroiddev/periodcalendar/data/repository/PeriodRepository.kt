package com.mjandroiddev.periodcalendar.data.repository

import com.mjandroiddev.periodcalendar.data.database.PeriodDao
import com.mjandroiddev.periodcalendar.data.database.PeriodEntity
import kotlinx.coroutines.flow.Flow
import java.time.LocalDate
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class PeriodRepository @Inject constructor(
    private val periodDao: PeriodDao
) {
    fun getAllPeriods(): Flow<List<PeriodEntity>> = periodDao.getAllPeriods()

    fun getPeriodsInRange(startDate: LocalDate, endDate: LocalDate): Flow<List<PeriodEntity>> =
        periodDao.getPeriodsInRange(startDate, endDate)

    suspend fun getPeriodById(id: Long): PeriodEntity? = periodDao.getPeriodById(id)

    suspend fun insertPeriod(period: PeriodEntity): Long = periodDao.insertPeriod(period)

    suspend fun updatePeriod(period: PeriodEntity) = periodDao.updatePeriod(period)

    suspend fun deletePeriod(period: PeriodEntity) = periodDao.deletePeriod(period)

    suspend fun getLastPeriod(): PeriodEntity? = periodDao.getLastPeriod()
}