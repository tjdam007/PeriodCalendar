package com.mjandroiddev.periodcalendar.data.database

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "user_settings")
data class UserSettings(
    @PrimaryKey
    val id: Int = 1, // Always 1 to ensure single row
    val avgCycleLength: Int = 28, // Default cycle length in days
    val periodDuration: Int = 5, // Default period duration in days
    val notifBeforePeriod: Int = 1, // Days before period to notify
    val notifOvulation: Boolean = true, // Enable ovulation notifications
    val notifFertileWindow: Boolean = true, // Enable fertile window notifications
    val themeMode: String = "system" // "light", "dark", "system"
)