package com.mjandroiddev.periodcalendar.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mjandroiddev.periodcalendar.data.database.CycleEntry
import com.mjandroiddev.periodcalendar.data.model.CrampLevel
import com.mjandroiddev.periodcalendar.data.model.FlowLevel
import com.mjandroiddev.periodcalendar.data.model.MoodType
import com.mjandroiddev.periodcalendar.data.repository.CycleEntryRepository
import com.mjandroiddev.periodcalendar.firebase.AnalyticsLogger
import com.mjandroiddev.periodcalendar.utils.CyclePredictionUtil
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.time.LocalDate
import javax.inject.Inject
import kotlin.math.abs

@HiltViewModel
class LogEntryViewModel @Inject constructor(
    private val cycleEntryRepository: CycleEntryRepository,
    private val analyticsLogger: AnalyticsLogger
) : ViewModel() {

    private val _selectedDate = MutableStateFlow(LocalDate.now())
    val selectedDate: StateFlow<LocalDate> = _selectedDate.asStateFlow()

    private val _existingEntry = MutableStateFlow<CycleEntry?>(null)
    val existingEntry: StateFlow<CycleEntry?> = _existingEntry.asStateFlow()

    private val _isPeriod = MutableStateFlow(false)
    val isPeriod: StateFlow<Boolean> = _isPeriod.asStateFlow()

    private val _flowLevel = MutableStateFlow(FlowLevel.LIGHT)
    val flowLevel: StateFlow<FlowLevel> = _flowLevel.asStateFlow()

    private val _mood = MutableStateFlow<MoodType?>(null)
    val mood: StateFlow<MoodType?> = _mood.asStateFlow()

    private val _cramps = MutableStateFlow(CrampLevel.NONE)
    val cramps: StateFlow<CrampLevel> = _cramps.asStateFlow()

    private val _isSaving = MutableStateFlow(false)
    val isSaving: StateFlow<Boolean> = _isSaving.asStateFlow()

    private val _saveResult = MutableStateFlow<SaveResult?>(null)
    val saveResult: StateFlow<SaveResult?> = _saveResult.asStateFlow()

    fun setSelectedDate(date: LocalDate) {
        _selectedDate.value = date
        loadExistingEntry(date)
        
        // Track calendar day view
        analyticsLogger.trackViewCalendarDay(
            dayType = AnalyticsLogger.DAY_TYPE_NORMAL, // Will be updated based on entry data
            date = date.toString()
        )
        
        // Set current screen for crash context
        analyticsLogger.setCurrentScreen("log_entry")
    }

    private fun loadExistingEntry(date: LocalDate) {
        viewModelScope.launch {
            try {
                val entry = cycleEntryRepository.getEntryByDate(date)
                _existingEntry.value = entry
                
                if (entry != null) {
                    _isPeriod.value = entry.isPeriod
                    _flowLevel.value = entry.flowLevel ?: FlowLevel.LIGHT
                    _mood.value = entry.mood
                    _cramps.value = entry.cramps ?: CrampLevel.NONE
                    
                    // Update the day type for analytics based on existing entry
                    val dayType = when {
                        entry.isPeriod -> AnalyticsLogger.DAY_TYPE_PERIOD
                        else -> {
                            // Check if it's in fertile window or ovulation day
                            // This would require additional logic based on cycle data
                            AnalyticsLogger.DAY_TYPE_NORMAL
                        }
                    }
                    analyticsLogger.trackViewCalendarDay(dayType, date.toString())
                }
            } catch (e: Exception) {
                analyticsLogger.logError(e, "Failed to load existing entry for date: $date")
            }
        }
    }

    fun updatePeriodStatus(isPeriod: Boolean) {
        _isPeriod.value = isPeriod
    }

    fun updateFlowLevel(flowLevel: FlowLevel) {
        _flowLevel.value = flowLevel
    }

    fun updateMood(mood: MoodType?) {
        _mood.value = mood
    }

    fun updateCramps(cramps: CrampLevel) {
        _cramps.value = cramps
    }

    fun saveEntry() {
        viewModelScope.launch {
            _isSaving.value = true
            try {
                val entry = CycleEntry(
                    id = _existingEntry.value?.id ?: 0,
                    date = _selectedDate.value,
                    isPeriod = _isPeriod.value,
                    flowLevel = if (_isPeriod.value) _flowLevel.value else null,
                    mood = _mood.value,
                    cramps = _cramps.value.takeIf { it != CrampLevel.NONE }
                )

                // Save to database
                val savedEntry = if (_existingEntry.value != null) {
                    cycleEntryRepository.updateEntry(entry)
                    entry
                } else {
                    val newId = cycleEntryRepository.insertEntry(entry)
                    entry.copy(id = newId.toInt())
                }

                // Track analytics
                trackEntryAnalytics(savedEntry)

                _existingEntry.value = savedEntry
                _saveResult.value = SaveResult.Success
                
                // Clear result after delay
                kotlinx.coroutines.delay(2000)
                _saveResult.value = null
                
            } catch (e: Exception) {
                analyticsLogger.logError(e, "Failed to save cycle entry")
                _saveResult.value = SaveResult.Error("Failed to save entry")
                
                // Clear result after delay
                kotlinx.coroutines.delay(3000)
                _saveResult.value = null
            } finally {
                _isSaving.value = false
            }
        }
    }

    private fun trackEntryAnalytics(entry: CycleEntry) {
        try {
            if (entry.isPeriod) {
                // Track period logged
                val symptoms = mutableListOf<String>()
                entry.mood?.let { symptoms.add(it.name.lowercase()) }
                entry.cramps?.let { if (it != CrampLevel.NONE) symptoms.add("cramps_${it.name.lowercase()}") }
                
                analyticsLogger.trackPeriodLogged(
                    date = entry.date.toString(),
                    flowLevel = entry.flowLevel?.name?.lowercase() ?: "unknown",
                    symptomsSelected = symptoms
                )
            }

            // Track individual symptoms
            entry.mood?.let { mood ->
                analyticsLogger.trackSymptomLogged(
                    symptomType = "mood",
                    intensity = mood.name.lowercase(),
                    cycleDay = calculateCycleDay(entry.date)
                )
            }

            entry.cramps?.let { cramps ->
                if (cramps != CrampLevel.NONE) {
                    analyticsLogger.trackSymptomLogged(
                        symptomType = "cramps",
                        intensity = cramps.name.lowercase(),
                        cycleDay = calculateCycleDay(entry.date)
                    )
                }
            }
        } catch (e: Exception) {
            // Don't crash if analytics fails
            analyticsLogger.logError(e, "Error tracking entry analytics")
        }
    }

    private fun calculateCycleDay(date: LocalDate): Int {
        // This is a simplified calculation - in a real app you'd want to
        // calculate based on the user's last period start date
        return try {
            val today = LocalDate.now()
            val daysDiff = java.time.temporal.ChronoUnit.DAYS.between(date, today).toInt()
            abs(daysDiff) + 1
        } catch (e: Exception) {
            1 // Default to day 1 if calculation fails
        }
    }

    fun clearSaveResult() {
        _saveResult.value = null
    }

    sealed class SaveResult {
        object Success : SaveResult()
        data class Error(val message: String) : SaveResult()
    }
}