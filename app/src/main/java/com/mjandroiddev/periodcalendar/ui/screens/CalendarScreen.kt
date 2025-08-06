package com.mjandroiddev.periodcalendar.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.mjandroiddev.periodcalendar.data.database.PeriodEntity
import com.mjandroiddev.periodcalendar.ui.components.CalendarDayCell
import com.mjandroiddev.periodcalendar.ui.components.CardWithTitle
import com.mjandroiddev.periodcalendar.ui.components.DayType
import com.mjandroiddev.periodcalendar.ui.components.PeriodButton
import com.mjandroiddev.periodcalendar.ui.theme.PeriodCalendarTheme
import com.mjandroiddev.periodcalendar.ui.viewmodel.CalendarViewModel
import java.time.LocalDate
import java.time.format.DateTimeFormatter

// Parent Composable - Handles ViewModel and state management
@Composable
fun CalendarScreen(
    viewModel: CalendarViewModel = hiltViewModel()
) {
    val periods by viewModel.periods.collectAsStateWithLifecycle()
    val selectedDate by viewModel.selectedDate.collectAsStateWithLifecycle()

    CalendarScreenContent(
        periods = periods,
        selectedDate = selectedDate,
        onDateSelected = viewModel::selectDate,
        onAddPeriod = { startDate, endDate, flow, symptoms, notes ->
            viewModel.addPeriod(startDate, endDate, flow, symptoms, notes)
        }
    )
}

// Child Composable - Handles UI rendering
@Composable
private fun CalendarScreenContent(
    periods: List<PeriodEntity>,
    selectedDate: LocalDate,
    onDateSelected: (LocalDate) -> Unit,
    onAddPeriod: (LocalDate, LocalDate?, Int, String, String) -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Header
        Text(
            text = "Period Calendar",
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onSurface
        )

        // Calendar Card
        CardWithTitle(
            title = selectedDate.format(DateTimeFormatter.ofPattern("MMMM yyyy")),
            icon = Icons.Default.CalendarMonth
        ) {
            CalendarGrid(
                currentMonth = selectedDate,
                periods = periods,
                onDateClick = onDateSelected
            )
        }

        // Selected Date Info
        SelectedDateInfo(
            selectedDate = selectedDate,
            periods = periods,
            onAddPeriod = onAddPeriod
        )
    }
}

@Composable
private fun CalendarGrid(
    currentMonth: LocalDate,
    periods: List<PeriodEntity>,
    onDateClick: (LocalDate) -> Unit
) {
    val firstOfMonth = currentMonth.withDayOfMonth(1)
    val daysInMonth = currentMonth.lengthOfMonth()
    val firstDayOfWeek = firstOfMonth.dayOfWeek.value % 7
    val today = LocalDate.now()

    LazyVerticalGrid(
        columns = GridCells.Fixed(7),
        horizontalArrangement = Arrangement.spacedBy(4.dp),
        verticalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        // Day headers
        items(listOf("S", "M", "T", "W", "T", "F", "S")) { dayHeader ->
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .padding(8.dp),
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

        // Empty cells for days before first day of month
        items(firstDayOfWeek) {
            Spacer(modifier = Modifier.size(40.dp))
        }

        // Days of the month
        items(daysInMonth) { day ->
            val date = firstOfMonth.withDayOfMonth(day + 1)
            val periodForDate = periods.find { period ->
                date >= period.startDate && (period.endDate == null || date <= period.endDate)
            }

            val dayType = when {
                periodForDate != null -> DayType.PERIOD
                // Add logic for fertile and ovulation days based on your business logic
                else -> DayType.DEFAULT
            }

            CalendarDayCell(
                day = day + 1,
                dayType = dayType,
                isToday = date == today,
                isSelected = date == currentMonth,
                onClick = { onDateClick(date) }
            )
        }
    }
}

@Composable
private fun SelectedDateInfo(
    selectedDate: LocalDate,
    periods: List<PeriodEntity>,
    onAddPeriod: (LocalDate, LocalDate?, Int, String, String) -> Unit
) {
    val periodForDate = periods.find { period ->
        selectedDate >= period.startDate && (period.endDate == null || selectedDate <= period.endDate)
    }

    CardWithTitle(
        title = "Selected Date",
        subtitle = selectedDate.format(DateTimeFormatter.ofPattern("MMM dd, yyyy")),
        icon = Icons.Default.CalendarMonth
    ) {
        if (periodForDate != null) {
            Column(
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = "Period Day",
                        style = MaterialTheme.typography.bodyLarge,
                        fontWeight = FontWeight.SemiBold,
                        color = MaterialTheme.colorScheme.error
                    )
                    
                    Text(
                        text = "Flow: ${periodForDate.flow}/5",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
                
                if (periodForDate.symptoms.isNotEmpty()) {
                    Text(
                        text = "Symptoms: ${periodForDate.symptoms}",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
                
                if (periodForDate.notes.isNotEmpty()) {
                    Text(
                        text = "Notes: ${periodForDate.notes}",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        } else {
            Column(
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Text(
                    text = "No period data for this date",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )

                PeriodButton(
                    text = "Mark as Period Day",
                    onClick = {
                        onAddPeriod(selectedDate, selectedDate, 3, "", "")
                    }
                )
            }
        }
    }
}

@Preview(name = "Light Theme")
@Composable
private fun CalendarScreenLightPreview() {
    PeriodCalendarTheme(darkTheme = false) {
        Surface {
            CalendarScreenContent(
                periods = listOf(
                    PeriodEntity(
                        id = 1,
                        startDate = LocalDate.now().minusDays(5),
                        endDate = LocalDate.now().minusDays(1),
                        flow = 3,
                        symptoms = "Cramps, headache",
                        notes = "Light flow"
                    )
                ),
                selectedDate = LocalDate.now(),
                onDateSelected = { },
                onAddPeriod = { _, _, _, _, _ -> }
            )
        }
    }
}

@Preview(name = "Dark Theme")
@Composable
private fun CalendarScreenDarkPreview() {
    PeriodCalendarTheme(darkTheme = true) {
        Surface {
            CalendarScreenContent(
                periods = listOf(
                    PeriodEntity(
                        id = 1,
                        startDate = LocalDate.now().minusDays(5),
                        endDate = LocalDate.now().minusDays(1),
                        flow = 3,
                        symptoms = "Cramps, headache",
                        notes = "Light flow"
                    )
                ),
                selectedDate = LocalDate.now(),
                onDateSelected = { },
                onAddPeriod = { _, _, _, _, _ -> }
            )
        }
    }
}