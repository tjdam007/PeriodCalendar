package com.mjandroiddev.periodcalendar.data.database

import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Dao
interface UserSettingsDao {
    
    @Query("SELECT * FROM user_settings WHERE id = 1")
    fun getUserSettings(): Flow<UserSettings?>
    
    @Query("SELECT * FROM user_settings WHERE id = 1")
    suspend fun getUserSettingsOnce(): UserSettings?
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOrUpdateSettings(settings: UserSettings)
    
    @Update
    suspend fun updateSettings(settings: UserSettings)
    
    @Query("UPDATE user_settings SET avgCycleLength = :avgCycleLength WHERE id = 1")
    suspend fun updateAvgCycleLength(avgCycleLength: Int)
    
    @Query("UPDATE user_settings SET periodDuration = :periodDuration WHERE id = 1")
    suspend fun updatePeriodDuration(periodDuration: Int)
    
    @Query("UPDATE user_settings SET notifBeforePeriod = :days WHERE id = 1")
    suspend fun updateNotificationBeforePeriod(days: Int)
    
    @Query("UPDATE user_settings SET notifOvulation = :enabled WHERE id = 1")
    suspend fun updateOvulationNotification(enabled: Boolean)
    
    @Query("UPDATE user_settings SET notifFertileWindow = :enabled WHERE id = 1")
    suspend fun updateFertileWindowNotification(enabled: Boolean)
    
    @Query("UPDATE user_settings SET themeMode = :themeMode WHERE id = 1")
    suspend fun updateThemeMode(themeMode: String)
    
    @Query("DELETE FROM user_settings")
    suspend fun deleteAllSettings()
    
}