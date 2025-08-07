package com.mjandroiddev.periodcalendar

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.mjandroiddev.periodcalendar.data.model.ThemeMode
import com.mjandroiddev.periodcalendar.firebase.AnalyticsLogger
import com.mjandroiddev.periodcalendar.ui.navigation.PeriodCalendarNavigation
import com.mjandroiddev.periodcalendar.ui.theme.PeriodCalendarTheme
import com.mjandroiddev.periodcalendar.ui.viewmodel.SettingsViewModel
import javax.inject.Inject
import dagger.hilt.android.AndroidEntryPoint

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
            sharedPrefs.edit().putBoolean("is_first_open", false).apply()
        }
        
        setContent {
            PeriodCalendarApp(analyticsLogger)
        }
    }
}

@Composable
private fun PeriodCalendarApp(analyticsLogger: AnalyticsLogger) {
    val settingsViewModel: SettingsViewModel = hiltViewModel()
    val userSettings by settingsViewModel.userSettings.collectAsStateWithLifecycle()
    val systemInDarkTheme = isSystemInDarkTheme()
    
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
            PeriodCalendarNavigation(
                onThemeChanged = { themeMode ->
                    settingsViewModel.updateThemeMode(themeMode)
                }
            )
        }
    }
}