package com.mjandroiddev.periodcalendar.ui.navigation

import java.time.LocalDate

sealed class Screen(val route: String) {
    // Main navigation screens
    object Splash : Screen("splash")
    object Home : Screen("home") // This will be the calendar with bottom nav
    
    // Bottom navigation tabs
    object Calendar : Screen("calendar")
    object Cycle : Screen("cycle") 
    object Settings : Screen("settings")
    
    // Detail screens
    object LogEntry : Screen("log_entry/{date}") {
        const val DATE_ARG = "date"
        fun createRoute(date: LocalDate): String = "log_entry/${date}"
        fun createRoute(dateString: String): String = "log_entry/$dateString"
    }
    
    object About : Screen("about")
    
    companion object {
        // Route patterns for navigation matching
        const val SPLASH_ROUTE = "splash"
        const val HOME_ROUTE = "home"
        const val CALENDAR_ROUTE = "calendar"
        const val CYCLE_ROUTE = "cycle"
        const val SETTINGS_ROUTE = "settings"
        const val LOG_ENTRY_ROUTE = "log_entry/{date}"
        const val ABOUT_ROUTE = "about"
    }
}