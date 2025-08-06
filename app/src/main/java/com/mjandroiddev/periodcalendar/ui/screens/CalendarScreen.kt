package com.mjandroiddev.periodcalendar.ui.screens

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.mjandroiddev.periodcalendar.ui.viewmodel.CalendarViewModel
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.time.temporal.ChronoUnit

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CalendarScreen(
    viewModel: CalendarViewModel = hiltViewModel()
) {
    val periods by viewModel.periods.collectAsStateWithLifecycle()
    val selectedDate by viewModel.selectedDate.collectAsStateWithLifecycle()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Text(
            text = "Period Calendar",
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(bottom = 16.dp)
        )

        Card(
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(
                modifier = Modifier.padding(16.dp)
            ) {
                Text(
                    text = selectedDate.format(DateTimeFormatter.ofPattern("MMMM yyyy")),
                    style = MaterialTheme.typography.titleLarge,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(16.dp))

                // Calendar grid
                CalendarGrid(
                    currentMonth = selectedDate,
                    periods = periods,
                    onDateClick = { date ->
                        viewModel.selectDate(date)
                    }
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Period info for selected date
        SelectedDateInfo(
            selectedDate = selectedDate,
            periods = periods,
            onAddPeriod = { startDate, endDate, flow, symptoms, notes ->
                viewModel.addPeriod(startDate, endDate, flow, symptoms, notes)
            }
        )
    }
}

@Composable
fun CalendarGrid(
    currentMonth: LocalDate,
    periods: List<com.mjandroiddev.periodcalendar.data.database.PeriodEntity>,
    onDateClick: (LocalDate) -> Unit
) {
    val firstOfMonth = currentMonth.withDayOfMonth(1)
    val daysInMonth = currentMonth.lengthOfMonth()
    val firstDayOfWeek = firstOfMonth.dayOfWeek.value % 7

    LazyVerticalGrid(
        columns = GridCells.Fixed(7),
        horizontalArrangement = Arrangement.spacedBy(4.dp),
        verticalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        // Day headers
        items(listOf("S", "M", "T", "W", "T", "F", "S")) { dayHeader ->
            Text(
                text = dayHeader,
                style = MaterialTheme.typography.bodySmall,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(8.dp)
            )
        }

        // Empty cells for days before first day of month
        items(firstDayOfWeek) {
            Spacer(modifier = Modifier.size(40.dp))
        }

        // Days of the month
        items(daysInMonth) { day ->
            val date = firstOfMonth.withDayOfMonth(day + 1)
            val isPeriodDay = periods.any { period ->
                date >= period.startDate && (period.endDate == null || date <= period.endDate)
            }

            Surface(
                modifier = Modifier
                    .size(40.dp)
                    .clickable { onDateClick(date) },
                color = if (isPeriodDay) {
                    MaterialTheme.colorScheme.errorContainer
                } else {
                    MaterialTheme.colorScheme.surface
                },
                shape = MaterialTheme.shapes.small
            ) {
                Box(
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = (day + 1).toString(),
                        style = MaterialTheme.typography.bodyMedium,
                        color = if (isPeriodDay) {
                            MaterialTheme.colorScheme.onErrorContainer
                        } else {
                            MaterialTheme.colorScheme.onSurface
                        }
                    )
                }
            }
        }
    }
}

@Composable
fun SelectedDateInfo(
    selectedDate: LocalDate,
    periods: List<com.mjandroiddev.periodcalendar.data.database.PeriodEntity>,
    onAddPeriod: (LocalDate, LocalDate?, Int, String, String) -> Unit
) {
    val periodForDate = periods.find { period ->
        selectedDate >= period.startDate && (period.endDate == null || selectedDate <= period.endDate)
    }

    Card(
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Text(
                text = "Selected: ${selectedDate.format(DateTimeFormatter.ofPattern("MMM dd, yyyy"))}",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )

            Spacer(modifier = Modifier.height(8.dp))

            if (periodForDate != null) {
                Text(
                    text = "Period Day",
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.error
                )
                Text(
                    text = "Flow: ${periodForDate.flow}/5",
                    style = MaterialTheme.typography.bodyMedium
                )
                if (periodForDate.symptoms.isNotEmpty()) {
                    Text(
                        text = "Symptoms: ${periodForDate.symptoms}",
                        style = MaterialTheme.typography.bodyMedium
                    )
                }
                if (periodForDate.notes.isNotEmpty()) {
                    Text(
                        text = "Notes: ${periodForDate.notes}",
                        style = MaterialTheme.typography.bodyMedium
                    )
                }
            } else {
                Text(
                    text = "No period data for this date",
                    style = MaterialTheme.typography.bodyMedium
                )

                Spacer(modifier = Modifier.height(8.dp))

                Button(
                    onClick = {
                        onAddPeriod(selectedDate, selectedDate, 3, "", "")
                    }
                ) {
                    Text("Mark as Period Day")
                }
            }
        }
    }
}