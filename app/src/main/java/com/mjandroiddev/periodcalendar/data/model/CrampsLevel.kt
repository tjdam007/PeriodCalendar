package com.mjandroiddev.periodcalendar.data.model

enum class CrampsLevel(val displayName: String, val value: String) {
    NONE("No Cramps", "none"),
    MILD("Mild", "mild"),
    MODERATE("Moderate", "moderate"),
    SEVERE("Severe", "severe");

    companion object {
        fun fromValue(value: String): CrampsLevel {
            return entries.find { it.value == value } ?: NONE
        }
        
        fun getAllValues(): List<String> = entries.map { it.value }
        fun getAllDisplayNames(): List<String> = entries.map { it.displayName }
    }
}