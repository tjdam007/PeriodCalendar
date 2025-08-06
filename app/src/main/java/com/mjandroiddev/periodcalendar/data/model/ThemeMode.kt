package com.mjandroiddev.periodcalendar.data.model

enum class ThemeMode(val displayName: String, val value: String) {
    LIGHT("Light", "light"),
    DARK("Dark", "dark"),
    SYSTEM("System Default", "system");

    companion object {
        fun fromValue(value: String): ThemeMode {
            return entries.find { it.value == value } ?: SYSTEM
        }
        
        fun getAllValues(): List<String> = entries.map { it.value }
        fun getAllDisplayNames(): List<String> = entries.map { it.displayName }
    }
}