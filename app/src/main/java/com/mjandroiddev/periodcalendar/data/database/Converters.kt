package com.mjandroiddev.periodcalendar.data.database

import androidx.room.TypeConverter
import com.mjandroiddev.periodcalendar.data.model.CrampLevel
import com.mjandroiddev.periodcalendar.data.model.FlowLevel
import com.mjandroiddev.periodcalendar.data.model.MoodType
import java.time.LocalDate

class Converters {
    @TypeConverter
    fun fromLocalDate(date: LocalDate?): String? {
        return date?.toString()
    }

    @TypeConverter
    fun toLocalDate(dateString: String?): LocalDate? {
        return dateString?.let { LocalDate.parse(it) }
    }
    
    @TypeConverter
    fun fromFlowLevel(flowLevel: FlowLevel?): String? {
        return flowLevel?.name
    }
    
    @TypeConverter
    fun toFlowLevel(flowLevelString: String?): FlowLevel? {
        return flowLevelString?.let { FlowLevel.fromString(it) }
    }
    
    @TypeConverter
    fun fromMoodType(moodType: MoodType?): String? {
        return moodType?.name
    }
    
    @TypeConverter
    fun toMoodType(moodTypeString: String?): MoodType? {
        return MoodType.fromString(moodTypeString)
    }
    
    @TypeConverter
    fun fromCrampLevel(crampLevel: CrampLevel?): String? {
        return crampLevel?.name
    }
    
    @TypeConverter
    fun toCrampLevel(crampLevelString: String?): CrampLevel? {
        return CrampLevel.fromString(crampLevelString)
    }
}