package com.mjandroiddev.periodcalendar.data.model

enum class MoodType(val displayName: String, val emoji: String) {
    HAPPY("Happy", "😊"),
    SAD("Sad", "😢"),
    IRRITABLE("Irritable", "😤"),
    ANXIOUS("Anxious", "😰"),
    ENERGETIC("Energetic", "⚡"),
    TIRED("Tired", "😴"),
    EMOTIONAL("Emotional", "🥺"),
    CONFIDENT("Confident", "💪");

    companion object {
        fun fromString(value: String?): MoodType? {
            return entries.find { it.name.equals(value, ignoreCase = true) }
        }
    }
}