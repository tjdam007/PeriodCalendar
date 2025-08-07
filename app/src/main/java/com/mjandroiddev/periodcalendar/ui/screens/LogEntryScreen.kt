package com.mjandroiddev.periodcalendar.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
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
            }
            
            // Flow Level Section (only show if period day is selected)
            if (isPeriod) {
                CardWithTitle(
                    title = "Flow Level",
                    icon = Icons.Default.Opacity
                ) {
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
                CrampsLevelSelector(
                    selectedCramps = selectedCramps,
                    onCrampsSelected = { selectedCramps = it }
                )
            }
            
            Spacer(modifier = Modifier.height(24.dp))
            
            // Save Button
            Button(
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
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    contentColor = MaterialTheme.colorScheme.onPrimary,
                    disabledContainerColor = MaterialTheme.colorScheme.surfaceVariant,
                    disabledContentColor = MaterialTheme.colorScheme.onSurfaceVariant
                )
            ) {
                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.Save,
                        contentDescription = null,
                        modifier = Modifier.size(20.dp)
                    )
                    Text(
                        text = if (existingEntry != null) "Update Entry" else "Save Entry",
                        style = MaterialTheme.typography.labelLarge,
                        fontWeight = FontWeight.Medium
                    )
                }
            }
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

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun FlowLevelSelector(
    selectedFlow: FlowLevel,
    onFlowSelected: (FlowLevel) -> Unit
) {
    val flowLevels = listOf(FlowLevel.LIGHT, FlowLevel.MEDIUM, FlowLevel.HEAVY)
    
    Column {
        Text(
            text = "Flow Level",
            style = MaterialTheme.typography.labelLarge,
            fontWeight = FontWeight.SemiBold,
            modifier = Modifier.padding(bottom = 12.dp)
        )
        
        SingleChoiceSegmentedButtonRow(
            modifier = Modifier.fillMaxWidth()
        ) {
            flowLevels.forEachIndexed { index, flowLevel ->
                SegmentedButton(
                    shape = SegmentedButtonDefaults.itemShape(
                        index = index,
                        count = flowLevels.size
                    ),
                    onClick = { onFlowSelected(flowLevel) },
                    selected = selectedFlow == flowLevel,
                    colors = SegmentedButtonDefaults.colors(
                        activeContainerColor = MaterialTheme.colorScheme.primaryContainer,
                        activeContentColor = MaterialTheme.colorScheme.onPrimaryContainer
                    )
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier.padding(vertical = 4.dp)
                    ) {
                        Icon(
                            imageVector = when (flowLevel) {
                                FlowLevel.LIGHT -> Icons.Default.WaterDrop
                                FlowLevel.MEDIUM -> Icons.Default.Opacity
                                FlowLevel.HEAVY -> Icons.Default.Bloodtype
                                else -> Icons.Default.Circle
                            },
                            contentDescription = flowLevel.displayName,
                            modifier = Modifier.size(20.dp)
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = flowLevel.displayName,
                            style = MaterialTheme.typography.labelSmall,
                            textAlign = TextAlign.Center
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun MoodSelector(
    selectedMood: Mood,
    onMoodSelected: (Mood) -> Unit
) {
    val targetMoods = listOf(Mood.HAPPY, Mood.SAD, Mood.ANGRY, Mood.ANXIOUS)
    
    Column {
        Text(
            text = "How are you feeling?",
            style = MaterialTheme.typography.labelLarge,
            fontWeight = FontWeight.SemiBold,
            modifier = Modifier.padding(bottom = 12.dp)
        )
        
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            targetMoods.forEach { mood ->
                MoodCard(
                    mood = mood,
                    isSelected = selectedMood == mood,
                    onClick = { 
                        onMoodSelected(if (selectedMood == mood) Mood.NONE else mood)
                    },
                    modifier = Modifier.weight(1f)
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun MoodCard(
    mood: Mood,
    isSelected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        onClick = onClick,
        modifier = modifier.height(80.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (isSelected) {
                MaterialTheme.colorScheme.primaryContainer
            } else {
                MaterialTheme.colorScheme.surfaceVariant
            }
        ),
        border = if (isSelected) {
            BorderStroke(2.dp, MaterialTheme.colorScheme.primary)
        } else null
    ) {
        Column(
            modifier = Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Icon(
                imageVector = when (mood) {
                    Mood.HAPPY -> Icons.Default.SentimentVerySatisfied
                    Mood.SAD -> Icons.Default.SentimentDissatisfied
                    Mood.ANGRY -> Icons.Default.SentimentVeryDissatisfied
                    Mood.ANXIOUS -> Icons.Default.Psychology
                    else -> Icons.Default.Mood
                },
                contentDescription = mood.displayName,
                modifier = Modifier.size(24.dp),
                tint = if (isSelected) {
                    MaterialTheme.colorScheme.onPrimaryContainer
                } else {
                    MaterialTheme.colorScheme.onSurfaceVariant
                }
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = mood.displayName,
                style = MaterialTheme.typography.labelSmall,
                textAlign = TextAlign.Center,
                color = if (isSelected) {
                    MaterialTheme.colorScheme.onPrimaryContainer
                } else {
                    MaterialTheme.colorScheme.onSurfaceVariant
                }
            )
        }
    }
}

@Composable
private fun CrampsLevelSelector(
    selectedCramps: CrampsLevel,
    onCrampsSelected: (CrampsLevel) -> Unit
) {
    val crampsLevels = CrampsLevel.entries
    
    Column {
        Text(
            text = "Cramps Level",
            style = MaterialTheme.typography.labelLarge,
            fontWeight = FontWeight.SemiBold,
            modifier = Modifier.padding(bottom = 12.dp)
        )
        
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            crampsLevels.forEach { crampsLevel ->
                CrampsButton(
                    crampsLevel = crampsLevel,
                    isSelected = selectedCramps == crampsLevel,
                    onClick = { onCrampsSelected(crampsLevel) },
                    modifier = Modifier.weight(1f)
                )
            }
        }
    }
}

@Composable
private fun CrampsButton(
    crampsLevel: CrampsLevel,
    isSelected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    OutlinedButton(
        onClick = onClick,
        modifier = modifier.height(48.dp),
        colors = ButtonDefaults.outlinedButtonColors(
            containerColor = if (isSelected) {
                MaterialTheme.colorScheme.primaryContainer
            } else {
                Color.Transparent
            },
            contentColor = if (isSelected) {
                MaterialTheme.colorScheme.onPrimaryContainer
            } else {
                MaterialTheme.colorScheme.onSurface
            }
        ),
        border = BorderStroke(
            width = if (isSelected) 2.dp else 1.dp,
            color = if (isSelected) {
                MaterialTheme.colorScheme.primary
            } else {
                MaterialTheme.colorScheme.outline
            }
        )
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Visual indicator for severity
            Row(
                horizontalArrangement = Arrangement.spacedBy(2.dp)
            ) {
                val dotsCount = when (crampsLevel) {
                    CrampsLevel.NONE -> 0
                    CrampsLevel.MILD -> 1
                    CrampsLevel.MODERATE -> 2
                    CrampsLevel.SEVERE -> 3
                }
                
                repeat(3) { index ->
                    Box(
                        modifier = Modifier
                            .size(6.dp)
                            .background(
                                color = if (index < dotsCount) {
                                    if (isSelected) {
                                        MaterialTheme.colorScheme.onPrimaryContainer
                                    } else {
                                        when (crampsLevel) {
                                            CrampsLevel.MILD -> Color(0xFF4CAF50)
                                            CrampsLevel.MODERATE -> Color(0xFFFF9800)
                                            CrampsLevel.SEVERE -> Color(0xFFFF5722)
                                            else -> Color.Transparent
                                        }
                                    }
                                } else {
                                    MaterialTheme.colorScheme.outline.copy(alpha = 0.3f)
                                },
                                shape = CircleShape
                            )
                    )
                }
            }
            
            Spacer(modifier = Modifier.height(4.dp))
            
            Text(
                text = crampsLevel.displayName,
                style = MaterialTheme.typography.labelSmall,
                textAlign = TextAlign.Center,
                maxLines = 1
            )
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

@Preview(name = "Log Entry - Dark Theme - Existing Entry")
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
                    mood = Mood.HAPPY.value,
                    cramps = CrampsLevel.MODERATE.value
                ),
                onSaveEntry = { },
                onDeleteEntry = { },
                onNavigateBack = { }
            )
        }
    }
}

@Preview(name = "Log Entry - New Entry Preview")
@Composable
private fun LogEntryScreenNewEntryPreview() {
    PeriodCalendarTheme(darkTheme = false) {
        Surface {
            LogEntryScreenContent(
                selectedDate = LocalDate.now().plusDays(1),
                existingEntry = null,
                onSaveEntry = { },
                onDeleteEntry = { },
                onNavigateBack = { }
            )
        }
    }
}