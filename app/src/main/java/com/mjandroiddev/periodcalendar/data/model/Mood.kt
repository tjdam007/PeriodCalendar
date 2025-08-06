package com.mjandroiddev.periodcalendar.data.model

enum class Mood(val displayName: String, val value: String, val emoji: String) {
    NONE("Not Specified", "", ""),
    HAPPY("Happy", "happy", "😊"),
    SAD("Sad", "sad", "😢"),
    ANGRY("Angry", "angry", "😠"),
    ANXIOUS("Anxious", "anxious", "😰"),
    CALM("Calm", "calm", "😌"),
    ENERGETIC("Energetic", "energetic", "⚡"),
    TIRED("Tired", "tired", "😴");

    companion object {
        fun fromValue(value: String): Mood {
            return entries.find { it.value == value } ?: NONE
        }
        
        fun getAllValues(): List<String> = entries.map { it.value }
        fun getAllDisplayNames(): List<String> = entries.map { it.displayName }
        fun getAllWithEmojis(): List<String> = entries.map { "${it.emoji} ${it.displayName}".trim() }
    }
}