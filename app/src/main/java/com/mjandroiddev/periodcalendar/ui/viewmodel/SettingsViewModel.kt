package com.mjandroiddev.periodcalendar.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mjandroiddev.periodcalendar.data.database.UserSettings
import com.mjandroiddev.periodcalendar.data.model.ThemeMode
import com.mjandroiddev.periodcalendar.data.repository.UserSettingsRepository
import com.mjandroiddev.periodcalendar.firebase.AnalyticsLogger
import com.mjandroiddev.periodcalendar.notifications.NotificationScheduler
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SettingsViewModel @Inject constructor(
    private val userSettingsRepository: UserSettingsRepository,
    private val notificationScheduler: NotificationScheduler,
    private val analyticsLogger: AnalyticsLogger
) : ViewModel() {

    private val _userSettings = MutableStateFlow(UserSettings())
    val userSettings: StateFlow<UserSettings> = _userSettings.asStateFlow()
    
    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()
    
    private val _saveMessage = MutableStateFlow<String?>(null)
    val saveMessage: StateFlow<String?> = _saveMessage.asStateFlow()

    init {
        loadSettings()
    }

    private fun loadSettings() {
        viewModelScope.launch {
            _isLoading.value = true
            var isFirstEmission = true
            try {
                userSettingsRepository.getUserSettings().collect { settings ->
                    _userSettings.value = settings
                    if (isFirstEmission) {
                        _isLoading.value = false
                        isFirstEmission = false
                    }
                }
            } catch (e: Exception) {
                _isLoading.value = false
            }
        }
    }

    fun updateCycleLength(avgCycleLength: Int) {
        if (avgCycleLength in 15..45) {
            val currentSettings = _userSettings.value
            val updatedSettings = currentSettings.copy(avgCycleLength = avgCycleLength)
            updateSettings(updatedSettings)
        }
    }

    fun updatePeriodDuration(periodDuration: Int) {
        if (periodDuration in 1..10) {
            val currentSettings = _userSettings.value
            val updatedSettings = currentSettings.copy(periodDuration = periodDuration)
            updateSettings(updatedSettings)
        }
    }

    fun updateNotificationDays(days: Int) {
        if (days in 0..7) {
            val currentSettings = _userSettings.value
            val updatedSettings = currentSettings.copy(notifBeforePeriod = days)
            updateSettings(updatedSettings, shouldRescheduleNotifications = true)
        }
    }

    fun updateOvulationNotification(enabled: Boolean) {
        val currentSettings = _userSettings.value
        val updatedSettings = currentSettings.copy(notifOvulation = enabled)
        updateSettings(updatedSettings, shouldRescheduleNotifications = true)
        
        // Track notification setting change
        analyticsLogger.trackNotificationSettingChanged("ovulation", enabled)
    }

    fun updateFertileWindowNotification(enabled: Boolean) {
        val currentSettings = _userSettings.value
        val updatedSettings = currentSettings.copy(notifFertileWindow = enabled)
        updateSettings(updatedSettings, shouldRescheduleNotifications = true)
        
        // Track notification setting change
        analyticsLogger.trackNotificationSettingChanged("fertile_window", enabled)
    }

    fun updateThemeMode(themeMode: ThemeMode) {
        val currentSettings = _userSettings.value
        val updatedSettings = currentSettings.copy(themeMode = themeMode.value)
        updateSettings(updatedSettings)
        
        // Track theme change
        val themeValue = when (themeMode) {
            ThemeMode.LIGHT -> AnalyticsLogger.THEME_LIGHT
            ThemeMode.DARK -> AnalyticsLogger.THEME_DARK
            ThemeMode.SYSTEM -> AnalyticsLogger.THEME_SYSTEM
        }
        analyticsLogger.trackThemeChanged(themeValue)
    }

    private fun updateSettings(
        settings: UserSettings,
        shouldRescheduleNotifications: Boolean = false
    ) {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                userSettingsRepository.updateSettings(settings)
                _userSettings.value = settings
                
                if (shouldRescheduleNotifications) {
                    notificationScheduler.rescheduleNotificationsAfterSettingsChange()
                }
                
                // Update Crashlytics context with user preferences
                analyticsLogger.setUserPreferences(
                    themeMode = settings.themeMode,
                    notificationsEnabled = settings.notifBeforePeriod > 0 || settings.notifOvulation || settings.notifFertileWindow,
                    avgCycleLength = settings.avgCycleLength
                )
                
                _saveMessage.value = "Settings saved"
                
                // Clear the message after a delay
                kotlinx.coroutines.delay(2000)
                _saveMessage.value = null
                
            } catch (e: Exception) {
                _saveMessage.value = "Error saving settings"
                kotlinx.coroutines.delay(3000)
                _saveMessage.value = null
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun resetToDefaults() {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                val defaultSettings = UserSettings()
                userSettingsRepository.resetToDefaults()
                _userSettings.value = defaultSettings
                notificationScheduler.rescheduleNotificationsAfterSettingsChange()
                _saveMessage.value = "Reset to defaults"
                
                kotlinx.coroutines.delay(2000)
                _saveMessage.value = null
            } catch (e: Exception) {
                _saveMessage.value = "Error resetting settings"
                kotlinx.coroutines.delay(3000)
                _saveMessage.value = null
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun clearSaveMessage() {
        _saveMessage.value = null
    }
}