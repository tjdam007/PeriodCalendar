package com.mjandroiddev.periodcalendar.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Analytics
import androidx.compose.material.icons.filled.History
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.mjandroiddev.periodcalendar.data.database.CycleEntity
import com.mjandroiddev.periodcalendar.ui.components.CardWithTitle
import com.mjandroiddev.periodcalendar.ui.theme.PeriodCalendarTheme
import com.mjandroiddev.periodcalendar.ui.viewmodel.CycleViewModel
import java.time.LocalDate
import java.time.format.DateTimeFormatter

// Parent Composable - Handles ViewModel and state management
@Composable
fun CycleScreen(
    viewModel: CycleViewModel = hiltViewModel()
) {
    val cycles by viewModel.cycles.collectAsStateWithLifecycle()
    val averageCycleLength by viewModel.averageCycleLength.collectAsStateWithLifecycle()
    val averagePeriodLength by viewModel.averagePeriodLength.collectAsStateWithLifecycle()

    CycleScreenContent(
        cycles = cycles,
        averageCycleLength = averageCycleLength,
        averagePeriodLength = averagePeriodLength
    )
}

// Child Composable - Handles UI rendering
@Composable
private fun CycleScreenContent(
    cycles: List<CycleEntity>,
    averageCycleLength: Double?,
    averagePeriodLength: Double?
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Header
        Text(
            text = "Cycle Statistics",
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onSurface
        )

        // Statistics Card
        CardWithTitle(
            title = "Average Statistics",
            icon = Icons.Default.Analytics
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                StatisticItem(
                    title = "Avg Cycle",
                    value = "${averageCycleLength?.toInt() ?: "--"} days",
                    color = MaterialTheme.colorScheme.primary
                )
                
                StatisticItem(
                    title = "Avg Period",
                    value = "${averagePeriodLength?.toInt() ?: "--"} days",
                    color = MaterialTheme.colorScheme.secondary
                )
            }
        }

        // Cycle History
        CardWithTitle(
            title = "Cycle History",
            subtitle = "${cycles.size} recorded cycles",
            icon = Icons.Default.History
        ) {
            if (cycles.isEmpty()) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 24.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "No cycle data yet",
                        style = MaterialTheme.typography.bodyLarge,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            } else {
                LazyColumn(
                    verticalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier.heightIn(max = 400.dp)
                ) {
                    items(cycles) { cycle ->
                        CycleItem(cycle = cycle)
                    }
                }
            }
        }
    }
}

@Composable
private fun StatisticItem(
    title: String,
    value: String,
    color: androidx.compose.ui.graphics.Color
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = title,
            style = MaterialTheme.typography.labelMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            text = value,
            style = MaterialTheme.typography.headlineSmall,
            fontWeight = FontWeight.Bold,
            color = color
        )
    }
}

@Composable
private fun CycleItem(
    cycle: CycleEntity
) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
        shape = MaterialTheme.shapes.small
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(
                    text = cycle.startDate.format(DateTimeFormatter.ofPattern("MMM dd, yyyy")),
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.Medium,
                    color = MaterialTheme.colorScheme.onSurface
                )
                cycle.endDate?.let { endDate ->
                    Text(
                        text = "to ${endDate.format(DateTimeFormatter.ofPattern("MMM dd, yyyy"))}",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            Row(
                horizontalArrangement = Arrangement.spacedBy(16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "${cycle.cycleLength}",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary
                    )
                    Text(
                        text = "cycle",
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
                
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "${cycle.periodLength}",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.secondary
                    )
                    Text(
                        text = "period",
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }
    }
}

@Preview(name = "Light Theme")
@Composable
private fun CycleScreenLightPreview() {
    PeriodCalendarTheme(darkTheme = false) {
        Surface {
            CycleScreenContent(
                cycles = listOf(
                    CycleEntity(
                        id = 1,
                        startDate = LocalDate.now().minusDays(28),
                        endDate = LocalDate.now(),
                        cycleLength = 28,
                        periodLength = 5
                    ),
                    CycleEntity(
                        id = 2,
                        startDate = LocalDate.now().minusDays(56),
                        endDate = LocalDate.now().minusDays(28),
                        cycleLength = 30,
                        periodLength = 4
                    ),
                    CycleEntity(
                        id = 3,
                        startDate = LocalDate.now().minusDays(86),
                        endDate = LocalDate.now().minusDays(56),
                        cycleLength = 27,
                        periodLength = 6
                    )
                ),
                averageCycleLength = 28.3,
                averagePeriodLength = 5.0
            )
        }
    }
}

@Preview(name = "Dark Theme")
@Composable
private fun CycleScreenDarkPreview() {
    PeriodCalendarTheme(darkTheme = true) {
        Surface {
            CycleScreenContent(
                cycles = listOf(
                    CycleEntity(
                        id = 1,
                        startDate = LocalDate.now().minusDays(28),
                        endDate = LocalDate.now(),
                        cycleLength = 28,
                        periodLength = 5
                    ),
                    CycleEntity(
                        id = 2,
                        startDate = LocalDate.now().minusDays(56),
                        endDate = LocalDate.now().minusDays(28),
                        cycleLength = 30,
                        periodLength = 4
                    )
                ),
                averageCycleLength = 29.0,
                averagePeriodLength = 4.5
            )
        }
    }
}

@Preview(name = "Empty State")
@Composable
private fun CycleScreenEmptyPreview() {
    PeriodCalendarTheme {
        Surface {
            CycleScreenContent(
                cycles = emptyList(),
                averageCycleLength = null,
                averagePeriodLength = null
            )
        }
    }
}