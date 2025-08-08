package com.mjandroiddev.periodcalendar.ui.screens

import android.Manifest
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.provider.Settings
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.filled.DarkMode
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.LightMode
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.NotificationsActive
import androidx.compose.material.icons.filled.NotificationsOff
import androidx.compose.material.icons.filled.Palette
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.SettingsBrightness
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.mjandroiddev.periodcalendar.data.model.ThemeMode
import com.mjandroiddev.periodcalendar.notifications.NotificationHelper
import com.mjandroiddev.periodcalendar.ui.components.CardWithTitle
import com.mjandroiddev.periodcalendar.ui.theme.PeriodCalendarTheme
import com.mjandroiddev.periodcalendar.ui.viewmodel.SettingsViewModel

// Parent Composable - Handles ViewModel and state management
@Composable
fun SettingsScreen(
    onNavigateBack: () -> Unit,
    onNavigateToAbout: (() -> Unit)? = null,
    onNavigateToSupport: (() -> Unit)? = null,
    onThemeChanged: (ThemeMode) -> Unit = {},
    viewModel: SettingsViewModel = hiltViewModel()
) {
    val userSettings by viewModel.userSettings.collectAsStateWithLifecycle()
    val isLoading by viewModel.isLoading.collectAsStateWithLifecycle()
    val saveMessage by viewModel.saveMessage.collectAsStateWithLifecycle()

    SettingsScreenContent(
        userSettings = userSettings,
        isLoading = isLoading,
        saveMessage = saveMessage,
        onCycleLengthChanged = viewModel::updateCycleLength,
        onPeriodDurationChanged = viewModel::updatePeriodDuration,
        onNotificationDaysChanged = viewModel::updateNotificationDays,
        onOvulationNotificationChanged = viewModel::updateOvulationNotification,
        onFertilityNotificationChanged = viewModel::updateFertileWindowNotification,
        onThemeChanged = { themeMode ->
            viewModel.updateThemeMode(themeMode)
            onThemeChanged(themeMode)
        },
        onResetToDefaults = viewModel::resetToDefaults,
        onNavigateBack = onNavigateBack,
        onNavigateToAbout = onNavigateToAbout,
        onNavigateToSupport = onNavigateToSupport,
        onClearSaveMessage = viewModel::clearSaveMessage
    )
}

// Child Composable - Handles UI rendering
@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun SettingsScreenContent(
    userSettings: com.mjandroiddev.periodcalendar.data.database.UserSettings,
    isLoading: Boolean,
    saveMessage: String?,
    onCycleLengthChanged: (Int) -> Unit,
    onPeriodDurationChanged: (Int) -> Unit,
    onNotificationDaysChanged: (Int) -> Unit,
    onOvulationNotificationChanged: (Boolean) -> Unit,
    onFertilityNotificationChanged: (Boolean) -> Unit,
    onThemeChanged: (ThemeMode) -> Unit,
    onResetToDefaults: () -> Unit,
    onNavigateBack: () -> Unit,
    onNavigateToAbout: (() -> Unit)?,
    onNavigateToSupport: (() -> Unit)?,
    onClearSaveMessage: () -> Unit
) {
    val context = LocalContext.current
    val notificationHelper = remember { NotificationHelper(context) }

    var showResetDialog by remember { mutableStateOf(false) }
    var showPermissionDialog by remember { mutableStateOf(false) }

    val notificationPermissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        if (!isGranted) {
            showPermissionDialog = true
        }
    }

    // Show save message as snackbar
    LaunchedEffect(saveMessage) {
        if (saveMessage != null) {
            kotlinx.coroutines.delay(2000)
            onClearSaveMessage()
        }
    }

    // Check notification permission on startup
    LaunchedEffect(Unit) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (!notificationHelper.areNotificationsEnabled()) {
                notificationPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
            }
        }
    }

    Column {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 8.dp, vertical = 10.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                IconButton(onClick = onNavigateBack) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = "Back"
                    )
                }
                Text(
                    text = "Settings",
                    style = MaterialTheme.typography.headlineMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                )
            }
            TextButton(
                onClick = { showResetDialog = true }
            ) {
                Text("Reset")
            }
        }


        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(20.dp)
        ) {
            // Cycle Settings Section
            CardWithTitle(
                title = "Cycle Settings",
                icon = Icons.Default.CalendarMonth
            ) {
                Column(
                    verticalArrangement = Arrangement.spacedBy(20.dp)
                ) {
                    // Average Cycle Length
                    NumberInputField(
                        label = "Average Cycle Length",
                        value = userSettings.avgCycleLength,
                        onValueChanged = onCycleLengthChanged,
                        suffix = "days",
                        range = 15..45,
                        helperText = "Typical range: 21-35 days"
                    )

                    // Period Duration
                    NumberInputField(
                        label = "Period Duration",
                        value = userSettings.periodDuration,
                        onValueChanged = onPeriodDurationChanged,
                        suffix = "days",
                        range = 1..10,
                        helperText = "How many days your period typically lasts"
                    )
                }
            }

            // Notification Settings Section
            CardWithTitle(
                title = "Notifications",
                icon = Icons.Default.Notifications
            ) {
                Column(
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    // Notification permission status
                    NotificationStatusCard(
                        notificationHelper = notificationHelper
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    // Days before period
                    NumberInputField(
                        label = "Remind me before period",
                        value = userSettings.notifBeforePeriod,
                        onValueChanged = onNotificationDaysChanged,
                        suffix = if (userSettings.notifBeforePeriod == 1) "day" else "days",
                        range = 0..7,
                        helperText = if (userSettings.notifBeforePeriod == 0) "Notifications disabled" else "Get notified before your period starts"
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    // Fertility Notifications
                    SettingsToggleItem(
                        title = "Ovulation Notification",
                        subtitle = "Get notified on predicted ovulation day",
                        checked = userSettings.notifOvulation,
                        onCheckedChange = onOvulationNotificationChanged,
                        icon = Icons.Default.FavoriteBorder
                    )

                    SettingsToggleItem(
                        title = "Fertile Window Notification",
                        subtitle = "Get notified when fertile window starts",
                        checked = userSettings.notifFertileWindow,
                        onCheckedChange = onFertilityNotificationChanged,
                        icon = Icons.Default.Favorite
                    )
                }
            }

            // Theme Settings Section
            CardWithTitle(
                title = "Appearance",
                icon = Icons.Default.Palette
            ) {
                Column {
                    Text(
                        text = "Theme Mode",
                        style = MaterialTheme.typography.labelLarge,
                        fontWeight = FontWeight.SemiBold,
                        modifier = Modifier.padding(bottom = 12.dp)
                    )

                    ThemeSelector(
                        selectedTheme = ThemeMode.fromValue(userSettings.themeMode),
                        onThemeChanged = onThemeChanged
                    )
                }
            }

            // Privacy Information Card
            Card(
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.secondaryContainer.copy(alpha = 0.5f)
                )
            ) {
                Row(
                    modifier = Modifier.padding(16.dp),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Security,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.onSecondaryContainer
                    )
                    Text(
                        text = "Your data is stored locally on your device. Notifications use discreet messages to protect your privacy.",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSecondaryContainer
                    )
                }
            }

            // Support Us Section
            if (onNavigateToSupport != null) {
                Card(
                    modifier = Modifier.clickable { onNavigateToSupport() },
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.1f)
                    ),
                    border = BorderStroke(
                        1.dp,
                        MaterialTheme.colorScheme.primary.copy(alpha = 0.3f)
                    )
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        horizontalArrangement = Arrangement.spacedBy(12.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .size(40.dp)
                                .clip(RoundedCornerShape(12.dp))
                                .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.2f)),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = "💝",
                                fontSize = 18.sp
                            )
                        }
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = "Support Development",
                                style = MaterialTheme.typography.bodyLarge,
                                fontWeight = FontWeight.Medium
                            )
                            Text(
                                text = "Help keep this app free and privacy-focused",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                        Icon(
                            imageVector = Icons.Default.ChevronRight,
                            contentDescription = "Navigate to Support",
                            tint = MaterialTheme.colorScheme.primary
                        )
                    }
                }
            }

            // About Section
            if (onNavigateToAbout != null) {
                Card(
                    modifier = Modifier.clickable { onNavigateToAbout() },
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.6f)
                    )
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        horizontalArrangement = Arrangement.spacedBy(12.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.Info,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.primary
                        )
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = "About Period Calendar",
                                style = MaterialTheme.typography.bodyLarge,
                                fontWeight = FontWeight.Medium
                            )
                            Text(
                                text = "Version 1.0.0 • Learn more about features and privacy",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                        Icon(
                            imageVector = Icons.Default.ChevronRight,
                            contentDescription = "Navigate to About",
                            tint = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }
        }

        // Loading overlay
        if (isLoading) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color.Black.copy(alpha = 0.3f)),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator()
            }
        }
    }

    // Reset Confirmation Dialog
    if (showResetDialog) {
        AlertDialog(
            onDismissRequest = { showResetDialog = false },
            title = { Text("Reset to Defaults") },
            text = {
                Text("This will reset all settings to their default values. This action cannot be undone.")
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        onResetToDefaults()
                        showResetDialog = false
                    }
                ) {
                    Text("Reset")
                }
            },
            dismissButton = {
                TextButton(onClick = { showResetDialog = false }) {
                    Text("Cancel")
                }
            }
        )
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
}

@Composable
private fun NotificationStatusCard(
    notificationHelper: NotificationHelper
) {
    val notificationsEnabled = notificationHelper.areNotificationsEnabled()
    val context = LocalContext.current

    Card(
        colors = CardDefaults.cardColors(
            containerColor = if (notificationsEnabled) {
                MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.6f)
            } else {
                MaterialTheme.colorScheme.errorContainer.copy(alpha = 0.6f)
            }
        )
    ) {
        Row(
            modifier = Modifier.padding(12.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
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
                },
                modifier = Modifier.size(16.dp)
            )

            Text(
                text = if (notificationsEnabled) "Notifications enabled" else "Notifications disabled",
                style = MaterialTheme.typography.bodySmall,
                color = if (notificationsEnabled) {
                    MaterialTheme.colorScheme.onPrimaryContainer
                } else {
                    MaterialTheme.colorScheme.onErrorContainer
                },
                modifier = Modifier.weight(1f)
            )

            if (!notificationsEnabled) {
                TextButton(
                    onClick = {
                        val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS).apply {
                            data = Uri.fromParts("package", context.packageName, null)
                        }
                        context.startActivity(intent)
                    }
                ) {
                    Text(
                        text = "Enable",
                        color = MaterialTheme.colorScheme.onErrorContainer,
                        style = MaterialTheme.typography.labelSmall
                    )
                }
            }
        }
    }
}

// Number Input Field Component
@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun NumberInputField(
    label: String,
    value: Int,
    onValueChanged: (Int) -> Unit,
    suffix: String,
    range: IntRange,
    helperText: String
) {
    var textValue by remember(value) { mutableStateOf(value.toString()) }
    var isError by remember { mutableStateOf(false) }

    Column {
        OutlinedTextField(
            value = textValue,
            onValueChange = { newValue ->
                textValue = newValue
                val intValue = newValue.toIntOrNull()
                if (intValue != null && intValue in range) {
                    onValueChanged(intValue)
                    isError = false
                } else {
                    isError = newValue.isNotEmpty()
                }
            },
            label = { Text(label) },
            suffix = { Text(suffix) },
            singleLine = true,
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
            isError = isError,
            supportingText = {
                Text(
                    text = if (isError) "Value must be between ${range.first} and ${range.last}" else helperText,
                    color = if (isError) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.onSurfaceVariant
                )
            },
            modifier = Modifier.fillMaxWidth()
        )
    }
}

// Settings Toggle Item Component
@Composable
private fun SettingsToggleItem(
    title: String,
    subtitle: String,
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit,
    icon: androidx.compose.ui.graphics.vector.ImageVector
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onCheckedChange(!checked) }
            .padding(vertical = 8.dp),
        horizontalArrangement = Arrangement.spacedBy(12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.size(20.dp)
        )

        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = title,
                style = MaterialTheme.typography.bodyLarge,
                fontWeight = FontWeight.Medium
            )
            Text(
                text = subtitle,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }

        Switch(
            checked = checked,
            onCheckedChange = onCheckedChange
        )
    }
}

// Theme Selector Component
@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun ThemeSelector(
    selectedTheme: ThemeMode,
    onThemeChanged: (ThemeMode) -> Unit
) {
    var expanded by remember { mutableStateOf(false) }

    ExposedDropdownMenuBox(
        expanded = expanded,
        onExpandedChange = { expanded = it },
        modifier = Modifier.fillMaxWidth()
    ) {
        OutlinedTextField(
            value = selectedTheme.displayName,
            onValueChange = { },
            readOnly = true,
            label = { Text("Theme") },
            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
            colors = ExposedDropdownMenuDefaults.outlinedTextFieldColors(),
            modifier = Modifier
                .menuAnchor()
                .fillMaxWidth()
        )

        ExposedDropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false }
        ) {
            ThemeMode.entries.forEach { theme ->
                DropdownMenuItem(
                    text = {
                        Row(
                            horizontalArrangement = Arrangement.spacedBy(12.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                imageVector = when (theme) {
                                    ThemeMode.LIGHT -> Icons.Default.LightMode
                                    ThemeMode.DARK -> Icons.Default.DarkMode
                                    ThemeMode.SYSTEM -> Icons.Default.SettingsBrightness
                                },
                                contentDescription = null,
                                modifier = Modifier.size(20.dp)
                            )
                            Column {
                                Text(theme.displayName)
                                Text(
                                    text = when (theme) {
                                        ThemeMode.LIGHT -> "Always use light theme"
                                        ThemeMode.DARK -> "Always use dark theme"
                                        ThemeMode.SYSTEM -> "Follow system setting"
                                    },
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }
                    },
                    onClick = {
                        onThemeChanged(theme)
                        expanded = false
                    },
                    contentPadding = ExposedDropdownMenuDefaults.ItemContentPadding
                )
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
                userSettings = com.mjandroiddev.periodcalendar.data.database.UserSettings(
                    avgCycleLength = 28,
                    periodDuration = 5,
                    notifBeforePeriod = 1,
                    notifOvulation = true,
                    notifFertileWindow = false,
                    themeMode = "system"
                ),
                isLoading = false,
                saveMessage = null,
                onCycleLengthChanged = { },
                onPeriodDurationChanged = { },
                onNotificationDaysChanged = { },
                onOvulationNotificationChanged = { },
                onFertilityNotificationChanged = { },
                onThemeChanged = { },
                onResetToDefaults = { },
                onNavigateBack = { },
                onNavigateToAbout = { },
                onNavigateToSupport = { },
                onClearSaveMessage = { }
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
                userSettings = com.mjandroiddev.periodcalendar.data.database.UserSettings(
                    avgCycleLength = 30,
                    periodDuration = 6,
                    notifBeforePeriod = 3,
                    notifOvulation = false,
                    notifFertileWindow = true,
                    themeMode = "dark"
                ),
                isLoading = false,
                saveMessage = "Settings saved",
                onCycleLengthChanged = { },
                onPeriodDurationChanged = { },
                onNotificationDaysChanged = { },
                onOvulationNotificationChanged = { },
                onFertilityNotificationChanged = { },
                onThemeChanged = { },
                onResetToDefaults = { },
                onNavigateBack = { },
                onNavigateToAbout = { },
                onNavigateToSupport = { },
                onClearSaveMessage = { }
            )
        }
    }
}