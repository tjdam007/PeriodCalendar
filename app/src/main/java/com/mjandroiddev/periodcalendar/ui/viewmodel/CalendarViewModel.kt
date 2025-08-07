package com.mjandroiddev.periodcalendar.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mjandroiddev.periodcalendar.data.database.CycleEntry
import com.mjandroiddev.periodcalendar.data.database.UserSettings
import com.mjandroiddev.periodcalendar.data.repository.CycleEntryRepository
import com.mjandroiddev.periodcalendar.data.repository.UserSettingsRepository
import com.mjandroiddev.periodcalendar.notifications.NotificationScheduler
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.time.LocalDate
import javax.inject.Inject

@HiltViewModel
class CalendarViewModel @Inject constructor(
    private val cycleEntryRepository: CycleEntryRepository,
    private val userSettingsRepository: UserSettingsRepository,
    private val notificationScheduler: NotificationScheduler
) : ViewModel() {

    private val _periods = MutableStateFlow<List<CycleEntry>>(emptyList())
    val periods: StateFlow<List<CycleEntry>> = _periods.asStateFlow()

    private val _userSettings = MutableStateFlow(UserSettings())
    val userSettings: StateFlow<UserSettings> = _userSettings.asStateFlow()

    init {
        loadPeriods()
        loadUserSettings()
    }

    private fun loadPeriods() {
        viewModelScope.launch {
            cycleEntryRepository.getAllEntries().collect {
                _periods.value = it
            }
        }
    }

    private fun loadUserSettings() {
        viewModelScope.launch {
            userSettingsRepository.getUserSettings().collect {
                _userSettings.value = it
            }
        }
    }

    fun addPeriod(entry: CycleEntry) {
        viewModelScope.launch {
            cycleEntryRepository.insertEntry(entry)
            
            // Reschedule notifications after adding a period entry
            if (entry.isPeriod) {
                notificationScheduler.rescheduleNotificationsAfterPeriodEntry()
            }
        }
    }

    fun updatePeriod(entry: CycleEntry) {
        viewModelScope.launch {
            cycleEntryRepository.updateEntry(entry)
            
            // Reschedule notifications if this was a period entry change
            if (entry.isPeriod) {
                notificationScheduler.rescheduleNotificationsAfterPeriodEntry()
            }
        }
    }

    fun deletePeriod(entry: CycleEntry) {
        viewModelScope.launch {
            cycleEntryRepository.deleteEntry(entry)
            
            // Reschedule notifications after deleting a period entry
            if (entry.isPeriod) {
                notificationScheduler.rescheduleNotificationsAfterPeriodEntry()
            }
        }
    }

    fun updateNotificationSettings(
        notifBeforePeriod: Int,
        notifOvulation: Boolean,
        notifFertileWindow: Boolean
    ) {
        viewModelScope.launch {
            val currentSettings = userSettingsRepository.getUserSettingsOnce()
            val updatedSettings = currentSettings.copy(
                notifBeforePeriod = notifBeforePeriod,
                notifOvulation = notifOvulation,
                notifFertileWindow = notifFertileWindow
            )
            
            userSettingsRepository.updateSettings(updatedSettings)
            notificationScheduler.rescheduleNotificationsAfterSettingsChange()
        }
    }

    // Legacy methods for backward compatibility
    @Deprecated("Use addPeriod(CycleEntry) instead")
    fun addPeriod(startDate: LocalDate, endDate: LocalDate?, flow: Int, symptoms: String, notes: String) {
        viewModelScope.launch {
            val entry = CycleEntry(
                date = startDate,
                isPeriod = true,
                flowLevel = flow.toString(),
                mood = symptoms,
                cramps = "none"
            )
            cycleEntryRepository.insertEntry(entry)
        }
    }
}