package com.mjandroiddev.periodcalendar.data

import com.mjandroiddev.periodcalendar.data.database.CycleEntry
import com.mjandroiddev.periodcalendar.data.database.UserSettings
import com.mjandroiddev.periodcalendar.data.model.*
import java.time.LocalDate

// Test class to verify compilation
class DatabaseTest {
    
    fun testCycleEntry() {
        val entry = CycleEntry(
            id = 1,
            date = LocalDate.now(),
            isPeriod = true,
            flowLevel = FlowLevel.MEDIUM.value,
            mood = Mood.HAPPY.value,
            cramps = CrampsLevel.MILD.value
        )
        
        // Test extension functions
        val flowLevel = entry.getFlowLevelEnum()
        val mood = entry.getMoodEnum()
        val crampsLevel = entry.getCrampsLevelEnum()
        val displayMood = entry.getDisplayMood()
        val isValid = entry.isValid()
        val hasSymptoms = entry.hasAnySymptoms()
        
        // Test modification
        val modifiedEntry = entry.withFlowLevel(FlowLevel.HEAVY)
            .withMood(Mood.TIRED)
            .withCrampsLevel(CrampsLevel.MODERATE)
    }
    
    fun testUserSettings() {
        val settings = UserSettings(
            avgCycleLength = 28,
            periodDuration = 5,
            notifBeforePeriod = 2,
            notifOvulation = true,
            notifFertileWindow = true,
            themeMode = ThemeMode.SYSTEM.value
        )
        
        // Test extension functions
        val themeMode = settings.getThemeModeEnum()
        val isValid = settings.isValid()
        val cycleSummary = settings.getCycleSummary()
        val notificationSummary = settings.getNotificationSummary()
        
        // Test calculation functions
        val lastPeriod = LocalDate.now().minusDays(15)
        val nextPeriod = settings.calculateNextPeriodDate(lastPeriod)
        val ovulationDate = settings.calculateOvulationDate(lastPeriod)
        val fertileWindow = settings.calculateFertileWindow(lastPeriod)
        val notificationDate = settings.getPeriodNotificationDate(nextPeriod)
    }
}