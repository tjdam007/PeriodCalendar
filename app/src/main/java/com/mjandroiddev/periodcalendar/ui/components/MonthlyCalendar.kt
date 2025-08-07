package com.mjandroiddev.periodcalendar.ui.components

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.mjandroiddev.periodcalendar.data.database.CycleEntry
import com.mjandroiddev.periodcalendar.data.database.UserSettings
import com.mjandroiddev.periodcalendar.data.model.*
import com.mjandroiddev.periodcalendar.ui.theme.PeriodCalendarTheme
import com.mjandroiddev.periodcalendar.utils.CyclePredictionUtil
import java.time.LocalDate
import java.time.YearMonth
import java.time.format.DateTimeFormatter
import java.time.format.TextStyle
import java.util.*

/**
 * Data class representing the state of a calendar day
 */
data class CalendarDayState(
    val date: LocalDate,
    val isPeriod: Boolean = false,
    val isFertile: Boolean = false,
    val isOvulation: Boolean = false,
    val isToday: Boolean = false,
    val isSelected: Boolean = false,
    val isCurrentMonth: Boolean = true,
    val hasSymptoms: Boolean = false,
    val mood: Mood = Mood.NONE,
    val crampsLevel: CrampsLevel = CrampsLevel.NONE,
    val flowLevel: FlowLevel = FlowLevel.NONE,
    val cycleEntry: CycleEntry? = null
)

/**
 * Comprehensive monthly calendar with period tracking features
 */
@Composable
fun MonthlyCalendar(
    currentMonth: YearMonth,
    selectedDate: LocalDate?,
    cycleEntries: List<CycleEntry>,
    userSettings: UserSettings,
    onDateClick: (LocalDate) -> Unit,
    onMonthChange: (YearMonth) -> Unit,
    modifier: Modifier = Modifier
) {
    val today = LocalDate.now()
    val lastPeriodDate = cycleEntries
        .filter { it.isPeriod }
        .maxByOrNull { it.date }
        ?.date
    
    Card(
        modifier = modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        )
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            // Calendar Header with Navigation
            CalendarHeader(
                currentMonth = currentMonth,
                onPreviousMonth = { onMonthChange(currentMonth.minusMonths(1)) },
                onNextMonth = { onMonthChange(currentMonth.plusMonths(1)) }
            )
            
            Spacer(modifier = Modifier.height(16.dp))
            
            // Days of Week Header
            DaysOfWeekHeader()
            
            Spacer(modifier = Modifier.height(8.dp))
            
            // Calendar Grid
            CalendarGrid(
                currentMonth = currentMonth,
                selectedDate = selectedDate,
                cycleEntries = cycleEntries,
                userSettings = userSettings,
                lastPeriodDate = lastPeriodDate,
                today = today,
                onDateClick = onDateClick
            )
        }
    }
}

@OptIn(ExperimentalAnimationApi::class)
@Composable
private fun CalendarHeader(
    currentMonth: YearMonth,
    onPreviousMonth: () -> Unit,
    onNextMonth: () -> Unit
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Previous Month Button
        IconButton(
            onClick = onPreviousMonth,
            modifier = Modifier.size(40.dp)
        ) {
            Icon(
                imageVector = Icons.Default.ChevronLeft,
                contentDescription = "Previous Month",
                tint = MaterialTheme.colorScheme.primary
            )
        }
        
        // Month and Year Title with Animation
        AnimatedContent(
            targetState = currentMonth,
            transitionSpec = {
                (slideInHorizontally(
                    initialOffsetX = { if (targetState > initialState) 300 else -300 }
                ) + fadeIn()).togetherWith(
                    slideOutHorizontally(
                                targetOffsetX = { if (targetState > initialState) -300 else 300 }
                            ) + fadeOut())
            },
            label = "MonthTransition"
        ) { month ->
            Text(
                text = month.format(DateTimeFormatter.ofPattern("MMMM yyyy")),
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface,
                textAlign = TextAlign.Center
            )
        }
        
        // Next Month Button
        IconButton(
            onClick = onNextMonth,
            modifier = Modifier.size(40.dp)
        ) {
            Icon(
                imageVector = Icons.Default.ChevronRight,
                contentDescription = "Next Month",
                tint = MaterialTheme.colorScheme.primary
            )
        }
    }
}

@Composable
private fun DaysOfWeekHeader() {
    val dayNames = listOf("Mon", "Tue", "Wed", "Thu", "Fri", "Sat", "Sun")
    
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceEvenly
    ) {
        dayNames.forEach { dayName ->
            Text(
                text = dayName,
                modifier = Modifier.weight(1f),
                textAlign = TextAlign.Center,
                style = MaterialTheme.typography.labelMedium,
                fontWeight = FontWeight.SemiBold,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

@Composable
private fun CalendarGrid(
    currentMonth: YearMonth,
    selectedDate: LocalDate?,
    cycleEntries: List<CycleEntry>,
    userSettings: UserSettings,
    lastPeriodDate: LocalDate?,
    today: LocalDate,
    onDateClick: (LocalDate) -> Unit
) {
    val daysInMonth = generateCalendarDays(
        currentMonth = currentMonth,
        cycleEntries = cycleEntries,
        userSettings = userSettings,
        lastPeriodDate = lastPeriodDate,
        today = today,
        selectedDate = selectedDate
    )
    
    LazyVerticalGrid(
        columns = GridCells.Fixed(7),
        horizontalArrangement = Arrangement.spacedBy(2.dp),
        verticalArrangement = Arrangement.spacedBy(2.dp),
        modifier = Modifier.heightIn(max = 300.dp)
    ) {
        items(daysInMonth) { dayState ->
            CalendarDayCell(
                dayState = dayState,
                onDateClick = onDateClick
            )
        }
    }
}

@Composable
private fun CalendarDayCell(
    dayState: CalendarDayState,
    onDateClick: (LocalDate) -> Unit
) {
    val backgroundColor = when {
        dayState.isSelected -> MaterialTheme.colorScheme.primary
        dayState.isPeriod -> MaterialTheme.colorScheme.error
        dayState.isOvulation -> MaterialTheme.colorScheme.tertiary
        dayState.isFertile -> MaterialTheme.colorScheme.secondary
        dayState.isToday -> MaterialTheme.colorScheme.primaryContainer
        else -> Color.Transparent
    }
    
    val contentColor = when {
        dayState.isSelected -> MaterialTheme.colorScheme.onPrimary
        dayState.isPeriod -> MaterialTheme.colorScheme.onError
        dayState.isOvulation -> MaterialTheme.colorScheme.onTertiary
        dayState.isFertile -> MaterialTheme.colorScheme.onSecondary
        dayState.isToday -> MaterialTheme.colorScheme.onPrimaryContainer
        !dayState.isCurrentMonth -> MaterialTheme.colorScheme.onSurface.copy(alpha = 0.4f)
        else -> MaterialTheme.colorScheme.onSurface
    }
    
    val animatedScale by animateFloatAsState(
        targetValue = if (dayState.isSelected) 1.1f else 1f,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioMediumBouncy,
            stiffness = Spring.StiffnessLow
        ),
        label = "DayScale"
    )
    
    Box(
        modifier = Modifier
            .size(40.dp)
            .scale(animatedScale)
            .clip(CircleShape)
            .background(backgroundColor)
            .clickable(enabled = dayState.isCurrentMonth) {
                onDateClick(dayState.date)
            },
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            // Day Number
            Text(
                text = dayState.date.dayOfMonth.toString(),
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = if (dayState.isToday || dayState.isSelected) FontWeight.Bold else FontWeight.Normal,
                color = contentColor,
                fontSize = 14.sp
            )
            
            // Symptom Indicators
            if (dayState.hasSymptoms && dayState.isCurrentMonth) {
                Row(
                    horizontalArrangement = Arrangement.Center,
                    modifier = Modifier.padding(top = 1.dp)
                ) {
                    SymptomIndicators(dayState)
                }
            }
        }
    }
}

@Composable
private fun SymptomIndicators(dayState: CalendarDayState) {
    Row(
        horizontalArrangement = Arrangement.spacedBy(1.dp)
    ) {
        // Mood indicator
        if (dayState.mood != Mood.NONE) {
            Box(
                modifier = Modifier
                    .size(4.dp)
                    .background(
                        color = getMoodColor(dayState.mood),
                        shape = CircleShape
                    )
            )
        }
        
        // Cramps indicator
        if (dayState.crampsLevel != CrampsLevel.NONE) {
            Box(
                modifier = Modifier
                    .size(4.dp)
                    .background(
                        color = getCrampsColor(dayState.crampsLevel),
                        shape = CircleShape
                    )
            )
        }
    }
}

@Composable
private fun getMoodColor(mood: Mood): Color {
    return when (mood) {
        Mood.HAPPY -> Color(0xFF4CAF50)
        Mood.SAD -> Color(0xFF2196F3)
        Mood.ANGRY -> Color(0xFFFF5722)
        Mood.ANXIOUS -> Color(0xFFFF9800)
        Mood.CALM -> Color(0xFF9C27B0)
        Mood.ENERGETIC -> Color(0xFFFFEB3B)
        Mood.TIRED -> Color(0xFF607D8B)
        else -> Color.Transparent
    }
}

@Composable
private fun getCrampsColor(crampsLevel: CrampsLevel): Color {
    return when (crampsLevel) {
        CrampsLevel.MILD -> Color(0xFFFFE0B2)
        CrampsLevel.MODERATE -> Color(0xFFFFB74D)
        CrampsLevel.SEVERE -> Color(0xFFFF5722)
        else -> Color.Transparent
    }
}

/**
 * Generates calendar days with their states for the given month
 */
private fun generateCalendarDays(
    currentMonth: YearMonth,
    cycleEntries: List<CycleEntry>,
    userSettings: UserSettings,
    lastPeriodDate: LocalDate?,
    today: LocalDate,
    selectedDate: LocalDate?
): List<CalendarDayState> {
    val firstDayOfMonth = currentMonth.atDay(1)
    val lastDayOfMonth = currentMonth.atEndOfMonth()
    
    // Calculate start date (Monday of the first week)
    val firstMonday = firstDayOfMonth.minusDays((firstDayOfMonth.dayOfWeek.value - 1).toLong())
    
    // Calculate end date (Sunday of the last week)
    val lastSunday = lastDayOfMonth.plusDays((7 - lastDayOfMonth.dayOfWeek.value).toLong())
    
    val entryMap = cycleEntries.associateBy { it.date }
    val days = mutableListOf<CalendarDayState>()
    
    // Generate predictions if we have last period data
    val predictions = lastPeriodDate?.let {
        try {
            CyclePredictionUtil.predictCycle(it, userSettings.avgCycleLength)
        } catch (e: Exception) {
            null
        }
    }
    
    var currentDate = firstMonday
    while (!currentDate.isAfter(lastSunday)) {
        val entry = entryMap[currentDate]
        val isCurrentMonth = currentDate.monthValue == currentMonth.monthValue
        
        // Determine if date is in fertile window or ovulation
        val isFertile = predictions?.let { pred ->
            !currentDate.isBefore(pred.fertileWindowStart) && !currentDate.isAfter(pred.fertileWindowEnd)
        } ?: false
        
        val isOvulation = predictions?.let { pred ->
            currentDate == pred.ovulationDate
        } ?: false
        
        val dayState = CalendarDayState(
            date = currentDate,
            isPeriod = entry?.isPeriod ?: false,
            isFertile = isFertile && !entry?.isPeriod.orDefault(false),
            isOvulation = isOvulation && !entry?.isPeriod.orDefault(false),
            isToday = currentDate == today,
            isSelected = currentDate == selectedDate,
            isCurrentMonth = isCurrentMonth,
            hasSymptoms = entry?.let { it.mood.isNotEmpty() || it.cramps != CrampsLevel.NONE.value } ?: false,
            mood = entry?.getMoodEnum() ?: Mood.NONE,
            crampsLevel = entry?.getCrampsLevelEnum() ?: CrampsLevel.NONE,
            flowLevel = entry?.getFlowLevelEnum() ?: FlowLevel.NONE,
            cycleEntry = entry
        )
        
        days.add(dayState)
        currentDate = currentDate.plusDays(1)
    }
    
    return days
}

private fun Boolean?.orDefault(default: Boolean) = this ?: default

@Preview(name = "Monthly Calendar - Light Theme")
@Composable
private fun MonthlyCalendarLightPreview() {
    PeriodCalendarTheme(darkTheme = false) {
        Surface {
            val sampleEntries = listOf(
                CycleEntry(
                    id = 1,
                    date = LocalDate.now().minusDays(5),
                    isPeriod = true,
                    flowLevel = FlowLevel.MEDIUM.value,
                    mood = Mood.TIRED.value,
                    cramps = CrampsLevel.MILD.value
                ),
                CycleEntry(
                    id = 2,
                    date = LocalDate.now().minusDays(4),
                    isPeriod = true,
                    flowLevel = FlowLevel.HEAVY.value,
                    mood = Mood.SAD.value,
                    cramps = CrampsLevel.MODERATE.value
                ),
                CycleEntry(
                    id = 3,
                    date = LocalDate.now().plusDays(3),
                    isPeriod = false,
                    flowLevel = FlowLevel.NONE.value,
                    mood = Mood.HAPPY.value,
                    cramps = CrampsLevel.NONE.value
                )
            )
            
            MonthlyCalendar(
                currentMonth = YearMonth.now(),
                selectedDate = LocalDate.now(),
                cycleEntries = sampleEntries,
                userSettings = UserSettings(),
                onDateClick = { },
                onMonthChange = { }
            )
        }
    }
}

@Preview(name = "Monthly Calendar - Dark Theme")
@Composable
private fun MonthlyCalendarDarkPreview() {
    PeriodCalendarTheme(darkTheme = true) {
        Surface {
            val sampleEntries = listOf(
                CycleEntry(
                    id = 1,
                    date = LocalDate.now().minusDays(5),
                    isPeriod = true,
                    flowLevel = FlowLevel.MEDIUM.value,
                    mood = Mood.TIRED.value,
                    cramps = CrampsLevel.MILD.value
                ),
                CycleEntry(
                    id = 2,
                    date = LocalDate.now().minusDays(4),
                    isPeriod = true,
                    flowLevel = FlowLevel.HEAVY.value,
                    mood = Mood.ANGRY.value,
                    cramps = CrampsLevel.SEVERE.value
                )
            )
            
            MonthlyCalendar(
                currentMonth = YearMonth.now(),
                selectedDate = LocalDate.now().minusDays(2),
                cycleEntries = sampleEntries,
                userSettings = UserSettings(avgCycleLength = 30, periodDuration = 6),
                onDateClick = { },
                onMonthChange = { }
            )
        }
    }
}