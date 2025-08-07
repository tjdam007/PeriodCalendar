package com.mjandroiddev.periodcalendar.firebase

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class NotificationClickReceiver : BroadcastReceiver() {
    
    @Inject
    lateinit var analyticsLogger: AnalyticsLogger
    
    override fun onReceive(context: Context?, intent: Intent?) {
        if (context == null || intent == null) return
        
        try {
            val title = intent.getStringExtra("title")
            val url = intent.getStringExtra("url")
            val browserIntent = intent.getParcelableExtra<Intent>("browser_intent")
            
            // Track notification click analytics
            analyticsLogger.trackNotificationClicked(title, url)
            
            // Launch browser with the URL
            if (browserIntent != null) {
                context.startActivity(browserIntent)
            }
        } catch (e: Exception) {
            // Log error but don't crash
            try {
                analyticsLogger.logError(e, "Error handling notification click")
            } catch (analyticsError: Exception) {
                // Fail silently if analytics fails
            }
        }
    }
}