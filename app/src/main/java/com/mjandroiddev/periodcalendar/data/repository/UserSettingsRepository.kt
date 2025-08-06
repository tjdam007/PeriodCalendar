package com.mjandroiddev.periodcalendar.data.repository

import com.mjandroiddev.periodcalendar.data.database.UserSettings
import com.mjandroiddev.periodcalendar.data.database.UserSettingsDao
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class UserSettingsRepository @Inject constructor(
    private val userSettingsDao: UserSettingsDao
) {
    
    // Flow-based settings observation
    fun getUserSettings(): Flow<UserSettings> = 
        userSettingsDao.getUserSettings().map { settings ->
            settings ?: UserSettings() // Return default if null
        }
    
    // Single-time settings access
    suspend fun getUserSettingsOnce(): UserSettings =
        userSettingsDao.getUserSettingsOnce() ?: UserSettings()
    
    suspend fun getOrCreateSettings(): UserSettings {
        return userSettingsDao.getUserSettingsOnce() ?: run {
            val defaultSettings = UserSettings()
            userSettingsDao.insertOrUpdateSettings(defaultSettings)
            defaultSettings
        }
    }
    
    // Full settings update
    suspend fun updateSettings(settings: UserSettings) =
        userSettingsDao.insertOrUpdateSettings(settings)
    
    suspend fun insertOrUpdateSettings(settings: UserSettings) =
        userSettingsDao.insertOrUpdateSettings(settings)
    
    // Individual setting updates
    suspend fun updateAvgCycleLength(avgCycleLength: Int) {
        ensureSettingsExist()
        userSettingsDao.updateAvgCycleLength(avgCycleLength)
    }
    
    suspend fun updatePeriodDuration(periodDuration: Int) {
        ensureSettingsExist()
        userSettingsDao.updatePeriodDuration(periodDuration)
    }
    
    suspend fun updateNotificationBeforePeriod(days: Int) {
        ensureSettingsExist()
        userSettingsDao.updateNotificationBeforePeriod(days)
    }
    
    suspend fun updateOvulationNotification(enabled: Boolean) {
        ensureSettingsExist()
        userSettingsDao.updateOvulationNotification(enabled)
    }
    
    suspend fun updateFertileWindowNotification(enabled: Boolean) {
        ensureSettingsExist()
        userSettingsDao.updateFertileWindowNotification(enabled)
    }
    
    suspend fun updateThemeMode(themeMode: String) {
        ensureSettingsExist()
        userSettingsDao.updateThemeMode(themeMode)
    }
    
    // Cleanup
    suspend fun deleteAllSettings() = userSettingsDao.deleteAllSettings()
    
    // Helper method to ensure settings exist before updating individual fields
    private suspend fun ensureSettingsExist() {
        if (userSettingsDao.getUserSettingsOnce() == null) {
            userSettingsDao.insertOrUpdateSettings(UserSettings())
        }
    }
    
    // Convenience methods for common operations
    suspend fun resetToDefaults() {
        userSettingsDao.insertOrUpdateSettings(UserSettings())
    }
    
    suspend fun isFirstLaunch(): Boolean {
        return userSettingsDao.getUserSettingsOnce() == null
    }
    
    suspend fun setupInitialSettings(
        avgCycleLength: Int = 28,
        periodDuration: Int = 5,
        notifBeforePeriod: Int = 1,
        notifOvulation: Boolean = true,
        notifFertileWindow: Boolean = true,
        themeMode: String = "system"
    ) {
        val settings = UserSettings(
            avgCycleLength = avgCycleLength,
            periodDuration = periodDuration,
            notifBeforePeriod = notifBeforePeriod,
            notifOvulation = notifOvulation,
            notifFertileWindow = notifFertileWindow,
            themeMode = themeMode
        )
        userSettingsDao.insertOrUpdateSettings(settings)
    }
    
    // Theme-related convenience methods
    suspend fun isDarkModeEnabled(): Boolean {
        val settings = getUserSettingsOnce()
        return settings.themeMode == "dark"
    }
    
    suspend fun isSystemThemeEnabled(): Boolean {
        val settings = getUserSettingsOnce()
        return settings.themeMode == "system"
    }
    
    // Notification-related convenience methods
    suspend fun areNotificationsEnabled(): Boolean {
        val settings = getUserSettingsOnce()
        return settings.notifOvulation || settings.notifFertileWindow || settings.notifBeforePeriod > 0
    }
    
    suspend fun getNotificationSettings(): Triple<Boolean, Boolean, Int> {
        val settings = getUserSettingsOnce()
        return Triple(
            settings.notifOvulation,
            settings.notifFertileWindow,
            settings.notifBeforePeriod
        )
    }
    
    // Cycle-related convenience methods
    suspend fun getCycleSettings(): Pair<Int, Int> {
        val settings = getUserSettingsOnce()
        return Pair(settings.avgCycleLength, settings.periodDuration)
    }
}