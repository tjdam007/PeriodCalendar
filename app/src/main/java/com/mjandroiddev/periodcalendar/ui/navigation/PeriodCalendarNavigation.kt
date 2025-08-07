package com.mjandroiddev.periodcalendar.ui.navigation

import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.Analytics
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.mjandroiddev.periodcalendar.ui.screens.CalendarScreen
import com.mjandroiddev.periodcalendar.ui.screens.CycleScreen
import com.mjandroiddev.periodcalendar.ui.screens.LogEntryScreen
import java.time.LocalDate

data class BottomNavItem(
    val screen: Screen,
    val icon: ImageVector,
    val label: String
)

val bottomNavItems = listOf(
    BottomNavItem(Screen.Calendar, Icons.Filled.CalendarMonth, "Calendar"),
    BottomNavItem(Screen.Cycle, Icons.Filled.Analytics, "Cycles")
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PeriodCalendarNavigation() {
    val navController = rememberNavController()
    
    Scaffold(
        bottomBar = {
            NavigationBar {
                val navBackStackEntry by navController.currentBackStackEntryAsState()
                val currentDestination = navBackStackEntry?.destination

                bottomNavItems.forEach { item ->
                    NavigationBarItem(
                        icon = { Icon(item.icon, contentDescription = item.label) },
                        label = { Text(item.label) },
                        selected = currentDestination?.hierarchy?.any { it.route == item.screen.route } == true,
                        onClick = {
                            navController.navigate(item.screen.route) {
                                popUpTo(navController.graph.findStartDestination().id) {
                                    saveState = true
                                }
                                launchSingleTop = true
                                restoreState = true
                            }
                        }
                    )
                }
            }
        }
    ) { innerPadding ->
        NavHost(
            navController = navController,
            startDestination = Screen.Calendar.route,
            modifier = Modifier.padding(innerPadding)
        ) {
            composable(Screen.Calendar.route) {
                CalendarScreen(
                    onNavigateToLogEntry = { date ->
                        val dateString = date.toString()
                        navController.navigate(Screen.LogEntry.createRoute(dateString))
                    }
                )
            }
            composable(Screen.Cycle.route) {
                CycleScreen()
            }
            composable(Screen.LogEntry.route) { backStackEntry ->
                val dateString = backStackEntry.arguments?.getString("date") ?: LocalDate.now().toString()
                val selectedDate = LocalDate.parse(dateString)
                LogEntryScreen(
                    selectedDate = selectedDate,
                    onNavigateBack = {
                        navController.popBackStack()
                    }
                )
            }
        }
    }
}