package com.mjandroiddev.periodcalendar.data.model

import com.mjandroiddev.periodcalendar.data.database.CycleEntry

// Extension functions for CycleEntry to work with enums
fun CycleEntry.getFlowLevelEnum(): FlowLevel = FlowLevel.fromValue(flowLevel?.name.toString())
fun CycleEntry.getMoodEnum(): Mood = Mood.fromValue(mood?.name.toString())
fun CycleEntry.getCrampsLevelEnum(): CrampsLevel = CrampsLevel.fromValue(cramps?.name.toString())