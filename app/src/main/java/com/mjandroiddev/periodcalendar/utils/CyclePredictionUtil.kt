package com.mjandroiddev.periodcalendar.utils

import java.time.LocalDate
import java.time.temporal.ChronoUnit

/**
 * Utility object for predicting menstrual cycle dates based on medical knowledge
 * and standard cycle calculation methods.
 */
object CyclePredictionUtil {
    
    /**
     * Standard luteal phase length in days (time between ovulation and next period)
     */
    private const val LUTEAL_PHASE_DAYS = 14L
    
    /**
     * Standard fertile window length before ovulation (sperm can survive up to 5 days)
     */
    private const val FERTILE_WINDOW_BEFORE_OVULATION = 5L
    
    /**
     * Minimum valid cycle length in days
     */
    private const val MIN_CYCLE_LENGTH = 15
    
    /**
     * Maximum valid cycle length in days
     */
    private const val MAX_CYCLE_LENGTH = 45
    
    /**
     * Calculates the predicted next period start date based on the last period date
     * and average cycle length.
     *
     * @param lastPeriodDate The start date of the last menstrual period
     * @param avgCycleLength The average cycle length in days (typically 21-35 days)
     * @return The predicted start date of the next period
     * @throws IllegalArgumentException if cycle length is outside valid range (15-45 days)
     */
    fun getNextPeriodDate(lastPeriodDate: LocalDate, avgCycleLength: Int): LocalDate {
        validateCycleLength(avgCycleLength)
        return lastPeriodDate.plusDays(avgCycleLength.toLong())
    }
    
    /**
     * Calculates the predicted ovulation date based on the next period date.
     * Ovulation typically occurs 14 days before the next period (luteal phase).
     *
     * @param nextPeriodDate The predicted start date of the next period
     * @return The predicted ovulation date
     */
    fun getOvulationDate(nextPeriodDate: LocalDate): LocalDate {
        return nextPeriodDate.minusDays(LUTEAL_PHASE_DAYS)
    }
    
    /**
     * Calculates the fertile window based on the ovulation date.
     * The fertile window includes:
     * - 5 days before ovulation (sperm can survive up to 5 days)
     * - The ovulation day itself
     * This gives a 6-day fertile window total.
     *
     * @param ovulationDate The predicted ovulation date
     * @return A Pair representing the fertile window (start date, end date) inclusive
     */
    fun getFertileWindow(ovulationDate: LocalDate): Pair<LocalDate, LocalDate> {
        val fertileStart = ovulationDate.minusDays(FERTILE_WINDOW_BEFORE_OVULATION)
        val fertileEnd = ovulationDate // Fertile window ends on ovulation day
        return Pair(fertileStart, fertileEnd)
    }
    
    /**
     * Comprehensive cycle prediction that calculates all important dates at once.
     * This is more efficient when you need multiple predictions.
     *
     * @param lastPeriodDate The start date of the last menstrual period
     * @param avgCycleLength The average cycle length in days
     * @return CyclePrediction object containing all predicted dates
     */
    fun predictCycle(lastPeriodDate: LocalDate, avgCycleLength: Int): CyclePrediction {
        validateCycleLength(avgCycleLength)
        
        val nextPeriodDate = getNextPeriodDate(lastPeriodDate, avgCycleLength)
        val ovulationDate = getOvulationDate(nextPeriodDate)
        val fertileWindow = getFertileWindow(ovulationDate)
        
        return CyclePrediction(
            nextPeriodDate = nextPeriodDate,
            ovulationDate = ovulationDate,
            fertileWindowStart = fertileWindow.first,
            fertileWindowEnd = fertileWindow.second,
            daysUntilNextPeriod = ChronoUnit.DAYS.between(LocalDate.now(), nextPeriodDate).toInt(),
            daysUntilOvulation = ChronoUnit.DAYS.between(LocalDate.now(), ovulationDate).toInt()
        )
    }
    
    /**
     * Calculates multiple future cycle predictions for planning purposes.
     *
     * @param lastPeriodDate The start date of the last menstrual period
     * @param avgCycleLength The average cycle length in days
     * @param numberOfCycles Number of future cycles to predict (default: 3)
     * @return List of CyclePrediction objects for future cycles
     */
    fun predictMultipleCycles(
        lastPeriodDate: LocalDate,
        avgCycleLength: Int,
        numberOfCycles: Int = 3
    ): List<CyclePrediction> {
        validateCycleLength(avgCycleLength)
        require(numberOfCycles > 0) { "Number of cycles must be positive, got: $numberOfCycles" }
        require(numberOfCycles <= 12) { "Number of cycles should not exceed 12 for practical purposes, got: $numberOfCycles" }
        
        val predictions = mutableListOf<CyclePrediction>()
        var currentPeriodDate = lastPeriodDate
        
        repeat(numberOfCycles) {
            val prediction = predictCycle(currentPeriodDate, avgCycleLength)
            predictions.add(prediction)
            currentPeriodDate = prediction.nextPeriodDate
        }
        
        return predictions
    }
    
    /**
     * Determines if a given date falls within the fertile window of a cycle.
     *
     * @param date The date to check
     * @param lastPeriodDate The start date of the last menstrual period
     * @param avgCycleLength The average cycle length in days
     * @return True if the date falls within the fertile window, false otherwise
     */
    fun isInFertileWindow(date: LocalDate, lastPeriodDate: LocalDate, avgCycleLength: Int): Boolean {
        val prediction = predictCycle(lastPeriodDate, avgCycleLength)
        return !date.isBefore(prediction.fertileWindowStart) && !date.isAfter(prediction.fertileWindowEnd)
    }
    
    /**
     * Determines if a given date is predicted to be an ovulation day.
     *
     * @param date The date to check
     * @param lastPeriodDate The start date of the last menstrual period
     * @param avgCycleLength The average cycle length in days
     * @return True if the date is predicted to be ovulation day, false otherwise
     */
    fun isOvulationDay(date: LocalDate, lastPeriodDate: LocalDate, avgCycleLength: Int): Boolean {
        val prediction = predictCycle(lastPeriodDate, avgCycleLength)
        return date == prediction.ovulationDate
    }
    
    /**
     * Calculates the current cycle day based on the last period start date.
     *
     * @param lastPeriodDate The start date of the last menstrual period
     * @param currentDate The current date (default: today)
     * @return The cycle day number (1-based), or null if current date is before last period
     */
    fun getCurrentCycleDay(lastPeriodDate: LocalDate, currentDate: LocalDate = LocalDate.now()): Int? {
        return if (currentDate.isBefore(lastPeriodDate)) {
            null
        } else {
            ChronoUnit.DAYS.between(lastPeriodDate, currentDate).toInt() + 1
        }
    }
    
    /**
     * Validates that the cycle length is within acceptable medical ranges.
     *
     * @param cycleLength The cycle length to validate
     * @throws IllegalArgumentException if cycle length is outside valid range
     */
    private fun validateCycleLength(cycleLength: Int) {
        require(cycleLength in MIN_CYCLE_LENGTH..MAX_CYCLE_LENGTH) {
            "Cycle length must be between $MIN_CYCLE_LENGTH and $MAX_CYCLE_LENGTH days, got: $cycleLength"
        }
    }
}

/**
 * Data class representing a complete cycle prediction with all important dates.
 *
 * @property nextPeriodDate Predicted start date of the next period
 * @property ovulationDate Predicted ovulation date
 * @property fertileWindowStart Start date of the fertile window
 * @property fertileWindowEnd End date of the fertile window
 * @property daysUntilNextPeriod Number of days until the next period (from today)
 * @property daysUntilOvulation Number of days until ovulation (from today)
 */
data class CyclePrediction(
    val nextPeriodDate: LocalDate,
    val ovulationDate: LocalDate,
    val fertileWindowStart: LocalDate,
    val fertileWindowEnd: LocalDate,
    val daysUntilNextPeriod: Int,
    val daysUntilOvulation: Int
) {
    /**
     * Returns a human-readable summary of the cycle prediction.
     */
    fun getSummary(): String = buildString {
        appendLine("Next Period: $nextPeriodDate (in $daysUntilNextPeriod days)")
        appendLine("Ovulation: $ovulationDate (in $daysUntilOvulation days)")
        appendLine("Fertile Window: $fertileWindowStart to $fertileWindowEnd")
    }
    
    /**
     * Checks if the prediction dates are in the past (for validation purposes).
     */
    fun isInPast(referenceDate: LocalDate = LocalDate.now()): Boolean {
        return nextPeriodDate.isBefore(referenceDate)
    }
    
    /**
     * Returns the length of the fertile window in days.
     */
    fun getFertileWindowLength(): Int {
        return ChronoUnit.DAYS.between(fertileWindowStart, fertileWindowEnd).toInt() + 1
    }
}