package com.mjandroiddev.periodcalendar.data.model

import com.mjandroiddev.periodcalendar.data.database.CycleEntry

// Extension functions for CycleEntry to work with enums
fun CycleEntry.getFlowLevelEnum(): FlowLevel = FlowLevel.fromValue(flowLevel)
fun CycleEntry.getMoodEnum(): Mood = Mood.fromValue(mood)
fun CycleEntry.getCrampsLevelEnum(): CrampsLevel = CrampsLevel.fromValue(cramps)

// Convenience functions for creating CycleEntry with enums
fun CycleEntry.withFlowLevel(flowLevel: FlowLevel): CycleEntry = 
    this.copy(flowLevel = flowLevel.value)

fun CycleEntry.withMood(mood: Mood): CycleEntry = 
    this.copy(mood = mood.value)

fun CycleEntry.withCrampsLevel(crampsLevel: CrampsLevel): CycleEntry = 
    this.copy(cramps = crampsLevel.value)

// Helper functions for UI
fun CycleEntry.getDisplayFlowLevel(): String = getFlowLevelEnum().displayName
fun CycleEntry.getDisplayMood(): String = getMoodEnum().let { 
    if (it.emoji.isNotEmpty()) "${it.emoji} ${it.displayName}" else it.displayName
}
fun CycleEntry.getDisplayCrampsLevel(): String = getCrampsLevelEnum().displayName

// Validation functions
fun CycleEntry.isValid(): Boolean {
    return flowLevel in FlowLevel.getAllValues() &&
           (mood.isEmpty() || mood in Mood.getAllValues()) &&
           cramps in CrampsLevel.getAllValues()
}

fun CycleEntry.hasAnySymptoms(): Boolean = 
    isPeriod || mood.isNotEmpty() || cramps != CrampsLevel.NONE.value