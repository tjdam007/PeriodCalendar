package com.mjandroiddev.periodcalendar.firebase

import com.google.firebase.analytics.FirebaseAnalytics
import com.google.firebase.crashlytics.FirebaseCrashlytics
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AnalyticsLogger @Inject constructor(
    private val firebaseAnalytics: FirebaseAnalytics,
    private val firebaseCrashlytics: FirebaseCrashlytics
) {
    
    companion object {
        // Onboarding & Setup Events
        const val APP_FIRST_OPEN = "app_first_open"
        const val ONBOARDING_COMPLETE = "onboarding_complete"
        
        // Core Usage Events
        const val PERIOD_LOGGED = "period_logged"
        const val SYMPTOM_LOGGED = "symptom_logged" 
        const val VIEW_CALENDAR_DAY = "view_calendar_day"
        
        // Engagement Events
        const val OPENED_REMINDER = "opened_reminder"
        const val NOTIFICATION_CLICKED = "notification_clicked"
        
        // Settings Events
        const val THEME_CHANGED = "theme_changed"
        const val NOTIFICATION_SETTING_CHANGED = "notification_setting_changed"
        
        // Parameter Keys
        const val PARAM_DATE = "date"
        const val PARAM_FLOW_LEVEL = "flow_level"
        const val PARAM_SYMPTOMS_SELECTED = "symptoms_selected"
        const val PARAM_SYMPTOM_TYPE = "symptom_type"
        const val PARAM_INTENSITY = "intensity"
        const val PARAM_CYCLE_DAY = "cycle_day"
        const val PARAM_DAY_TYPE = "day_type"
        const val PARAM_REMINDER_TYPE = "reminder_type"
        const val PARAM_TITLE = "title"
        const val PARAM_URL = "url"
        const val PARAM_THEME_MODE = "theme_mode"
        const val PARAM_SETTING_TYPE = "setting_type"
        const val PARAM_ENABLED = "enabled"
        
        // Day Types
        const val DAY_TYPE_PERIOD = "period"
        const val DAY_TYPE_FERTILE = "fertile" 
        const val DAY_TYPE_OVULATION = "ovulation"
        const val DAY_TYPE_NORMAL = "normal"
        
        // Reminder Types
        const val REMINDER_PERIOD = "period"
        const val REMINDER_FERTILE = "fertile"
        const val REMINDER_OVULATION = "ovulation"
        
        // Theme Modes
        const val THEME_LIGHT = "light"
        const val THEME_DARK = "dark"
        const val THEME_SYSTEM = "system"
    }
    
    /**
     * Track app first open
     */
    fun trackAppFirstOpen() {
        logEvent(APP_FIRST_OPEN)
        setCrashlyticsCustomKey("first_app_open", true)
    }
    
    /**
     * Track onboarding completion
     */
    fun trackOnboardingComplete() {
        logEvent(ONBOARDING_COMPLETE)
        setCrashlyticsCustomKey("onboarding_completed", true)
    }
    
    /**
     * Track when user logs a period day
     * @param date The date of the period entry
     * @param flowLevel The intensity of flow (Light, Medium, Heavy)
     * @param symptomsSelected List of symptoms selected
     */
    fun trackPeriodLogged(
        date: String,
        flowLevel: String,
        symptomsSelected: List<String>
    ) {
        logEvent(PERIOD_LOGGED) {
            param(PARAM_DATE, date)
            param(PARAM_FLOW_LEVEL, flowLevel)
            param(PARAM_SYMPTOMS_SELECTED, symptomsSelected.joinToString(","))
        }
        setCrashlyticsCustomKey("last_period_logged", date)
    }
    
    /**
     * Track symptom logging
     * @param symptomType Type of symptom (mood, cramps, etc.)
     * @param intensity Intensity level
     * @param cycleDay Day in current cycle
     */
    fun trackSymptomLogged(
        symptomType: String,
        intensity: String,
        cycleDay: Int
    ) {
        logEvent(SYMPTOM_LOGGED) {
            param(PARAM_SYMPTOM_TYPE, symptomType)
            param(PARAM_INTENSITY, intensity)
            param(PARAM_CYCLE_DAY, cycleDay.toString())
        }
    }
    
    /**
     * Track calendar day view
     * @param dayType Type of day (period, fertile, ovulation, normal)
     * @param date Date that was viewed
     */
    fun trackViewCalendarDay(dayType: String, date: String) {
        logEvent(VIEW_CALENDAR_DAY) {
            param(PARAM_DAY_TYPE, dayType)
            param(PARAM_DATE, date)
        }
        setCrashlyticsCustomKey("last_calendar_day_viewed", date)
    }
    
    /**
     * Track reminder notification opened
     * @param reminderType Type of reminder (period, fertile, ovulation)
     */
    fun trackReminderOpened(reminderType: String) {
        logEvent(OPENED_REMINDER) {
            param(PARAM_REMINDER_TYPE, reminderType)
        }
    }
    
    /**
     * Track FCM notification clicked
     * @param title Notification title
     * @param url URL that will be opened
     */
    fun trackNotificationClicked(title: String?, url: String?) {
        logEvent(NOTIFICATION_CLICKED) {
            param(PARAM_TITLE, title ?: "")
            param(PARAM_URL, url ?: "")
        }
    }
    
    /**
     * Track theme change
     * @param themeMode New theme mode (light, dark, system)
     */
    fun trackThemeChanged(themeMode: String) {
        logEvent(THEME_CHANGED) {
            param(PARAM_THEME_MODE, themeMode)
        }
        setCrashlyticsCustomKey("current_theme", themeMode)
    }
    
    /**
     * Track notification setting changes
     * @param settingType Type of notification setting changed
     * @param enabled Whether the setting was enabled or disabled
     */
    fun trackNotificationSettingChanged(settingType: String, enabled: Boolean) {
        logEvent(NOTIFICATION_SETTING_CHANGED) {
            param(PARAM_SETTING_TYPE, settingType)
            param(PARAM_ENABLED, enabled.toString())
        }
        setCrashlyticsCustomKey("notifications_enabled", enabled)
    }
    
    /**
     * Set user preferences in Crashlytics for context
     */
    fun setUserPreferences(
        themeMode: String,
        notificationsEnabled: Boolean,
        avgCycleLength: Int
    ) {
        setCrashlyticsCustomKey("user_theme", themeMode)
        setCrashlyticsCustomKey("notifications_enabled", notificationsEnabled)
        setCrashlyticsCustomKey("avg_cycle_length", avgCycleLength)
    }
    
    /**
     * Set current screen for crash context
     */
    fun setCurrentScreen(screenName: String) {
        setCrashlyticsCustomKey("current_screen", screenName)
    }
    
    /**
     * Log custom error with context
     */
    fun logError(throwable: Throwable, message: String? = null) {
        message?.let {
            firebaseCrashlytics.log(it)
        }
        firebaseCrashlytics.recordException(throwable)
    }
    
    /**
     * Helper function to log events with parameters
     */
    private fun logEvent(eventName: String, params: (ParameterBuilder.() -> Unit)? = null) {
        try {
            if (params != null) {
                val builder = ParameterBuilder()
                builder.params()
                firebaseAnalytics.logEvent(eventName, builder.bundle)
            } else {
                firebaseAnalytics.logEvent(eventName, null)
            }
        } catch (e: Exception) {
            // Log the error but don't crash the app
            firebaseCrashlytics.recordException(e)
        }
    }
    
    /**
     * Helper to set Crashlytics custom keys safely
     */
    private fun setCrashlyticsCustomKey(key: String, value: Any) {
        try {
            when (value) {
                is String -> firebaseCrashlytics.setCustomKey(key, value)
                is Boolean -> firebaseCrashlytics.setCustomKey(key, value)
                is Int -> firebaseCrashlytics.setCustomKey(key, value)
                is Long -> firebaseCrashlytics.setCustomKey(key, value)
                is Float -> firebaseCrashlytics.setCustomKey(key, value)
                is Double -> firebaseCrashlytics.setCustomKey(key, value)
            }
        } catch (e: Exception) {
            // Silently fail to avoid crashes
        }
    }
}

/**
 * Helper class to build event parameters
 */
private class ParameterBuilder {
    val bundle = android.os.Bundle()
    
    fun param(key: String, value: String) {
        bundle.putString(key, value)
    }
    
    fun param(key: String, value: Int) {
        bundle.putInt(key, value)
    }
    
    fun param(key: String, value: Long) {
        bundle.putLong(key, value)
    }
    
    fun param(key: String, value: Double) {
        bundle.putDouble(key, value)
    }
    
    fun param(key: String, value: Boolean) {
        bundle.putBoolean(key, value)
    }
}