package com.mjandroiddev.periodcalendar.data.repository

import com.mjandroiddev.periodcalendar.data.database.CycleEntry
import com.mjandroiddev.periodcalendar.data.database.CycleEntryDao
import kotlinx.coroutines.flow.Flow
import java.time.LocalDate
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class CycleEntryRepository @Inject constructor(
    private val cycleEntryDao: CycleEntryDao
) {
    
    // Flow-based data observation
    fun getAllEntries(): Flow<List<CycleEntry>> = cycleEntryDao.getAllEntries()
    
    fun getEntriesInRange(startDate: LocalDate, endDate: LocalDate): Flow<List<CycleEntry>> =
        cycleEntryDao.getEntriesInRange(startDate, endDate)
    
    fun getPeriodEntries(): Flow<List<CycleEntry>> = cycleEntryDao.getPeriodEntries()
    
    // Single-time data access
    suspend fun getEntryByDate(date: LocalDate): CycleEntry? =
        cycleEntryDao.getEntryByDate(date)
    
    suspend fun getEntryById(id: Int): CycleEntry? =
        cycleEntryDao.getEntryById(id)
    
    suspend fun getLastPeriodEntry(): CycleEntry? =
        cycleEntryDao.getLastPeriodEntry()
    
    suspend fun getRecentPeriodEntries(fromDate: LocalDate, limit: Int = 10): List<CycleEntry> =
        cycleEntryDao.getRecentPeriodEntries(fromDate, limit)
    
    suspend fun getPeriodDaysCount(startDate: LocalDate, endDate: LocalDate): Int =
        cycleEntryDao.getPeriodDaysCount(startDate, endDate)
    
    suspend fun getAllPeriodDates(): List<LocalDate> =
        cycleEntryDao.getAllPeriodDates()
    
    // Data modification
    suspend fun insertEntry(entry: CycleEntry): Long =
        cycleEntryDao.insertEntry(entry)
    
    suspend fun insertEntries(entries: List<CycleEntry>): List<Long> =
        cycleEntryDao.insertEntries(entries)
    
    suspend fun updateEntry(entry: CycleEntry) =
        cycleEntryDao.updateEntry(entry)
    
    suspend fun deleteEntry(entry: CycleEntry) =
        cycleEntryDao.deleteEntry(entry)
    
    suspend fun deleteEntryById(id: Int) =
        cycleEntryDao.deleteEntryById(id)
    
    suspend fun deleteEntryByDate(date: LocalDate) =
        cycleEntryDao.deleteEntryByDate(date)
    
    suspend fun deleteAllEntries() =
        cycleEntryDao.deleteAllEntries()
    
    // Statistics and calculations
    suspend fun getAverageCycleLength(): Double? =
        cycleEntryDao.getAverageCycleLength()
    
    suspend fun getAveragePeriodLength(): Double? =
        cycleEntryDao.getAveragePeriodLength()
    
    // Convenience methods for common operations
    suspend fun addOrUpdateDayEntry(
        date: LocalDate,
        isPeriod: Boolean = false,
        flowLevel: String = "none",
        mood: String = "",
        cramps: String = "none"
    ): Long {
        val existingEntry = getEntryByDate(date)
        return if (existingEntry != null) {
            val updatedEntry = existingEntry.copy(
                isPeriod = isPeriod,
                flowLevel = flowLevel,
                mood = mood,
                cramps = cramps
            )
            updateEntry(updatedEntry)
            existingEntry.id.toLong()
        } else {
            val newEntry = CycleEntry(
                date = date,
                isPeriod = isPeriod,
                flowLevel = flowLevel,
                mood = mood,
                cramps = cramps
            )
            insertEntry(newEntry)
        }
    }
    
    suspend fun markPeriodDays(startDate: LocalDate, endDate: LocalDate, flowLevel: String = "medium") {
        var currentDate = startDate
        val entries = mutableListOf<CycleEntry>()
        
        while (!currentDate.isAfter(endDate)) {
            val existingEntry = getEntryByDate(currentDate)
            if (existingEntry != null) {
                entries.add(existingEntry.copy(isPeriod = true, flowLevel = flowLevel))
            } else {
                entries.add(
                    CycleEntry(
                        date = currentDate,
                        isPeriod = true,
                        flowLevel = flowLevel,
                        mood = "",
                        cramps = "none"
                    )
                )
            }
            currentDate = currentDate.plusDays(1)
        }
        
        insertEntries(entries)
    }
    
    suspend fun updateMoodForDate(date: LocalDate, mood: String) {
        val entry = getEntryByDate(date)
        if (entry != null) {
            updateEntry(entry.copy(mood = mood))
        } else {
            insertEntry(
                CycleEntry(
                    date = date,
                    isPeriod = false,
                    flowLevel = "none",
                    mood = mood,
                    cramps = "none"
                )
            )
        }
    }
    
    suspend fun updateCrampsForDate(date: LocalDate, cramps: String) {
        val entry = getEntryByDate(date)
        if (entry != null) {
            updateEntry(entry.copy(cramps = cramps))
        } else {
            insertEntry(
                CycleEntry(
                    date = date,
                    isPeriod = false,
                    flowLevel = "none",
                    mood = "",
                    cramps = cramps
                )
            )
        }
    }
}