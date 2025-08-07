package com.mjandroiddev.periodcalendar.utils

import com.mjandroiddev.periodcalendar.data.database.CycleEntry
import com.mjandroiddev.periodcalendar.data.database.UserSettings
import java.time.LocalDate

/**
 * Extension functions to integrate CyclePredictionUtil with database entities.
 */

/**
 * Gets the next period prediction based on user settings and the last period entry.
 */
fun UserSettings.getNextPeriodPrediction(lastPeriodDate: LocalDate): CyclePrediction {
    return CyclePredictionUtil.predictCycle(lastPeriodDate, this.avgCycleLength)
}

/**
 * Gets multiple cycle predictions based on user settings.
 */
fun UserSettings.getMultiplePredictions(
    lastPeriodDate: LocalDate,
    numberOfCycles: Int = 3
): List<CyclePrediction> {
    return CyclePredictionUtil.predictMultipleCycles(lastPeriodDate, this.avgCycleLength, numberOfCycles)
}

/**
 * Checks if a date is in the fertile window based on user settings.
 */
fun UserSettings.isDateInFertileWindow(date: LocalDate, lastPeriodDate: LocalDate): Boolean {
    return CyclePredictionUtil.isInFertileWindow(date, lastPeriodDate, this.avgCycleLength)
}

/**
 * Checks if a date is an ovulation day based on user settings.
 */
fun UserSettings.isOvulationDay(date: LocalDate, lastPeriodDate: LocalDate): Boolean {
    return CyclePredictionUtil.isOvulationDay(date, lastPeriodDate, this.avgCycleLength)
}

/**
 * Gets the current cycle day based on user settings and last period date.
 */
fun UserSettings.getCurrentCycleDay(lastPeriodDate: LocalDate, currentDate: LocalDate = LocalDate.now()): Int? {
    return CyclePredictionUtil.getCurrentCycleDay(lastPeriodDate, currentDate)
}

/**
 * Finds the most recent period start date from a list of cycle entries.
 */
fun List<CycleEntry>.getLastPeriodStartDate(): LocalDate? {
    return this
        .filter { it.isPeriod }
        .groupBy { it.date }
        .keys
        .maxOrNull()
}

/**
 * Gets all period start dates from a list of cycle entries, sorted by date.
 */
fun List<CycleEntry>.getPeriodStartDates(): List<LocalDate> {
    return this
        .filter { it.isPeriod }
        .groupBy { it.date }
        .keys
        .sorted()
}

/**
 * Calculates the actual cycle lengths from historical period data.
 * Returns a list of cycle lengths in days between consecutive periods.
 */
fun List<CycleEntry>.calculateActualCycleLengths(): List<Int> {
    val periodStartDates = this.getPeriodStartDates()
    
    if (periodStartDates.size < 2) {
        return emptyList()
    }
    
    val cycleLengths = mutableListOf<Int>()
    
    for (i in 1 until periodStartDates.size) {
        val previousDate = periodStartDates[i - 1]
        val currentDate = periodStartDates[i]
        val cycleLength = java.time.temporal.ChronoUnit.DAYS.between(previousDate, currentDate).toInt()
        cycleLengths.add(cycleLength)
    }
    
    return cycleLengths
}

/**
 * Calculates the average cycle length from historical period data.
 */
fun List<CycleEntry>.calculateAverageCycleLength(): Int? {
    val cycleLengths = this.calculateActualCycleLengths()
    
    return if (cycleLengths.isNotEmpty()) {
        (cycleLengths.sum().toDouble() / cycleLengths.size).toInt()
    } else {
        null
    }
}

/**
 * Suggests an updated average cycle length based on recent period data.
 * This can be used to update user settings automatically.
 */
fun List<CycleEntry>.suggestUpdatedCycleLength(currentAverage: Int, recentCyclesWeight: Double = 0.3): Int {
    val actualAverage = this.calculateAverageCycleLength() ?: return currentAverage
    
    // Weighted average: give more weight to recent actual data
    val updatedAverage = (currentAverage * (1 - recentCyclesWeight) + actualAverage * recentCyclesWeight)
    
    return updatedAverage.toInt().coerceIn(15, 45) // Ensure within valid range
}

/**
 * Gets predictions with enhanced accuracy using historical data.
 * Uses actual cycle length patterns to improve predictions.
 */
fun UserSettings.getEnhancedPredictions(
    cycleEntries: List<CycleEntry>,
    numberOfCycles: Int = 3
): List<CyclePrediction> {
    val lastPeriodDate = cycleEntries.getLastPeriodStartDate() ?: return emptyList()
    
    // Use actual average if we have enough data, otherwise use settings
    val effectiveCycleLength = cycleEntries.calculateAverageCycleLength() ?: this.avgCycleLength
    
    return CyclePredictionUtil.predictMultipleCycles(lastPeriodDate, effectiveCycleLength, numberOfCycles)
}

/**
 * Generates a cycle summary with predictions and statistics.
 */
data class CycleSummary(
    val lastPeriodDate: LocalDate?,
    val currentCycleDay: Int?,
    val nextPrediction: CyclePrediction?,
    val averageCycleLength: Int,
    val actualCycleLengths: List<Int>,
    val isRegular: Boolean // Based on cycle length variation
)

/**
 * Creates a comprehensive cycle summary combining user settings and historical data.
 */
fun UserSettings.createCycleSummary(cycleEntries: List<CycleEntry>): CycleSummary {
    val lastPeriodDate = cycleEntries.getLastPeriodStartDate()
    val actualCycleLengths = cycleEntries.calculateActualCycleLengths()
    val actualAverage = cycleEntries.calculateAverageCycleLength() ?: this.avgCycleLength
    val currentCycleDay = lastPeriodDate?.let { 
        CyclePredictionUtil.getCurrentCycleDay(it, LocalDate.now()) 
    }
    val nextPrediction = lastPeriodDate?.let {
        CyclePredictionUtil.predictCycle(it, actualAverage)
    }
    
    // Consider cycle regular if variation is within ±3 days
    val isRegular = if (actualCycleLengths.isNotEmpty()) {
        val variation = actualCycleLengths.maxOrNull()!! - actualCycleLengths.minOrNull()!!
        variation <= 6 // Within 6 days total variation (±3)
    } else {
        true // Assume regular if no historical data
    }
    
    return CycleSummary(
        lastPeriodDate = lastPeriodDate,
        currentCycleDay = currentCycleDay,
        nextPrediction = nextPrediction,
        averageCycleLength = actualAverage,
        actualCycleLengths = actualCycleLengths,
        isRegular = isRegular
    )
}