package com.mjandroiddev.periodcalendar.data.repository

import com.mjandroiddev.periodcalendar.data.database.CycleDao
import com.mjandroiddev.periodcalendar.data.database.CycleEntity
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class CycleRepository @Inject constructor(
    private val cycleDao: CycleDao
) {
    fun getAllCycles(): Flow<List<CycleEntity>> = cycleDao.getAllCycles()

    suspend fun getCycleById(id: Long): CycleEntity? = cycleDao.getCycleById(id)

    suspend fun insertCycle(cycle: CycleEntity): Long = cycleDao.insertCycle(cycle)

    suspend fun updateCycle(cycle: CycleEntity) = cycleDao.updateCycle(cycle)

    suspend fun deleteCycle(cycle: CycleEntity) = cycleDao.deleteCycle(cycle)

    suspend fun getAverageCycleLength(): Double? = cycleDao.getAverageCycleLength()

    suspend fun getAveragePeriodLength(): Double? = cycleDao.getAveragePeriodLength()
}