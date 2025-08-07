package com.mjandroiddev.periodcalendar.utils

import org.junit.Assert.*
import org.junit.Test
import java.time.LocalDate
import java.time.temporal.ChronoUnit

/**
 * Comprehensive unit tests for CyclePredictionUtil covering various cycle scenarios.
 */
class CyclePredictionUtilTest {

    companion object {
        // Test reference date: January 1, 2024 (Monday)
        private val REFERENCE_DATE = LocalDate.of(2024, 1, 1)
    }

    // ===== BASIC FUNCTION TESTS =====

    @Test
    fun `getNextPeriodDate calculates correctly for regular 28-day cycle`() {
        val lastPeriodDate = REFERENCE_DATE
        val avgCycleLength = 28
        val expected = REFERENCE_DATE.plusDays(28) // January 29, 2024

        val result = CyclePredictionUtil.getNextPeriodDate(lastPeriodDate, avgCycleLength)

        assertEquals(expected, result)
    }

    @Test
    fun `getOvulationDate calculates 14 days before next period`() {
        val nextPeriodDate = LocalDate.of(2024, 1, 29) // January 29
        val expected = LocalDate.of(2024, 1, 15) // January 15 (14 days before)

        val result = CyclePredictionUtil.getOvulationDate(nextPeriodDate)

        assertEquals(expected, result)
    }

    @Test
    fun `getFertileWindow calculates 6-day window ending on ovulation`() {
        val ovulationDate = LocalDate.of(2024, 1, 15) // January 15
        val expectedStart = LocalDate.of(2024, 1, 10) // January 10 (5 days before)
        val expectedEnd = LocalDate.of(2024, 1, 15) // January 15 (ovulation day)

        val result = CyclePredictionUtil.getFertileWindow(ovulationDate)

        assertEquals(expectedStart, result.first)
        assertEquals(expectedEnd, result.second)
    }

    // ===== REGULAR CYCLE TESTS (21-35 DAYS) =====

    @Test
    fun `regular 21-day cycle predictions are accurate`() {
        val lastPeriodDate = REFERENCE_DATE
        val cycleLength = 21

        val nextPeriod = CyclePredictionUtil.getNextPeriodDate(lastPeriodDate, cycleLength)
        val ovulation = CyclePredictionUtil.getOvulationDate(nextPeriod)
        val fertileWindow = CyclePredictionUtil.getFertileWindow(ovulation)

        // Next period: January 22 (1 + 21)
        assertEquals(LocalDate.of(2024, 1, 22), nextPeriod)
        // Ovulation: January 8 (22 - 14)
        assertEquals(LocalDate.of(2024, 1, 8), ovulation)
        // Fertile window: January 3 to January 8
        assertEquals(LocalDate.of(2024, 1, 3), fertileWindow.first)
        assertEquals(LocalDate.of(2024, 1, 8), fertileWindow.second)
    }

    @Test
    fun `regular 30-day cycle predictions are accurate`() {
        val lastPeriodDate = REFERENCE_DATE
        val cycleLength = 30

        val nextPeriod = CyclePredictionUtil.getNextPeriodDate(lastPeriodDate, cycleLength)
        val ovulation = CyclePredictionUtil.getOvulationDate(nextPeriod)
        val fertileWindow = CyclePredictionUtil.getFertileWindow(ovulation)

        // Next period: January 31 (1 + 30)
        assertEquals(LocalDate.of(2024, 1, 31), nextPeriod)
        // Ovulation: January 17 (31 - 14)
        assertEquals(LocalDate.of(2024, 1, 17), ovulation)
        // Fertile window: January 12 to January 17
        assertEquals(LocalDate.of(2024, 1, 12), fertileWindow.first)
        assertEquals(LocalDate.of(2024, 1, 17), fertileWindow.second)
    }

    @Test
    fun `regular 35-day cycle predictions are accurate`() {
        val lastPeriodDate = REFERENCE_DATE
        val cycleLength = 35

        val nextPeriod = CyclePredictionUtil.getNextPeriodDate(lastPeriodDate, cycleLength)
        val ovulation = CyclePredictionUtil.getOvulationDate(nextPeriod)
        val fertileWindow = CyclePredictionUtil.getFertileWindow(ovulation)

        // Next period: February 5 (1 + 35)
        assertEquals(LocalDate.of(2024, 2, 5), nextPeriod)
        // Ovulation: January 22 (Feb 5 - 14)
        assertEquals(LocalDate.of(2024, 1, 22), ovulation)
        // Fertile window: January 17 to January 22
        assertEquals(LocalDate.of(2024, 1, 17), fertileWindow.first)
        assertEquals(LocalDate.of(2024, 1, 22), fertileWindow.second)
    }

    // ===== SHORT CYCLE TESTS (15-20 DAYS) =====

    @Test
    fun `short 15-day cycle predictions are accurate`() {
        val lastPeriodDate = REFERENCE_DATE
        val cycleLength = 15

        val nextPeriod = CyclePredictionUtil.getNextPeriodDate(lastPeriodDate, cycleLength)
        val ovulation = CyclePredictionUtil.getOvulationDate(nextPeriod)
        val fertileWindow = CyclePredictionUtil.getFertileWindow(ovulation)

        // Next period: January 16 (1 + 15)
        assertEquals(LocalDate.of(2024, 1, 16), nextPeriod)
        // Ovulation: January 2 (16 - 14)
        assertEquals(LocalDate.of(2024, 1, 2), ovulation)
        // Fertile window: December 28, 2023 to January 2, 2024
        assertEquals(LocalDate.of(2023, 12, 28), fertileWindow.first)
        assertEquals(LocalDate.of(2024, 1, 2), fertileWindow.second)
    }

    @Test
    fun `short 18-day cycle predictions are accurate`() {
        val lastPeriodDate = REFERENCE_DATE
        val cycleLength = 18

        val nextPeriod = CyclePredictionUtil.getNextPeriodDate(lastPeriodDate, cycleLength)
        val ovulation = CyclePredictionUtil.getOvulationDate(nextPeriod)
        val fertileWindow = CyclePredictionUtil.getFertileWindow(ovulation)

        // Next period: January 19 (1 + 18)
        assertEquals(LocalDate.of(2024, 1, 19), nextPeriod)
        // Ovulation: January 5 (19 - 14)
        assertEquals(LocalDate.of(2024, 1, 5), ovulation)
        // Fertile window: December 31, 2023 to January 5, 2024
        assertEquals(LocalDate.of(2023, 12, 31), fertileWindow.first)
        assertEquals(LocalDate.of(2024, 1, 5), fertileWindow.second)
    }

    // ===== LONG CYCLE TESTS (36-45 DAYS) =====

    @Test
    fun `long 40-day cycle predictions are accurate`() {
        val lastPeriodDate = REFERENCE_DATE
        val cycleLength = 40

        val nextPeriod = CyclePredictionUtil.getNextPeriodDate(lastPeriodDate, cycleLength)
        val ovulation = CyclePredictionUtil.getOvulationDate(nextPeriod)
        val fertileWindow = CyclePredictionUtil.getFertileWindow(ovulation)

        // Next period: February 10 (1 + 40)
        assertEquals(LocalDate.of(2024, 2, 10), nextPeriod)
        // Ovulation: January 27 (Feb 10 - 14)
        assertEquals(LocalDate.of(2024, 1, 27), ovulation)
        // Fertile window: January 22 to January 27
        assertEquals(LocalDate.of(2024, 1, 22), fertileWindow.first)
        assertEquals(LocalDate.of(2024, 1, 27), fertileWindow.second)
    }

    @Test
    fun `long 45-day cycle predictions are accurate`() {
        val lastPeriodDate = REFERENCE_DATE
        val cycleLength = 45

        val nextPeriod = CyclePredictionUtil.getNextPeriodDate(lastPeriodDate, cycleLength)
        val ovulation = CyclePredictionUtil.getOvulationDate(nextPeriod)
        val fertileWindow = CyclePredictionUtil.getFertileWindow(ovulation)

        // Next period: February 15 (1 + 45)
        assertEquals(LocalDate.of(2024, 2, 15), nextPeriod)
        // Ovulation: February 1 (Feb 15 - 14)
        assertEquals(LocalDate.of(2024, 2, 1), ovulation)
        // Fertile window: January 27 to February 1
        assertEquals(LocalDate.of(2024, 1, 27), fertileWindow.first)
        assertEquals(LocalDate.of(2024, 2, 1), fertileWindow.second)
    }

    // ===== IRREGULAR CYCLE TESTS =====

    @Test
    fun `irregular cycles with varying lengths work correctly`() {
        val lastPeriodDate = REFERENCE_DATE
        val irregularLengths = listOf(25, 32, 28, 35, 30, 26)

        irregularLengths.forEach { cycleLength ->
            val nextPeriod = CyclePredictionUtil.getNextPeriodDate(lastPeriodDate, cycleLength)
            val ovulation = CyclePredictionUtil.getOvulationDate(nextPeriod)
            val fertileWindow = CyclePredictionUtil.getFertileWindow(ovulation)

            // Verify the basic relationship holds
            assertEquals(cycleLength.toLong(), ChronoUnit.DAYS.between(lastPeriodDate, nextPeriod))
            assertEquals(14L, ChronoUnit.DAYS.between(ovulation, nextPeriod))
            assertEquals(5L, ChronoUnit.DAYS.between(fertileWindow.first, fertileWindow.second))
        }
    }

    // ===== COMPREHENSIVE PREDICTION TESTS =====

    @Test
    fun `predictCycle returns complete cycle information`() {
        val lastPeriodDate = REFERENCE_DATE
        val cycleLength = 28

        val prediction = CyclePredictionUtil.predictCycle(lastPeriodDate, cycleLength)

        assertEquals(LocalDate.of(2024, 1, 29), prediction.nextPeriodDate)
        assertEquals(LocalDate.of(2024, 1, 15), prediction.ovulationDate)
        assertEquals(LocalDate.of(2024, 1, 10), prediction.fertileWindowStart)
        assertEquals(LocalDate.of(2024, 1, 15), prediction.fertileWindowEnd)

        // Days until calculations depend on current date, so we'll test the structure
        assertNotNull(prediction.daysUntilNextPeriod)
        assertNotNull(prediction.daysUntilOvulation)
    }

    @Test
    fun `predictMultipleCycles returns correct number of predictions`() {
        val lastPeriodDate = REFERENCE_DATE
        val cycleLength = 28
        val numberOfCycles = 3

        val predictions = CyclePredictionUtil.predictMultipleCycles(lastPeriodDate, cycleLength, numberOfCycles)

        assertEquals(numberOfCycles, predictions.size)

        // Verify each subsequent prediction is one cycle length later
        predictions.forEachIndexed { index, prediction ->
            val expectedDate = lastPeriodDate.plusDays((cycleLength * (index + 1)).toLong())
            assertEquals(expectedDate, prediction.nextPeriodDate)
        }
    }

    // ===== UTILITY FUNCTION TESTS =====

    @Test
    fun `isInFertileWindow correctly identifies fertile days`() {
        val lastPeriodDate = REFERENCE_DATE
        val cycleLength = 28

        // Fertile window: January 10-15
        assertTrue(CyclePredictionUtil.isInFertileWindow(LocalDate.of(2024, 1, 10), lastPeriodDate, cycleLength))
        assertTrue(CyclePredictionUtil.isInFertileWindow(LocalDate.of(2024, 1, 12), lastPeriodDate, cycleLength))
        assertTrue(CyclePredictionUtil.isInFertileWindow(LocalDate.of(2024, 1, 15), lastPeriodDate, cycleLength))

        // Outside fertile window
        assertFalse(CyclePredictionUtil.isInFertileWindow(LocalDate.of(2024, 1, 9), lastPeriodDate, cycleLength))
        assertFalse(CyclePredictionUtil.isInFertileWindow(LocalDate.of(2024, 1, 16), lastPeriodDate, cycleLength))
    }

    @Test
    fun `isOvulationDay correctly identifies ovulation day`() {
        val lastPeriodDate = REFERENCE_DATE
        val cycleLength = 28

        // Ovulation day: January 15
        assertTrue(CyclePredictionUtil.isOvulationDay(LocalDate.of(2024, 1, 15), lastPeriodDate, cycleLength))

        // Not ovulation days
        assertFalse(CyclePredictionUtil.isOvulationDay(LocalDate.of(2024, 1, 14), lastPeriodDate, cycleLength))
        assertFalse(CyclePredictionUtil.isOvulationDay(LocalDate.of(2024, 1, 16), lastPeriodDate, cycleLength))
    }

    @Test
    fun `getCurrentCycleDay calculates cycle day correctly`() {
        val lastPeriodDate = REFERENCE_DATE

        // Day 1 (same day as last period)
        assertEquals(1, CyclePredictionUtil.getCurrentCycleDay(lastPeriodDate, REFERENCE_DATE))

        // Day 5 (4 days after last period)
        assertEquals(5, CyclePredictionUtil.getCurrentCycleDay(lastPeriodDate, REFERENCE_DATE.plusDays(4)))

        // Day 15 (14 days after last period)
        assertEquals(15, CyclePredictionUtil.getCurrentCycleDay(lastPeriodDate, REFERENCE_DATE.plusDays(14)))

        // Date before last period should return null
        assertNull(CyclePredictionUtil.getCurrentCycleDay(lastPeriodDate, REFERENCE_DATE.minusDays(1)))
    }

    // ===== VALIDATION TESTS =====

    @Test(expected = IllegalArgumentException::class)
    fun `getNextPeriodDate throws exception for cycle length too short`() {
        CyclePredictionUtil.getNextPeriodDate(REFERENCE_DATE, 14)
    }

    @Test(expected = IllegalArgumentException::class)
    fun `getNextPeriodDate throws exception for cycle length too long`() {
        CyclePredictionUtil.getNextPeriodDate(REFERENCE_DATE, 46)
    }

    @Test(expected = IllegalArgumentException::class)
    fun `predictCycle throws exception for invalid cycle length`() {
        CyclePredictionUtil.predictCycle(REFERENCE_DATE, 50)
    }

    @Test(expected = IllegalArgumentException::class)
    fun `predictMultipleCycles throws exception for zero cycles`() {
        CyclePredictionUtil.predictMultipleCycles(REFERENCE_DATE, 28, 0)
    }

    @Test(expected = IllegalArgumentException::class)
    fun `predictMultipleCycles throws exception for too many cycles`() {
        CyclePredictionUtil.predictMultipleCycles(REFERENCE_DATE, 28, 13)
    }

    // ===== CYCLE PREDICTION DATA CLASS TESTS =====

    @Test
    fun `CyclePrediction getFertileWindowLength returns correct length`() {
        val prediction = CyclePrediction(
            nextPeriodDate = LocalDate.of(2024, 1, 29),
            ovulationDate = LocalDate.of(2024, 1, 15),
            fertileWindowStart = LocalDate.of(2024, 1, 10),
            fertileWindowEnd = LocalDate.of(2024, 1, 15),
            daysUntilNextPeriod = 28,
            daysUntilOvulation = 14
        )

        assertEquals(6, prediction.getFertileWindowLength()) // January 10-15 = 6 days
    }

    @Test
    fun `CyclePrediction isInPast works correctly`() {
        val pastPrediction = CyclePrediction(
            nextPeriodDate = LocalDate.of(2024, 1, 1),
            ovulationDate = LocalDate.of(2023, 12, 18),
            fertileWindowStart = LocalDate.of(2023, 12, 13),
            fertileWindowEnd = LocalDate.of(2023, 12, 18),
            daysUntilNextPeriod = -10,
            daysUntilOvulation = -24
        )

        val futurePrediction = CyclePrediction(
            nextPeriodDate = LocalDate.of(2025, 1, 1),
            ovulationDate = LocalDate.of(2024, 12, 18),
            fertileWindowStart = LocalDate.of(2024, 12, 13),
            fertileWindowEnd = LocalDate.of(2024, 12, 18),
            daysUntilNextPeriod = 365,
            daysUntilOvulation = 351
        )

        assertTrue(pastPrediction.isInPast(LocalDate.of(2024, 6, 1)))
        assertFalse(futurePrediction.isInPast(LocalDate.of(2024, 6, 1)))
    }

    @Test
    fun `CyclePrediction getSummary provides readable format`() {
        val prediction = CyclePrediction(
            nextPeriodDate = LocalDate.of(2024, 1, 29),
            ovulationDate = LocalDate.of(2024, 1, 15),
            fertileWindowStart = LocalDate.of(2024, 1, 10),
            fertileWindowEnd = LocalDate.of(2024, 1, 15),
            daysUntilNextPeriod = 28,
            daysUntilOvulation = 14
        )

        val summary = prediction.getSummary()
        assertTrue(summary.contains("Next Period: 2024-01-29"))
        assertTrue(summary.contains("Ovulation: 2024-01-15"))
        assertTrue(summary.contains("Fertile Window: 2024-01-10 to 2024-01-15"))
    }

    // ===== EDGE CASE TESTS =====

    @Test
    fun `cycle calculations work correctly across month boundaries`() {
        // Start at end of January
        val lastPeriodDate = LocalDate.of(2024, 1, 20)
        val cycleLength = 28

        val nextPeriod = CyclePredictionUtil.getNextPeriodDate(lastPeriodDate, cycleLength)
        val ovulation = CyclePredictionUtil.getOvulationDate(nextPeriod)
        val fertileWindow = CyclePredictionUtil.getFertileWindow(ovulation)

        // Should cross into February
        assertEquals(LocalDate.of(2024, 2, 17), nextPeriod) // Jan 20 + 28 = Feb 17
        assertEquals(LocalDate.of(2024, 2, 3), ovulation) // Feb 17 - 14 = Feb 3
        assertEquals(LocalDate.of(2024, 1, 29), fertileWindow.first) // Feb 3 - 5 = Jan 29
        assertEquals(LocalDate.of(2024, 2, 3), fertileWindow.second)
    }

    @Test
    fun `cycle calculations work correctly across year boundaries`() {
        // Start near end of December
        val lastPeriodDate = LocalDate.of(2023, 12, 20)
        val cycleLength = 28

        val nextPeriod = CyclePredictionUtil.getNextPeriodDate(lastPeriodDate, cycleLength)
        val ovulation = CyclePredictionUtil.getOvulationDate(nextPeriod)
        val fertileWindow = CyclePredictionUtil.getFertileWindow(ovulation)

        // Should cross into next year
        assertEquals(LocalDate.of(2024, 1, 17), nextPeriod) // Dec 20 + 28 = Jan 17
        assertEquals(LocalDate.of(2024, 1, 3), ovulation) // Jan 17 - 14 = Jan 3
        assertEquals(LocalDate.of(2023, 12, 29), fertileWindow.first) // Jan 3 - 5 = Dec 29
        assertEquals(LocalDate.of(2024, 1, 3), fertileWindow.second)
    }

    @Test
    fun `leap year February calculations work correctly`() {
        // Test leap year behavior (2024 is a leap year)
        val lastPeriodDate = LocalDate.of(2024, 2, 15) // Mid February
        val cycleLength = 28

        val nextPeriod = CyclePredictionUtil.getNextPeriodDate(lastPeriodDate, cycleLength)
        val ovulation = CyclePredictionUtil.getOvulationDate(nextPeriod)

        assertEquals(LocalDate.of(2024, 3, 14), nextPeriod) // Feb 15 + 28 = Mar 14 (accounting for leap year)
        assertEquals(LocalDate.of(2024, 2, 29), ovulation) // Mar 14 - 14 = Feb 29 (leap day!)
    }
}