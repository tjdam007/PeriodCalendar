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
import com.mjandroiddev.periodcalendar.ui.navigation.PeriodCalendarNavigation
import com.mjandroiddev.periodcalendar.ui.theme.PeriodCalendarTheme
import com.mjandroiddev.periodcalendar.ui.viewmodel.SettingsViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            PeriodCalendarApp()
        }
    }
}

@Composable
private fun PeriodCalendarApp() {
    val settingsViewModel: SettingsViewModel = hiltViewModel()
    val userSettings by settingsViewModel.userSettings.collectAsStateWithLifecycle()
    val systemInDarkTheme = isSystemInDarkTheme()
    
    val darkTheme = when (ThemeMode.fromValue(userSettings.themeMode)) {
        ThemeMode.LIGHT -> false
        ThemeMode.DARK -> true
        ThemeMode.SYSTEM -> systemInDarkTheme
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