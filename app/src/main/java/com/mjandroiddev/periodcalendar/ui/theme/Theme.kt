package com.mjandroiddev.periodcalendar.ui.theme

import android.app.Activity
import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat

private val PeriodCalendarDarkColorScheme = darkColorScheme(
    primary = PeriodRed80,
    onPrimary = Neutral10,
    primaryContainer = PeriodRed40,
    onPrimaryContainer = PeriodRed80,
    inversePrimary = PeriodRed40,
    secondary = FertileGreen80,
    onSecondary = Neutral10,
    secondaryContainer = FertileGreen40,
    onSecondaryContainer = FertileGreen80,
    tertiary = OvulationPurple80,
    onTertiary = Neutral10,
    tertiaryContainer = OvulationPurple40,
    onTertiaryContainer = OvulationPurple80,
    background = Neutral10,
    onBackground = Neutral90,
    surface = Neutral10,
    onSurface = Neutral90,
    surfaceVariant = Neutral30,
    onSurfaceVariant = Neutral80,
    surfaceTint = PeriodRed80,
    inverseSurface = Neutral90,
    inverseOnSurface = Neutral10,
    error = Error80,
    onError = Neutral10,
    errorContainer = Error40,
    onErrorContainer = Error80,
    outline = Neutral60,
    outlineVariant = Neutral30,
    scrim = Neutral0
)

private val PeriodCalendarLightColorScheme = lightColorScheme(
    primary = PeriodRed40,
    onPrimary = Color.White,
    primaryContainer = PeriodRed80,
    onPrimaryContainer = PeriodRed40,
    inversePrimary = PeriodRed80,
    secondary = FertileGreen40,
    onSecondary = Color.White,
    secondaryContainer = FertileGreen80,
    onSecondaryContainer = FertileGreen40,
    tertiary = OvulationPurple40,
    onTertiary = Color.White,
    tertiaryContainer = OvulationPurple80,
    onTertiaryContainer = OvulationPurple40,
    background = Neutral95,
    onBackground = Neutral10,
    surface = Neutral95,
    onSurface = Neutral10,
    surfaceVariant = Neutral90,
    onSurfaceVariant = Neutral30,
    surfaceTint = PeriodRed40,
    inverseSurface = Neutral20,
    inverseOnSurface = Neutral95,
    error = Error40,
    onError = Color.White,
    errorContainer = Error80,
    onErrorContainer = Error40,
    outline = Neutral50,
    outlineVariant = Neutral80,
    scrim = Neutral0
)

@Composable
fun PeriodCalendarTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    dynamicColor: Boolean = true,
    content: @Composable () -> Unit
) {
    val colorScheme = when {
        dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
            val context = LocalContext.current
            if (darkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
        }
        darkTheme -> PeriodCalendarDarkColorScheme
        else -> PeriodCalendarLightColorScheme
    }
    
    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as Activity).window
            window.statusBarColor = colorScheme.surface.toArgb()
            WindowCompat.getInsetsController(window, view).isAppearanceLightStatusBars = !darkTheme
        }
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}