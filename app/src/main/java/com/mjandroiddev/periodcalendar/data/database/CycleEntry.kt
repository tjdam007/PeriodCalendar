package com.mjandroiddev.periodcalendar.data.database

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.time.LocalDate

@Entity(tableName = "cycle_entries")
data class CycleEntry(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val date: LocalDate,
    val isPeriod: Boolean,
    val flowLevel: String, // "none", "light", "medium", "heavy", "very_heavy"
    val mood: String, // "happy", "sad", "angry", "anxious", "calm", "energetic", "tired"
    val cramps: String // "none", "mild", "moderate", "severe"
)