package com.mjandroiddev.periodcalendar.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mjandroiddev.periodcalendar.data.database.PeriodEntity
import com.mjandroiddev.periodcalendar.data.repository.PeriodRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.time.LocalDate
import javax.inject.Inject

@HiltViewModel
class CalendarViewModel @Inject constructor(
    private val periodRepository: PeriodRepository
) : ViewModel() {

    private val _periods = MutableStateFlow<List<PeriodEntity>>(emptyList())
    val periods: StateFlow<List<PeriodEntity>> = _periods.asStateFlow()

    private val _selectedDate = MutableStateFlow(LocalDate.now())
    val selectedDate: StateFlow<LocalDate> = _selectedDate.asStateFlow()

    init {
        loadPeriods()
    }

    private fun loadPeriods() {
        viewModelScope.launch {
            periodRepository.getAllPeriods().collect {
                _periods.value = it
            }
        }
    }

    fun selectDate(date: LocalDate) {
        _selectedDate.value = date
    }

    fun addPeriod(startDate: LocalDate, endDate: LocalDate?, flow: Int, symptoms: String, notes: String) {
        viewModelScope.launch {
            val period = PeriodEntity(
                startDate = startDate,
                endDate = endDate,
                flow = flow,
                symptoms = symptoms,
                notes = notes
            )
            periodRepository.insertPeriod(period)
        }
    }

    fun updatePeriod(period: PeriodEntity) {
        viewModelScope.launch {
            periodRepository.updatePeriod(period)
        }
    }

    fun deletePeriod(period: PeriodEntity) {
        viewModelScope.launch {
            periodRepository.deletePeriod(period)
        }
    }
}