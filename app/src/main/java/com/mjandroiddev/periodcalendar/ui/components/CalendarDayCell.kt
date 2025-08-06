package com.mjandroiddev.periodcalendar.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.mjandroiddev.periodcalendar.ui.theme.*
import java.time.LocalDate

enum class DayType {
    DEFAULT,
    PERIOD,
    FERTILE,
    OVULATION,
    SELECTED
}

@Composable
fun CalendarDayCell(
    day: Int,
    dayType: DayType = DayType.DEFAULT,
    isToday: Boolean = false,
    isSelected: Boolean = false,
    isEnabled: Boolean = true,
    modifier: Modifier = Modifier,
    onClick: () -> Unit = {}
) {
    val (backgroundColor, contentColor) = when {
        isSelected -> MaterialTheme.colorScheme.primary to MaterialTheme.colorScheme.onPrimary
        dayType == DayType.PERIOD -> MaterialTheme.colorScheme.error to MaterialTheme.colorScheme.onError
        dayType == DayType.FERTILE -> MaterialTheme.colorScheme.secondary to MaterialTheme.colorScheme.onSecondary
        dayType == DayType.OVULATION -> MaterialTheme.colorScheme.tertiary to MaterialTheme.colorScheme.onTertiary
        isToday -> MaterialTheme.colorScheme.primaryContainer to MaterialTheme.colorScheme.onPrimaryContainer
        else -> Color.Transparent to MaterialTheme.colorScheme.onSurface
    }

    val alpha = if (isEnabled) 1f else 0.38f

    Surface(
        modifier = modifier
            .size(40.dp)
            .clip(CircleShape)
            .clickable(enabled = isEnabled) { onClick() },
        color = backgroundColor.copy(alpha = alpha),
        shape = CircleShape,
        shadowElevation = if (isSelected || dayType != DayType.DEFAULT) 2.dp else 0.dp
    ) {
        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier.fillMaxSize()
        ) {
            Text(
                text = day.toString(),
                style = MaterialTheme.typography.bodyMedium,
                color = contentColor.copy(alpha = alpha),
                fontWeight = if (isToday || isSelected) FontWeight.Bold else FontWeight.Normal,
                textAlign = TextAlign.Center
            )

            // Today indicator (border)
            if (isToday && !isSelected && dayType == DayType.DEFAULT) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(
                            color = Color.Transparent,
                            shape = CircleShape
                        )
                        .then(
                            Modifier.background(
                                color = Color.Transparent,
                                shape = CircleShape
                            )
                        )
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(2.dp)
                            .background(
                                color = Color.Transparent,
                                shape = CircleShape
                            )
                    )
                }
            }
        }
    }
}

@Preview(name = "Light Theme - Calendar Days")
@Composable
private fun CalendarDayCellLightPreview() {
    PeriodCalendarTheme(darkTheme = false) {
        Surface {
            Column(
                modifier = Modifier.padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Text(
                    text = "Calendar Day Types",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
                
                // Row 1: Basic days
                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    CalendarDayCell(day = 1, dayType = DayType.DEFAULT)
                    CalendarDayCell(day = 2, isToday = true)
                    CalendarDayCell(day = 3, isSelected = true)
                    CalendarDayCell(day = 4, isEnabled = false)
                    Text("Basic", style = MaterialTheme.typography.bodySmall)
                }
                
                // Row 2: Period days
                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    CalendarDayCell(day = 5, dayType = DayType.PERIOD)
                    CalendarDayCell(day = 6, dayType = DayType.PERIOD, isToday = true)
                    CalendarDayCell(day = 7, dayType = DayType.PERIOD, isSelected = true)
                    CalendarDayCell(day = 8, dayType = DayType.PERIOD, isEnabled = false)
                    Text("Period", style = MaterialTheme.typography.bodySmall)
                }
                
                // Row 3: Fertile days
                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    CalendarDayCell(day = 9, dayType = DayType.FERTILE)
                    CalendarDayCell(day = 10, dayType = DayType.FERTILE, isToday = true)
                    CalendarDayCell(day = 11, dayType = DayType.FERTILE, isSelected = true)
                    CalendarDayCell(day = 12, dayType = DayType.FERTILE, isEnabled = false)
                    Text("Fertile", style = MaterialTheme.typography.bodySmall)
                }
                
                // Row 4: Ovulation days
                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    CalendarDayCell(day = 13, dayType = DayType.OVULATION)
                    CalendarDayCell(day = 14, dayType = DayType.OVULATION, isToday = true)
                    CalendarDayCell(day = 15, dayType = DayType.OVULATION, isSelected = true)
                    CalendarDayCell(day = 16, dayType = DayType.OVULATION, isEnabled = false)
                    Text("Ovulation", style = MaterialTheme.typography.bodySmall)
                }
            }
        }
    }
}

@Preview(name = "Dark Theme - Calendar Days")
@Composable
private fun CalendarDayCellDarkPreview() {
    PeriodCalendarTheme(darkTheme = true) {
        Surface {
            Column(
                modifier = Modifier.padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Text(
                    text = "Calendar Day Types",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
                
                // Row 1: Basic days
                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    CalendarDayCell(day = 1, dayType = DayType.DEFAULT)
                    CalendarDayCell(day = 2, isToday = true)
                    CalendarDayCell(day = 3, isSelected = true)
                    CalendarDayCell(day = 4, isEnabled = false)
                    Text("Basic", style = MaterialTheme.typography.bodySmall)
                }
                
                // Row 2: Period days
                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    CalendarDayCell(day = 5, dayType = DayType.PERIOD)
                    CalendarDayCell(day = 6, dayType = DayType.PERIOD, isToday = true)
                    CalendarDayCell(day = 7, dayType = DayType.PERIOD, isSelected = true)
                    CalendarDayCell(day = 8, dayType = DayType.PERIOD, isEnabled = false)
                    Text("Period", style = MaterialTheme.typography.bodySmall)
                }
                
                // Row 3: Fertile days
                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    CalendarDayCell(day = 9, dayType = DayType.FERTILE)
                    CalendarDayCell(day = 10, dayType = DayType.FERTILE, isToday = true)
                    CalendarDayCell(day = 11, dayType = DayType.FERTILE, isSelected = true)
                    CalendarDayCell(day = 12, dayType = DayType.FERTILE, isEnabled = false)
                    Text("Fertile", style = MaterialTheme.typography.bodySmall)
                }
                
                // Row 4: Ovulation days
                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    CalendarDayCell(day = 13, dayType = DayType.OVULATION)
                    CalendarDayCell(day = 14, dayType = DayType.OVULATION, isToday = true)
                    CalendarDayCell(day = 15, dayType = DayType.OVULATION, isSelected = true)
                    CalendarDayCell(day = 16, dayType = DayType.OVULATION, isEnabled = false)
                    Text("Ovulation", style = MaterialTheme.typography.bodySmall)
                }
            }
        }
    }
}

@Preview(name = "Calendar Grid Preview")
@Composable
private fun CalendarGridPreview() {
    PeriodCalendarTheme {
        Surface {
            Column(
                modifier = Modifier.padding(16.dp)
            ) {
                Text(
                    text = "March 2024",
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(bottom = 16.dp)
                )
                
                // Calendar grid simulation
                val days = listOf(
                    listOf("S", "M", "T", "W", "T", "F", "S"),
                    listOf("", "", "", "", "", "1", "2"),
                    listOf("3", "4", "5", "6", "7", "8", "9"),
                    listOf("10", "11", "12", "13", "14", "15", "16"),
                    listOf("17", "18", "19", "20", "21", "22", "23"),
                    listOf("24", "25", "26", "27", "28", "29", "30"),
                    listOf("31", "", "", "", "", "", "")
                )
                
                Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                    // Header
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceEvenly
                    ) {
                        days[0].forEach { dayHeader ->
                            Box(
                                modifier = Modifier.size(40.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = dayHeader,
                                    style = MaterialTheme.typography.labelMedium,
                                    fontWeight = FontWeight.SemiBold,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }
                    }
                    
                    // Calendar days
                    days.drop(1).forEach { week ->
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceEvenly
                        ) {
                            week.forEach { day ->
                                if (day.isNotEmpty()) {
                                    val dayInt = day.toInt()
                                    val dayType = when {
                                        dayInt in 5..9 -> DayType.PERIOD
                                        dayInt in 12..18 -> DayType.FERTILE
                                        dayInt == 15 -> DayType.OVULATION
                                        else -> DayType.DEFAULT
                                    }
                                    
                                    CalendarDayCell(
                                        day = dayInt,
                                        dayType = dayType,
                                        isToday = dayInt == 15,
                                        isSelected = dayInt == 8
                                    )
                                } else {
                                    Spacer(modifier = Modifier.size(40.dp))
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}