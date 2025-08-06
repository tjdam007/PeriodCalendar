package com.mjandroiddev.periodcalendar.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mjandroiddev.periodcalendar.data.database.CycleEntity
import com.mjandroiddev.periodcalendar.data.repository.CycleRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class CycleViewModel @Inject constructor(
    private val cycleRepository: CycleRepository
) : ViewModel() {

    private val _cycles = MutableStateFlow<List<CycleEntity>>(emptyList())
    val cycles: StateFlow<List<CycleEntity>> = _cycles.asStateFlow()

    private val _averageCycleLength = MutableStateFlow<Double?>(null)
    val averageCycleLength: StateFlow<Double?> = _averageCycleLength.asStateFlow()

    private val _averagePeriodLength = MutableStateFlow<Double?>(null)
    val averagePeriodLength: StateFlow<Double?> = _averagePeriodLength.asStateFlow()

    init {
        loadCycles()
        loadAverages()
    }

    private fun loadCycles() {
        viewModelScope.launch {
            cycleRepository.getAllCycles().collect {
                _cycles.value = it
            }
        }
    }

    private fun loadAverages() {
        viewModelScope.launch {
            _averageCycleLength.value = cycleRepository.getAverageCycleLength()
            _averagePeriodLength.value = cycleRepository.getAveragePeriodLength()
        }
    }

    fun addCycle(cycle: CycleEntity) {
        viewModelScope.launch {
            cycleRepository.insertCycle(cycle)
            loadAverages()
        }
    }

    fun updateCycle(cycle: CycleEntity) {
        viewModelScope.launch {
            cycleRepository.updateCycle(cycle)
            loadAverages()
        }
    }

    fun deleteCycle(cycle: CycleEntity) {
        viewModelScope.launch {
            cycleRepository.deleteCycle(cycle)
            loadAverages()
        }
    }
}