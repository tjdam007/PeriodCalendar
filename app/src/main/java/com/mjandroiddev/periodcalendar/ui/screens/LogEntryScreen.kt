package com.mjandroiddev.periodcalendar.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.selection.selectableGroup
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.mjandroiddev.periodcalendar.data.database.CycleEntry
import com.mjandroiddev.periodcalendar.data.model.*
import com.mjandroiddev.periodcalendar.ui.components.CardWithTitle
import com.mjandroiddev.periodcalendar.ui.components.PeriodButton
import com.mjandroiddev.periodcalendar.ui.theme.PeriodCalendarTheme
import com.mjandroiddev.periodcalendar.ui.viewmodel.CalendarViewModel
import java.time.LocalDate
import java.time.format.DateTimeFormatter

// Parent Composable - Handles ViewModel and state management
@Composable
fun LogEntryScreen(
    selectedDate: LocalDate,
    onNavigateBack: () -> Unit,
    viewModel: CalendarViewModel = hiltViewModel()
) {
    val periods by viewModel.periods.collectAsStateWithLifecycle()
    val existingEntry = periods.find { it.date == selectedDate }
    
    LogEntryScreenContent(
        selectedDate = selectedDate,
        existingEntry = existingEntry,
        onSaveEntry = { entry ->
            if (existingEntry != null) {
                viewModel.updatePeriod(entry)
            } else {
                viewModel.addPeriod(entry)
            }
            onNavigateBack()
        },
        onDeleteEntry = { entry ->
            viewModel.deletePeriod(entry)
            onNavigateBack()
        },
        onNavigateBack = onNavigateBack
    )
}

// Child Composable - Handles UI rendering
@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun LogEntryScreenContent(
    selectedDate: LocalDate,
    existingEntry: CycleEntry?,
    onSaveEntry: (CycleEntry) -> Unit,
    onDeleteEntry: (CycleEntry) -> Unit,
    onNavigateBack: () -> Unit
) {
    var isPeriod by remember { mutableStateOf(existingEntry?.isPeriod ?: false) }
    var selectedFlow by remember { mutableStateOf(existingEntry?.getFlowLevelEnum() ?: FlowLevel.NONE) }
    var selectedMood by remember { mutableStateOf(existingEntry?.getMoodEnum() ?: Mood.NONE) }
    var selectedCramps by remember { mutableStateOf(existingEntry?.getCrampsLevelEnum() ?: CrampsLevel.NONE) }
    var showDeleteDialog by remember { mutableStateOf(false) }
    
    val hasChanges = remember(isPeriod, selectedFlow, selectedMood, selectedCramps, existingEntry) {
        existingEntry?.let { entry ->
            entry.isPeriod != isPeriod ||
            entry.getFlowLevelEnum() != selectedFlow ||
            entry.getMoodEnum() != selectedMood ||
            entry.getCrampsLevelEnum() != selectedCramps
        } ?: (isPeriod || selectedFlow != FlowLevel.NONE || selectedMood != Mood.NONE || selectedCramps != CrampsLevel.NONE)
    }
    
    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = selectedDate.format(DateTimeFormatter.ofPattern("MMMM dd, yyyy")),
                        fontWeight = FontWeight.Bold
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(
                            imageVector = Icons.Default.ArrowBack,
                            contentDescription = "Back"
                        )
                    }
                },
                actions = {
                    if (existingEntry != null) {
                        IconButton(onClick = { showDeleteDialog = true }) {
                            Icon(
                                imageVector = Icons.Default.Delete,
                                contentDescription = "Delete Entry",
                                tint = MaterialTheme.colorScheme.error
                            )
                        }
                    }
                }
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .verticalScroll(rememberScrollState())
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Period Toggle
            CardWithTitle(
                title = "Period Day",
                icon = Icons.Default.Bloodtype
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Mark as period day",
                        style = MaterialTheme.typography.bodyLarge
                    )
                    Switch(
                        checked = isPeriod,
                        onCheckedChange = { 
                            isPeriod = it
                            if (!it) {
                                selectedFlow = FlowLevel.NONE
                            }
                        }
                    )
                }
                
                if (isPeriod) {
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "Flow Level",
                        style = MaterialTheme.typography.labelLarge,
                        fontWeight = FontWeight.SemiBold
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    FlowLevelSelector(
                        selectedFlow = selectedFlow,
                        onFlowSelected = { selectedFlow = it }
                    )
                }
            }
            
            // Mood Section
            CardWithTitle(
                title = "Mood",
                icon = Icons.Default.Mood
            ) {
                MoodSelector(
                    selectedMood = selectedMood,
                    onMoodSelected = { selectedMood = it }
                )
            }
            
            // Symptoms Section
            CardWithTitle(
                title = "Symptoms",
                icon = Icons.Default.Healing
            ) {
                Text(
                    text = "Cramps Level",
                    style = MaterialTheme.typography.labelLarge,
                    fontWeight = FontWeight.SemiBold
                )
                Spacer(modifier = Modifier.height(8.dp))
                CrampsLevelSelector(
                    selectedCramps = selectedCramps,
                    onCrampsSelected = { selectedCramps = it }
                )
            }
            
            Spacer(modifier = Modifier.height(24.dp))
            
            // Save Button
            PeriodButton(
                text = if (existingEntry != null) "Update Entry" else "Save Entry",
                onClick = {
                    val entry = CycleEntry(
                        id = existingEntry?.id ?: 0,
                        date = selectedDate,
                        isPeriod = isPeriod,
                        flowLevel = selectedFlow.value,
                        mood = selectedMood.value,
                        cramps = selectedCramps.value
                    )
                    onSaveEntry(entry)
                },
                enabled = hasChanges,
                modifier = Modifier.fillMaxWidth()
            )
        }
    }
    
    // Delete Confirmation Dialog
    if (showDeleteDialog && existingEntry != null) {
        AlertDialog(
            onDismissRequest = { showDeleteDialog = false },
            title = {
                Text("Delete Entry")
            },
            text = {
                Text("Are you sure you want to delete this entry? This action cannot be undone.")
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        onDeleteEntry(existingEntry)
                        showDeleteDialog = false
                    },
                    colors = ButtonDefaults.textButtonColors(
                        contentColor = MaterialTheme.colorScheme.error
                    )
                ) {
                    Text("Delete")
                }
            },
            dismissButton = {
                TextButton(onClick = { showDeleteDialog = false }) {
                    Text("Cancel")
                }
            }
        )
    }
}

@Composable
private fun FlowLevelSelector(
    selectedFlow: FlowLevel,
    onFlowSelected: (FlowLevel) -> Unit
) {
    val flowLevels = FlowLevel.entries.filter { it != FlowLevel.NONE }
    
    Column(
        modifier = Modifier.selectableGroup()
    ) {
        flowLevels.forEach { flowLevel ->
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .selectable(
                        selected = selectedFlow == flowLevel,
                        onClick = { onFlowSelected(flowLevel) },
                        role = Role.RadioButton
                    )
                    .padding(vertical = 8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                RadioButton(
                    selected = selectedFlow == flowLevel,
                    onClick = null // Handled by selectable modifier
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = flowLevel.displayName,
                    style = MaterialTheme.typography.bodyLarge
                )
            }
        }
    }
}

@Composable
private fun MoodSelector(
    selectedMood: Mood,
    onMoodSelected: (Mood) -> Unit
) {
    Column {
        Text(
            text = "How are you feeling?",
            style = MaterialTheme.typography.labelLarge,
            fontWeight = FontWeight.SemiBold
        )
        Spacer(modifier = Modifier.height(8.dp))
        
        // Mood chips
        val moods = Mood.entries.filter { it != Mood.NONE }
        val chunkedMoods = moods.chunked(3) // 3 moods per row
        
        chunkedMoods.forEach { moodRow ->
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                moodRow.forEach { mood ->
                    FilterChip(
                        selected = selectedMood == mood,
                        onClick = { 
                            onMoodSelected(if (selectedMood == mood) Mood.NONE else mood)
                        },
                        label = {
                            Text(
                                text = "${mood.emoji} ${mood.displayName}".trim(),
                                style = MaterialTheme.typography.labelMedium
                            )
                        },
                        modifier = Modifier.weight(1f)
                    )
                }
                // Fill remaining space if last row has fewer items
                repeat(3 - moodRow.size) {
                    Spacer(modifier = Modifier.weight(1f))
                }
            }
            Spacer(modifier = Modifier.height(8.dp))
        }
    }
}

@Composable
private fun CrampsLevelSelector(
    selectedCramps: CrampsLevel,
    onCrampsSelected: (CrampsLevel) -> Unit
) {
    val crampsLevels = CrampsLevel.entries
    
    Column(
        modifier = Modifier.selectableGroup()
    ) {
        crampsLevels.forEach { crampsLevel ->
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .selectable(
                        selected = selectedCramps == crampsLevel,
                        onClick = { onCrampsSelected(crampsLevel) },
                        role = Role.RadioButton
                    )
                    .padding(vertical = 8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                RadioButton(
                    selected = selectedCramps == crampsLevel,
                    onClick = null // Handled by selectable modifier
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = crampsLevel.displayName,
                    style = MaterialTheme.typography.bodyLarge
                )
            }
        }
    }
}

@Preview(name = "Log Entry - Light Theme")
@Composable
private fun LogEntryScreenLightPreview() {
    PeriodCalendarTheme(darkTheme = false) {
        Surface {
            LogEntryScreenContent(
                selectedDate = LocalDate.now(),
                existingEntry = null,
                onSaveEntry = { },
                onDeleteEntry = { },
                onNavigateBack = { }
            )
        }
    }
}

@Preview(name = "Log Entry - Dark Theme")
@Composable
private fun LogEntryScreenDarkPreview() {
    PeriodCalendarTheme(darkTheme = true) {
        Surface {
            LogEntryScreenContent(
                selectedDate = LocalDate.now(),
                existingEntry = CycleEntry(
                    id = 1,
                    date = LocalDate.now(),
                    isPeriod = true,
                    flowLevel = FlowLevel.MEDIUM.value,
                    mood = Mood.TIRED.value,
                    cramps = CrampsLevel.MILD.value
                ),
                onSaveEntry = { },
                onDeleteEntry = { },
                onNavigateBack = { }
            )
        }
    }
}