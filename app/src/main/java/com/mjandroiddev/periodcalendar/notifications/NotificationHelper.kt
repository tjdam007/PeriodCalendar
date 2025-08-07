package com.mjandroiddev.periodcalendar.notifications

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.core.app.NotificationCompat
import com.mjandroiddev.periodcalendar.MainActivity
import com.mjandroiddev.periodcalendar.R
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class NotificationHelper @Inject constructor(
    private val context: Context
) {
    private val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
    
    companion object {
        const val PERIOD_CHANNEL_ID = "period_notifications"
        const val FERTILITY_CHANNEL_ID = "fertility_notifications"
        const val OVULATION_CHANNEL_ID = "ovulation_notifications"
        
        // Notification IDs
        const val PERIOD_REMINDER_ID = 1001
        const val FERTILE_WINDOW_ID = 1002
        const val OVULATION_ID = 1003
        
        // Request codes for pending intents
        const val PERIOD_REQUEST_CODE = 2001
        const val FERTILE_REQUEST_CODE = 2002
        const val OVULATION_REQUEST_CODE = 2003
    }
    
    init {
        createNotificationChannels()
    }
    
    private fun createNotificationChannels() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            // Period notifications channel
            val periodChannel = NotificationChannel(
                PERIOD_CHANNEL_ID,
                "Period Reminders",
                NotificationManager.IMPORTANCE_DEFAULT
            ).apply {
                description = "Notifications about upcoming periods"
                enableVibration(true)
                setShowBadge(true)
            }
            
            // Fertility notifications channel
            val fertilityChannel = NotificationChannel(
                FERTILITY_CHANNEL_ID,
                "Fertility Tracking",
                NotificationManager.IMPORTANCE_DEFAULT
            ).apply {
                description = "Notifications about fertile windows"
                enableVibration(true)
                setShowBadge(true)
            }
            
            // Ovulation notifications channel
            val ovulationChannel = NotificationChannel(
                OVULATION_CHANNEL_ID,
                "Ovulation Tracking",
                NotificationManager.IMPORTANCE_DEFAULT
            ).apply {
                description = "Notifications about ovulation"
                enableVibration(true)
                setShowBadge(true)
            }
            
            notificationManager.createNotificationChannels(
                listOf(periodChannel, fertilityChannel, ovulationChannel)
            )
        }
    }
    
    fun showPeriodReminderNotification(daysUntil: Int) {
        val intent = Intent(context, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        }
        
        val pendingIntent = PendingIntent.getActivity(
            context,
            PERIOD_REQUEST_CODE,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )
        
        val notification = NotificationCompat.Builder(context, PERIOD_CHANNEL_ID)
            .setSmallIcon(R.drawable.ic_notification)
            .setContentTitle("Cycle Update")
            .setContentText(getPeriodReminderMessage(daysUntil))
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setContentIntent(pendingIntent)
            .setAutoCancel(true)
            .setCategory(NotificationCompat.CATEGORY_REMINDER)
            .build()
        
        notificationManager.notify(PERIOD_REMINDER_ID, notification)
    }
    
    fun showFertileWindowNotification() {
        val intent = Intent(context, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        }
        
        val pendingIntent = PendingIntent.getActivity(
            context,
            FERTILE_REQUEST_CODE,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )
        
        val notification = NotificationCompat.Builder(context, FERTILITY_CHANNEL_ID)
            .setSmallIcon(R.drawable.ic_notification)
            .setContentTitle("Fertility Tracker")
            .setContentText("Fertile window starts today")
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setContentIntent(pendingIntent)
            .setAutoCancel(true)
            .setCategory(NotificationCompat.CATEGORY_REMINDER)
            .build()
        
        notificationManager.notify(FERTILE_WINDOW_ID, notification)
    }
    
    fun showOvulationNotification() {
        val intent = Intent(context, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        }
        
        val pendingIntent = PendingIntent.getActivity(
            context,
            OVULATION_REQUEST_CODE,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )
        
        val notification = NotificationCompat.Builder(context, OVULATION_CHANNEL_ID)
            .setSmallIcon(R.drawable.ic_notification)
            .setContentTitle("Cycle Tracker")
            .setContentText("Ovulation expected today")
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setContentIntent(pendingIntent)
            .setAutoCancel(true)
            .setCategory(NotificationCompat.CATEGORY_REMINDER)
            .build()
        
        notificationManager.notify(OVULATION_ID, notification)
    }
    
    private fun getPeriodReminderMessage(daysUntil: Int): String {
        return when (daysUntil) {
            0 -> "Your cycle is due today"
            1 -> "Your cycle is due tomorrow"
            else -> "Your cycle is due in $daysUntil days"
        }
    }
    
    fun cancelAllNotifications() {
        notificationManager.cancel(PERIOD_REMINDER_ID)
        notificationManager.cancel(FERTILE_WINDOW_ID)
        notificationManager.cancel(OVULATION_ID)
    }
    
    fun cancelPeriodNotification() {
        notificationManager.cancel(PERIOD_REMINDER_ID)
    }
    
    fun cancelFertilityNotifications() {
        notificationManager.cancel(FERTILE_WINDOW_ID)
        notificationManager.cancel(OVULATION_ID)
    }
    
    fun areNotificationsEnabled(): Boolean {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            notificationManager.areNotificationsEnabled()
        } else {
            true // Assume enabled for older versions
        }
    }
    
    fun isPeriodChannelEnabled(): Boolean {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = notificationManager.getNotificationChannel(PERIOD_CHANNEL_ID)
            channel?.importance != NotificationManager.IMPORTANCE_NONE
        } else {
            true
        }
    }
    
    fun isFertilityChannelEnabled(): Boolean {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = notificationManager.getNotificationChannel(FERTILITY_CHANNEL_ID)
            channel?.importance != NotificationManager.IMPORTANCE_NONE
        } else {
            true
        }
    }
}