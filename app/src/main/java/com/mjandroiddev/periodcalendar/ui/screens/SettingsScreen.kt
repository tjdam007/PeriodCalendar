package com.mjandroiddev.periodcalendar.ui.screens

import android.Manifest
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.provider.Settings
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.mjandroiddev.periodcalendar.notifications.NotificationHelper
import com.mjandroiddev.periodcalendar.ui.components.CardWithTitle
import com.mjandroiddev.periodcalendar.ui.theme.PeriodCalendarTheme
import com.mjandroiddev.periodcalendar.ui.viewmodel.CalendarViewModel

// Parent Composable - Handles ViewModel and state management
@Composable
fun SettingsScreen(
    onNavigateBack: () -> Unit,
    viewModel: CalendarViewModel = hiltViewModel()
) {
    val userSettings by viewModel.userSettings.collectAsStateWithLifecycle()
    val context = LocalContext.current
    
    SettingsScreenContent(
        periodReminderDays = userSettings.notifBeforePeriod,
        ovulationNotification = userSettings.notifOvulation,
        fertilityNotification = userSettings.notifFertileWindow,
        onPeriodReminderChanged = { days ->
            viewModel.updateNotificationSettings(
                notifBeforePeriod = days,
                notifOvulation = userSettings.notifOvulation,
                notifFertileWindow = userSettings.notifFertileWindow
            )
        },
        onOvulationNotificationChanged = { enabled ->
            viewModel.updateNotificationSettings(
                notifBeforePeriod = userSettings.notifBeforePeriod,
                notifOvulation = enabled,
                notifFertileWindow = userSettings.notifFertileWindow
            )
        },
        onFertilityNotificationChanged = { enabled ->
            viewModel.updateNotificationSettings(
                notifBeforePeriod = userSettings.notifBeforePeriod,
                notifOvulation = userSettings.notifOvulation,
                notifFertileWindow = enabled
            )
        },
        onNavigateBack = onNavigateBack
    )
}

// Child Composable - Handles UI rendering
@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun SettingsScreenContent(
    periodReminderDays: Int,
    ovulationNotification: Boolean,
    fertilityNotification: Boolean,
    onPeriodReminderChanged: (Int) -> Unit,
    onOvulationNotificationChanged: (Boolean) -> Unit,
    onFertilityNotificationChanged: (Boolean) -> Unit,
    onNavigateBack: () -> Unit
) {
    val context = LocalContext.current
    val notificationHelper = remember { NotificationHelper(context) }
    
    var showPermissionDialog by remember { mutableStateOf(false) }
    var showNotificationSettingsDialog by remember { mutableStateOf(false) }
    
    val notificationPermissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        if (!isGranted) {
            showPermissionDialog = true
        }
    }
    
    LaunchedEffect(Unit) {
        // Check and request notification permission on Android 13+
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (!notificationHelper.areNotificationsEnabled()) {
                notificationPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
            }
        }
    }
    
    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "Notification Settings",
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
            // Notification Status Card
            NotificationStatusCard(
                notificationHelper = notificationHelper,
                onOpenSettings = { showNotificationSettingsDialog = true }
            )
            
            // Period Reminder Settings
            CardWithTitle(
                title = "Period Reminders",
                icon = Icons.Default.Notifications
            ) {
                Column(
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    Text(
                        text = "Get notified before your period starts",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    
                    PeriodReminderSelector(
                        selectedDays = periodReminderDays,
                        onDaysChanged = onPeriodReminderChanged
                    )
                }
            }
            
            // Fertility Tracking Settings
            CardWithTitle(
                title = "Fertility Tracking",
                icon = Icons.Default.FavoriteBorder
            ) {
                Column(
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    // Fertile Window Notification
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = "Fertile Window",
                                style = MaterialTheme.typography.bodyLarge,
                                fontWeight = FontWeight.Medium
                            )
                            Text(
                                text = "Notify when fertile window starts",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                        Switch(
                            checked = fertilityNotification,
                            onCheckedChange = onFertilityNotificationChanged
                        )
                    }
                    
                    Divider()
                    
                    // Ovulation Notification
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = "Ovulation Day",
                                style = MaterialTheme.typography.bodyLarge,
                                fontWeight = FontWeight.Medium
                            )
                            Text(
                                text = "Notify on predicted ovulation day",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                        Switch(
                            checked = ovulationNotification,
                            onCheckedChange = onOvulationNotificationChanged
                        )
                    }
                }
            }
            
            // Information Card
            Card(
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.secondaryContainer
                )
            ) {
                Row(
                    modifier = Modifier.padding(16.dp),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Info,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.onSecondaryContainer
                    )
                    Text(
                        text = "Notifications use discreet messages to protect your privacy. They will only appear if you have period data to make predictions from.",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSecondaryContainer
                    )
                }
            }
        }
    }
    
    // Permission Dialog
    if (showPermissionDialog) {
        AlertDialog(
            onDismissRequest = { showPermissionDialog = false },
            title = { Text("Notification Permission") },
            text = { 
                Text("To receive period and fertility reminders, please enable notifications in your device settings.")
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS).apply {
                            data = Uri.fromParts("package", context.packageName, null)
                        }
                        context.startActivity(intent)
                        showPermissionDialog = false
                    }
                ) {
                    Text("Open Settings")
                }
            },
            dismissButton = {
                TextButton(onClick = { showPermissionDialog = false }) {
                    Text("Cancel")
                }
            }
        )
    }
    
    // Notification Settings Dialog
    if (showNotificationSettingsDialog) {
        AlertDialog(
            onDismissRequest = { showNotificationSettingsDialog = false },
            title = { Text("Notification Settings") },
            text = { 
                Text("You can manage notification preferences for different types of reminders in your device settings.")
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS).apply {
                            data = Uri.fromParts("package", context.packageName, null)
                        }
                        context.startActivity(intent)
                        showNotificationSettingsDialog = false
                    }
                ) {
                    Text("Open Settings")
                }
            },
            dismissButton = {
                TextButton(onClick = { showNotificationSettingsDialog = false }) {
                    Text("Cancel")
                }
            }
        )
    }
}

@Composable
private fun NotificationStatusCard(
    notificationHelper: NotificationHelper,
    onOpenSettings: () -> Unit
) {
    val notificationsEnabled = notificationHelper.areNotificationsEnabled()
    
    Card(
        colors = CardDefaults.cardColors(
            containerColor = if (notificationsEnabled) {
                MaterialTheme.colorScheme.primaryContainer
            } else {
                MaterialTheme.colorScheme.errorContainer
            }
        )
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = if (notificationsEnabled) {
                    Icons.Default.NotificationsActive
                } else {
                    Icons.Default.NotificationsOff
                },
                contentDescription = null,
                tint = if (notificationsEnabled) {
                    MaterialTheme.colorScheme.onPrimaryContainer
                } else {
                    MaterialTheme.colorScheme.onErrorContainer
                }
            )
            
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = if (notificationsEnabled) "Notifications Enabled" else "Notifications Disabled",
                    style = MaterialTheme.typography.bodyLarge,
                    fontWeight = FontWeight.Medium,
                    color = if (notificationsEnabled) {
                        MaterialTheme.colorScheme.onPrimaryContainer
                    } else {
                        MaterialTheme.colorScheme.onErrorContainer
                    }
                )
                Text(
                    text = if (notificationsEnabled) {
                        "Your reminder settings will work as configured"
                    } else {
                        "Enable notifications to receive reminders"
                    },
                    style = MaterialTheme.typography.bodySmall,
                    color = if (notificationsEnabled) {
                        MaterialTheme.colorScheme.onPrimaryContainer
                    } else {
                        MaterialTheme.colorScheme.onErrorContainer
                    }
                )
            }
            
            if (!notificationsEnabled) {
                TextButton(onClick = onOpenSettings) {
                    Text(
                        text = "Enable",
                        color = MaterialTheme.colorScheme.onErrorContainer
                    )
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun PeriodReminderSelector(
    selectedDays: Int,
    onDaysChanged: (Int) -> Unit
) {
    val dayOptions = listOf(0, 1, 2, 3, 5, 7)
    
    Column {
        Text(
            text = "Remind me:",
            style = MaterialTheme.typography.labelMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.padding(bottom = 8.dp)
        )
        
        SingleChoiceSegmentedButtonRow(
            modifier = Modifier.fillMaxWidth()
        ) {
            dayOptions.forEachIndexed { index, days ->
                SegmentedButton(
                    shape = SegmentedButtonDefaults.itemShape(
                        index = index,
                        count = dayOptions.size
                    ),
                    onClick = { onDaysChanged(days) },
                    selected = selectedDays == days,
                    colors = SegmentedButtonDefaults.colors(
                        activeContainerColor = MaterialTheme.colorScheme.primaryContainer,
                        activeContentColor = MaterialTheme.colorScheme.onPrimaryContainer
                    )
                ) {
                    Text(
                        text = when (days) {
                            0 -> "Off"
                            1 -> "1 day"
                            else -> "$days days"
                        },
                        style = MaterialTheme.typography.labelSmall
                    )
                }
            }
        }
    }
}

@Preview(name = "Settings Screen - Light")
@Composable
private fun SettingsScreenLightPreview() {
    PeriodCalendarTheme(darkTheme = false) {
        Surface {
            SettingsScreenContent(
                periodReminderDays = 1,
                ovulationNotification = true,
                fertilityNotification = false,
                onPeriodReminderChanged = { },
                onOvulationNotificationChanged = { },
                onFertilityNotificationChanged = { },
                onNavigateBack = { }
            )
        }
    }
}

@Preview(name = "Settings Screen - Dark")
@Composable
private fun SettingsScreenDarkPreview() {
    PeriodCalendarTheme(darkTheme = true) {
        Surface {
            SettingsScreenContent(
                periodReminderDays = 3,
                ovulationNotification = false,
                fertilityNotification = true,
                onPeriodReminderChanged = { },
                onOvulationNotificationChanged = { },
                onFertilityNotificationChanged = { },
                onNavigateBack = { }
            )
        }
    }
}