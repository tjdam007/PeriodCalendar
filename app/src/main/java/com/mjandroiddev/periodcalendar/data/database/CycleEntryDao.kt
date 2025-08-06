package com.mjandroiddev.periodcalendar.data.database

import androidx.room.*
import kotlinx.coroutines.flow.Flow
import java.time.LocalDate

@Dao
interface CycleEntryDao {
    
    @Query("SELECT * FROM cycle_entries ORDER BY date DESC")
    fun getAllEntries(): Flow<List<CycleEntry>>
    
    @Query("SELECT * FROM cycle_entries WHERE date BETWEEN :startDate AND :endDate ORDER BY date ASC")
    fun getEntriesInRange(startDate: LocalDate, endDate: LocalDate): Flow<List<CycleEntry>>
    
    @Query("SELECT * FROM cycle_entries WHERE date = :date")
    suspend fun getEntryByDate(date: LocalDate): CycleEntry?
    
    @Query("SELECT * FROM cycle_entries WHERE id = :id")
    suspend fun getEntryById(id: Int): CycleEntry?
    
    @Query("SELECT * FROM cycle_entries WHERE isPeriod = 1 ORDER BY date DESC")
    fun getPeriodEntries(): Flow<List<CycleEntry>>
    
    @Query("SELECT * FROM cycle_entries WHERE isPeriod = 1 ORDER BY date DESC LIMIT 1")
    suspend fun getLastPeriodEntry(): CycleEntry?
    
    @Query("""
        SELECT * FROM cycle_entries 
        WHERE isPeriod = 1 AND date >= :fromDate 
        ORDER BY date DESC 
        LIMIT :limit
    """)
    suspend fun getRecentPeriodEntries(fromDate: LocalDate, limit: Int = 10): List<CycleEntry>
    
    @Query("SELECT COUNT(*) FROM cycle_entries WHERE date BETWEEN :startDate AND :endDate AND isPeriod = 1")
    suspend fun getPeriodDaysCount(startDate: LocalDate, endDate: LocalDate): Int
    
    @Query("SELECT DISTINCT date FROM cycle_entries WHERE isPeriod = 1 ORDER BY date DESC")
    suspend fun getAllPeriodDates(): List<LocalDate>
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertEntry(entry: CycleEntry): Long
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertEntries(entries: List<CycleEntry>): List<Long>
    
    @Update
    suspend fun updateEntry(entry: CycleEntry)
    
    @Delete
    suspend fun deleteEntry(entry: CycleEntry)
    
    @Query("DELETE FROM cycle_entries WHERE id = :id")
    suspend fun deleteEntryById(id: Int)
    
    @Query("DELETE FROM cycle_entries WHERE date = :date")
    suspend fun deleteEntryByDate(date: LocalDate)
    
    @Query("DELETE FROM cycle_entries")
    suspend fun deleteAllEntries()
    
    // Statistics queries
    @Query("""
        SELECT AVG(cycle_length) FROM (
            SELECT JULIANDAY(next_period.date) - JULIANDAY(current_period.date) as cycle_length
            FROM (
                SELECT date, ROW_NUMBER() OVER (ORDER BY date) as rn 
                FROM cycle_entries 
                WHERE isPeriod = 1 
                GROUP BY date
            ) current_period
            JOIN (
                SELECT date, ROW_NUMBER() OVER (ORDER BY date) as rn 
                FROM cycle_entries 
                WHERE isPeriod = 1 
                GROUP BY date
            ) next_period ON current_period.rn + 1 = next_period.rn
        )
    """)
    suspend fun getAverageCycleLength(): Double?
    
    @Query("""
        SELECT AVG(period_length) FROM (
            SELECT COUNT(*) as period_length
            FROM cycle_entries
            WHERE isPeriod = 1
            GROUP BY (
                SELECT COUNT(*) 
                FROM cycle_entries e2 
                WHERE e2.isPeriod = 1 AND e2.date <= cycle_entries.date
            ) / 10
        )
    """)
    suspend fun getAveragePeriodLength(): Double?
}