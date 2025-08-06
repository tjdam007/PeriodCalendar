package com.mjandroiddev.periodcalendar.data.database

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.time.LocalDate

@Entity(tableName = "periods")
data class PeriodEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val startDate: LocalDate,
    val endDate: LocalDate?,
    val flow: Int, // 1-5 scale
    val symptoms: String = "",
    val notes: String = ""
)