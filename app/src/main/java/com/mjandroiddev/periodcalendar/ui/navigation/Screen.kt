package com.mjandroiddev.periodcalendar.ui.navigation

sealed class Screen(val route: String) {
    object Calendar : Screen("calendar")
    object Cycle : Screen("cycle")
    object LogEntry : Screen("log_entry/{date}") {
        fun createRoute(date: String): String = "log_entry/$date"
    }
}