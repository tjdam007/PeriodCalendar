package com.mjandroiddev.periodcalendar.ui.navigation

sealed class Screen(val route: String) {
    object Calendar : Screen("calendar")
    object Cycle : Screen("cycle")
}