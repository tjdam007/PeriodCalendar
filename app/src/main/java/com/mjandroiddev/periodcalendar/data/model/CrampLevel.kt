package com.mjandroiddev.periodcalendar.data.model

enum class CrampLevel(val displayName: String) {
    NONE("None"),
    MILD("Mild"),
    MODERATE("Moderate"),
    SEVERE("Severe");

    companion object {
        fun fromString(value: String?): CrampLevel {
            return entries.find { it.name.equals(value, ignoreCase = true) } ?: NONE
        }
    }
}