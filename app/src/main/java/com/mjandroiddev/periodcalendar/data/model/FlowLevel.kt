package com.mjandroiddev.periodcalendar.data.model

enum class FlowLevel(val displayName: String, val value: String) {
    NONE("No Flow", "none"),
    LIGHT("Light", "light"),
    MEDIUM("Medium", "medium"),
    HEAVY("Heavy", "heavy"),
    VERY_HEAVY("Very Heavy", "very_heavy");

    companion object {
        fun fromValue(value: String): FlowLevel {
            return entries.find { it.value == value } ?: NONE
        }
        
        fun getAllValues(): List<String> = entries.map { it.value }
        fun getAllDisplayNames(): List<String> = entries.map { it.displayName }
    }
}