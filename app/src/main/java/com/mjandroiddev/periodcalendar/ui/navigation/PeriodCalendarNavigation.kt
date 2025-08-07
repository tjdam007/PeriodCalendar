package com.mjandroiddev.periodcalendar.ui.navigation

import androidx.compose.runtime.*
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.mjandroiddev.periodcalendar.data.model.ThemeMode
import com.mjandroiddev.periodcalendar.ui.screens.*
import java.time.LocalDate


@Composable
fun PeriodCalendarNavigation(
    onThemeChanged: (ThemeMode) -> Unit = {}
) {
    val navController = rememberNavController()
    
    NavHost(
        navController = navController,
        startDestination = Screen.Splash.route
    ) {
        // Splash Screen
        composable(Screen.Splash.route) {
            SplashScreen(
                onNavigateToHome = {
                    navController.navigate(Screen.Home.route) {
                        popUpTo(Screen.Splash.route) { inclusive = true }
                    }
                }
            )
        }
        
        // Home Screen (with bottom navigation)
        composable(Screen.Home.route) {
            HomeScreen(
                onNavigateToLogEntry = { date ->
                    navController.navigate(Screen.LogEntry.createRoute(date))
                },
                onNavigateToAbout = {
                    navController.navigate(Screen.About.route)
                },
                onNavigateToSupport = {
                    navController.navigate(Screen.SupportUs.route)
                }
            )
        }
        
        // Log Entry Screen with date argument
        composable(
            route = Screen.LogEntry.route,
            arguments = listOf(
                navArgument(Screen.LogEntry.DATE_ARG) {
                    type = NavType.StringType
                }
            )
        ) { backStackEntry ->
            val dateString = backStackEntry.arguments?.getString(Screen.LogEntry.DATE_ARG) 
                ?: LocalDate.now().toString()
            val selectedDate = try {
                LocalDate.parse(dateString)
            } catch (e: Exception) {
                LocalDate.now()
            }
            
            LogEntryScreen(
                selectedDate = selectedDate,
                onNavigateBack = {
                    navController.popBackStack()
                }
            )
        }
        
        // About Screen
        composable(Screen.About.route) {
            AboutScreen(
                onNavigateBack = {
                    navController.popBackStack()
                }
            )
        }
        
        // Support Us Screen
        composable(Screen.SupportUs.route) {
            SupportUsScreen(
                onNavigateBack = {
                    navController.popBackStack()
                }
            )
        }
    }
}