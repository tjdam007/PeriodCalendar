package com.mjandroiddev.periodcalendar.data.database

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.mjandroiddev.periodcalendar.data.model.CrampLevel
import com.mjandroiddev.periodcalendar.data.model.FlowLevel
import com.mjandroiddev.periodcalendar.data.model.MoodType
import java.time.LocalDate

@Entity(tableName = "cycle_entries")
data class CycleEntry(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val date: LocalDate,
    val isPeriod: Boolean,
    val flowLevel: FlowLevel? = null,
    val mood: MoodType? = null,
    val cramps: CrampLevel? = null
)