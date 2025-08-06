package com.mjandroiddev.periodcalendar.data.model

import com.mjandroiddev.periodcalendar.data.database.UserSettings
import java.time.LocalDate

// Extension functions for UserSettings to work with enums
fun UserSettings.getThemeModeEnum(): ThemeMode = ThemeMode.fromValue(themeMode)

fun UserSettings.withThemeMode(themeMode: ThemeMode): UserSettings = 
    this.copy(themeMode = themeMode.value)

// Validation functions
fun UserSettings.isValid(): Boolean {
    return avgCycleLength in 15..45 && // Reasonable cycle length range
           periodDuration in 1..10 && // Reasonable period duration range
           notifBeforePeriod in 0..7 && // Max 7 days notification
           themeMode in ThemeMode.getAllValues()
}

// Cycle calculation helpers
fun UserSettings.calculateNextPeriodDate(lastPeriodStart: LocalDate): LocalDate {
    return lastPeriodStart.plusDays(avgCycleLength.toLong())
}

fun UserSettings.calculateOvulationDate(lastPeriodStart: LocalDate): LocalDate {
    // Ovulation typically occurs 14 days before next period
    return lastPeriodStart.plusDays((avgCycleLength - 14).toLong())
}

fun UserSettings.calculateFertileWindow(lastPeriodStart: LocalDate): Pair<LocalDate, LocalDate> {
    val ovulationDate = calculateOvulationDate(lastPeriodStart)
    // Fertile window is typically 5 days before ovulation to 1 day after
    return Pair(
        ovulationDate.minusDays(5),
        ovulationDate.plusDays(1)
    )
}

fun UserSettings.getPeriodNotificationDate(nextPeriodDate: LocalDate): LocalDate {
    return nextPeriodDate.minusDays(notifBeforePeriod.toLong())
}

// Display helpers
fun UserSettings.getDisplayThemeMode(): String = getThemeModeEnum().displayName

fun UserSettings.getCycleSummary(): String {
    return "Cycle: ${avgCycleLength} days, Period: ${periodDuration} days"
}

fun UserSettings.getNotificationSummary(): String {
    val notifications = mutableListOf<String>()
    if (notifBeforePeriod > 0) {
        notifications.add("Period: ${notifBeforePeriod} day(s) before")
    }
    if (notifOvulation) {
        notifications.add("Ovulation")
    }
    if (notifFertileWindow) {
        notifications.add("Fertile window")
    }
    return if (notifications.isEmpty()) "No notifications" else notifications.joinToString(", ")
}