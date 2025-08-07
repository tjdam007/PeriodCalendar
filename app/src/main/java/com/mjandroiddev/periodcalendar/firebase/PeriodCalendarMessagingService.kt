package com.mjandroiddev.periodcalendar.firebase

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Build
import androidx.core.app.NotificationCompat
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import com.mjandroiddev.periodcalendar.R
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.net.HttpURLConnection
import java.net.URL
import javax.inject.Inject

@AndroidEntryPoint
class PeriodCalendarMessagingService : FirebaseMessagingService() {
    
    @Inject
    lateinit var analyticsLogger: AnalyticsLogger
    
    companion object {
        private const val NOTIFICATION_CHANNEL_ID = "period_calendar_fcm"
        private const val NOTIFICATION_CHANNEL_NAME = "Period Calendar Notifications"
        private const val NOTIFICATION_ID = 1001
    }
    
    override fun onCreate() {
        super.onCreate()
        createNotificationChannel()
    }
    
    override fun onMessageReceived(remoteMessage: RemoteMessage) {
        super.onMessageReceived(remoteMessage)
        
        // Log FCM message received
        try {
            analyticsLogger.logError(
                RuntimeException("FCM Message Received"), 
                "From: ${remoteMessage.from}, Data: ${remoteMessage.data}"
            )
        } catch (e: Exception) {
            // Fail silently if analytics isn't initialized
        }
        
        // Extract notification data
        val title = remoteMessage.notification?.title
        val body = remoteMessage.notification?.body
        val imageUrl = remoteMessage.notification?.imageUrl?.toString()
        val clickUrl = remoteMessage.data["url"]
        
        // Show notification if we have title and body
        if (!title.isNullOrEmpty() && !body.isNullOrEmpty()) {
            showNotification(title, body, imageUrl, clickUrl)
        }
    }
    
    override fun onNewToken(token: String) {
        super.onNewToken(token)
        
        // Log new token for debugging
        try {
            analyticsLogger.logError(
                RuntimeException("FCM Token Refreshed"), 
                "New token: $token"
            )
        } catch (e: Exception) {
            // Fail silently
        }
        
        // Here you would typically send the token to your server
        // sendRegistrationToServer(token)
    }
    
    /**
     * Create notification channel for FCM notifications
     */
    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                NOTIFICATION_CHANNEL_ID,
                NOTIFICATION_CHANNEL_NAME,
                NotificationManager.IMPORTANCE_DEFAULT
            ).apply {
                description = "Receive health tips and cycle insights"
                enableVibration(true)
                enableLights(true)
            }
            
            val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)
        }
    }
    
    /**
     * Show notification with optional image and click action
     */
    private fun showNotification(
        title: String,
        body: String,
        imageUrl: String?,
        clickUrl: String?
    ) {
        val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        
        // Create pending intent for notification click
        val pendingIntent = createNotificationPendingIntent(title, clickUrl)
        
        // Build notification
        val notificationBuilder = NotificationCompat.Builder(this, NOTIFICATION_CHANNEL_ID)
            .setSmallIcon(R.drawable.ic_notification)
            .setContentTitle(title)
            .setContentText(body)
            .setStyle(NotificationCompat.BigTextStyle().bigText(body))
            .setAutoCancel(true)
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setContentIntent(pendingIntent)
        
        // Load and set large image if URL provided
        if (!imageUrl.isNullOrEmpty()) {
            CoroutineScope(Dispatchers.IO).launch {
                try {
                    val bitmap = loadImageFromUrl(imageUrl)
                    if (bitmap != null) {
                        withContext(Dispatchers.Main) {
                            val updatedNotification = notificationBuilder
                                .setLargeIcon(bitmap)
                                .setStyle(
                                    NotificationCompat.BigPictureStyle()
                                        .bigPicture(bitmap)
                                        .setBigContentTitle(title)
                                        .setSummaryText(body)
                                )
                                .build()
                            
                            notificationManager.notify(NOTIFICATION_ID, updatedNotification)
                        }
                    } else {
                        // Show notification without image
                        withContext(Dispatchers.Main) {
                            notificationManager.notify(NOTIFICATION_ID, notificationBuilder.build())
                        }
                    }
                } catch (e: Exception) {
                    // Show notification without image if loading fails
                    withContext(Dispatchers.Main) {
                        notificationManager.notify(NOTIFICATION_ID, notificationBuilder.build())
                    }
                }
            }
        } else {
            // Show notification without image
            notificationManager.notify(NOTIFICATION_ID, notificationBuilder.build())
        }
    }
    
    /**
     * Create pending intent that opens URL in browser when notification is clicked
     */
    private fun createNotificationPendingIntent(title: String, clickUrl: String?): PendingIntent {
        return if (!clickUrl.isNullOrEmpty()) {
            // Create intent to open URL in browser
            val browserIntent = Intent(Intent.ACTION_VIEW, Uri.parse(clickUrl)).apply {
                flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP
            }
            
            // Create pending intent with analytics tracking
            val trackingIntent = Intent(this, NotificationClickReceiver::class.java).apply {
                putExtra("title", title)
                putExtra("url", clickUrl)
                putExtra("browser_intent", browserIntent)
            }
            
            PendingIntent.getBroadcast(
                this,
                NOTIFICATION_ID,
                trackingIntent,
                PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
            )
        } else {
            // Fallback to opening main app
            val appIntent = packageManager.getLaunchIntentForPackage(packageName) ?: Intent()
            PendingIntent.getActivity(
                this,
                NOTIFICATION_ID,
                appIntent,
                PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
            )
        }
    }
    
    /**
     * Load image from URL for large notification image
     */
    private suspend fun loadImageFromUrl(imageUrl: String): Bitmap? = withContext(Dispatchers.IO) {
        try {
            val url = URL(imageUrl)
            val connection = url.openConnection() as HttpURLConnection
            connection.connectTimeout = 5000
            connection.readTimeout = 10000
            connection.doInput = true
            connection.connect()
            
            val inputStream = connection.inputStream
            val bitmap = BitmapFactory.decodeStream(inputStream)
            inputStream.close()
            connection.disconnect()
            
            // Resize if too large to avoid memory issues
            if (bitmap != null && (bitmap.width > 512 || bitmap.height > 512)) {
                val ratio = minOf(512f / bitmap.width, 512f / bitmap.height)
                val newWidth = (bitmap.width * ratio).toInt()
                val newHeight = (bitmap.height * ratio).toInt()
                Bitmap.createScaledBitmap(bitmap, newWidth, newHeight, true)
            } else {
                bitmap
            }
        } catch (e: Exception) {
            null
        }
    }
}