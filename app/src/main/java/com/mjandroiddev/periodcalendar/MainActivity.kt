package com.mjandroiddev.periodcalendar

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.mjandroiddev.periodcalendar.data.model.ThemeMode
import com.mjandroiddev.periodcalendar.firebase.AnalyticsLogger
import com.mjandroiddev.periodcalendar.ui.components.RatingAndSupportDialogManager
import com.mjandroiddev.periodcalendar.ui.components.shouldShowRatingDialog
import com.mjandroiddev.periodcalendar.ui.navigation.PeriodCalendarNavigation
import com.mjandroiddev.periodcalendar.ui.theme.PeriodCalendarTheme
import com.mjandroiddev.periodcalendar.ui.viewmodel.SettingsViewModel
import javax.inject.Inject
import dagger.hilt.android.AndroidEntryPoint
import androidx.core.content.edit

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    @Inject
    lateinit var analyticsLogger: AnalyticsLogger

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        // Track app first open - check if this is first time opening
        val sharedPrefs = getSharedPreferences("app_prefs", MODE_PRIVATE)
        val isFirstOpen = sharedPrefs.getBoolean("is_first_open", true)

        if (isFirstOpen) {
            analyticsLogger.trackAppFirstOpen()
            sharedPrefs.edit { putBoolean("is_first_open", false) }
        }

        setContent {
            PeriodCalendarApp(analyticsLogger)
        }
    }
}

@Composable
private fun PeriodCalendarApp(analyticsLogger: AnalyticsLogger) {
    val context = LocalContext.current
    val settingsViewModel: SettingsViewModel = hiltViewModel()
    val userSettings by settingsViewModel.userSettings.collectAsStateWithLifecycle()
    val systemInDarkTheme = isSystemInDarkTheme()

    // Rating dialog state
    var showRatingDialog by remember { mutableStateOf(false) }
    var navigateToSupport by remember { mutableStateOf(false) }

    // Check if we should show the rating dialog
    LaunchedEffect(Unit) {
        kotlinx.coroutines.delay(3000) // Wait 3 seconds after app launch
        showRatingDialog = shouldShowRatingDialog(context)
    }

    val darkTheme = when (ThemeMode.fromValue(userSettings.themeMode)) {
        ThemeMode.LIGHT -> false
        ThemeMode.DARK -> true
        ThemeMode.SYSTEM -> systemInDarkTheme
    }

    // Set user preferences in Crashlytics when settings change
    LaunchedEffect(userSettings) {
        analyticsLogger.setUserPreferences(
            themeMode = userSettings.themeMode,
            notificationsEnabled = userSettings.notifBeforePeriod > 0 || userSettings.notifOvulation || userSettings.notifFertileWindow,
            avgCycleLength = userSettings.avgCycleLength
        )
    }

    PeriodCalendarTheme(darkTheme = darkTheme) {
        Surface(
            modifier = Modifier.fillMaxSize(),
            color = MaterialTheme.colorScheme.background
        ) {
            Box {
                PeriodCalendarNavigation(
                    onThemeChanged = { themeMode ->
                        settingsViewModel.updateThemeMode(themeMode)
                    }
                )

                // Rating and Support Dialog Manager
                RatingAndSupportDialogManager(
                    shouldShow = showRatingDialog,
                    onDialogShown = {
                        // Track that user interacted with rating dialog
                        analyticsLogger.logError(
                            RuntimeException("Rating Dialog Interaction"),
                            "User clicked rate app button"
                        )
                    },
                    onNavigateToSupport = {
                        navigateToSupport = true
                    },
                    onDismissDialog = {
                        showRatingDialog = false
                    }
                )
            }
        }
    }

    // Handle navigation to support screen
    if (navigateToSupport) {
        LaunchedEffect(Unit) {
            // This will be handled by the navigation in PeriodCalendarNavigation
            navigateToSupport = false
        }
    }
}