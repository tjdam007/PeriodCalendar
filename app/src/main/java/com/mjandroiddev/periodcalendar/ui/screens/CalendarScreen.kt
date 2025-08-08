package com.mjandroiddev.periodcalendar.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.mjandroiddev.periodcalendar.data.database.CycleEntry
import com.mjandroiddev.periodcalendar.data.database.UserSettings
import com.mjandroiddev.periodcalendar.data.model.CrampLevel
import com.mjandroiddev.periodcalendar.data.model.FlowLevel
import com.mjandroiddev.periodcalendar.data.model.MoodType
import com.mjandroiddev.periodcalendar.ui.components.MonthlyCalendar
import com.mjandroiddev.periodcalendar.ui.theme.PeriodCalendarTheme
import com.mjandroiddev.periodcalendar.ui.viewmodel.CalendarViewModel
import java.time.LocalDate
import java.time.YearMonth

// Parent Composable - Handles ViewModel and state management
@Composable
fun CalendarScreen(
    onNavigateToLogEntry: (LocalDate) -> Unit,
    viewModel: CalendarViewModel = hiltViewModel()
) {
    val periods by viewModel.periods.collectAsStateWithLifecycle()
    val userSettings by viewModel.userSettings.collectAsStateWithLifecycle()

    CalendarScreenContent(
        periods = periods,
        userSettings = userSettings,
        onDateClick = onNavigateToLogEntry
    )
}

// Child Composable - Handles UI rendering
@Composable
private fun CalendarScreenContent(
    periods: List<CycleEntry>,
    userSettings: UserSettings,
    onDateClick: (LocalDate) -> Unit
) {
    var currentMonth by remember { mutableStateOf(YearMonth.now()) }
    var selectedDate by remember { mutableStateOf<LocalDate?>(null) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Header
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Image(
                painter = painterResource(com.mjandroiddev.periodcalendar.R.drawable.splash_icon),
                modifier = Modifier
                    .size(30.dp),
                contentDescription = "Back"
            )
            Text(
                text = "Period Calendar",
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface
            )
        }


        // Monthly Calendar with all the features
        MonthlyCalendar(
            currentMonth = currentMonth,
            selectedDate = selectedDate,
            cycleEntries = periods,
            userSettings = userSettings,
            onDateClick = { date ->
                selectedDate = date
                onDateClick(date)
            },
            onMonthChange = { newMonth ->
                currentMonth = newMonth
            }
        )
    }
}


@Preview(name = "Calendar Screen - Light Theme")
@Composable
private fun CalendarScreenLightPreview() {
    PeriodCalendarTheme(darkTheme = false) {
        Surface {
            CalendarScreenContent(
                periods = listOf(
                    CycleEntry(
                        id = 1,
                        date = LocalDate.now().minusDays(5),
                        isPeriod = true,
                        flowLevel = FlowLevel.MEDIUM,
                        mood = MoodType.TIRED,
                        cramps = CrampLevel.MILD
                    ),
                    CycleEntry(
                        id = 2,
                        date = LocalDate.now().minusDays(4),
                        isPeriod = true,
                        flowLevel = FlowLevel.HEAVY,
                        mood = MoodType.SAD,
                        cramps = CrampLevel.MODERATE
                    )
                ),
                userSettings = UserSettings(),
                onDateClick = { }
            )
        }
    }
}

@Preview(name = "Calendar Screen - Dark Theme")
@Composable
private fun CalendarScreenDarkPreview() {
    PeriodCalendarTheme(darkTheme = true) {
        Surface {
            CalendarScreenContent(
                periods = listOf(
                    CycleEntry(
                        id = 1,
                        date = LocalDate.now().minusDays(3),
                        isPeriod = true,
                        flowLevel = FlowLevel.LIGHT,
                        mood = MoodType.HAPPY,
                        cramps = null
                    ),
                    CycleEntry(
                        id = 2,
                        date = LocalDate.now().plusDays(2),
                        isPeriod = false,
                        flowLevel = null,
                        mood = MoodType.ENERGETIC,
                        cramps = null
                    )
                ),
                userSettings = UserSettings(avgCycleLength = 30, periodDuration = 6),
                onDateClick = { }
            )
        }
    }
}