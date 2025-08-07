package com.mjandroiddev.periodcalendar.notifications

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log
import androidx.work.CoroutineWorker
import androidx.work.Data
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import androidx.work.WorkerParameters
import com.mjandroiddev.periodcalendar.data.repository.CycleEntryRepository
import com.mjandroiddev.periodcalendar.data.repository.UserSettingsRepository
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * BroadcastReceiver that reschedules notifications after device boot.
 */
@AndroidEntryPoint
class BootReceiver : BroadcastReceiver() {
    
    companion object {
        private const val TAG = "BootReceiver"
        const val BOOT_RESCHEDULE_WORK = "boot_reschedule_work"
    }
    
    override fun onReceive(context: Context, intent: Intent) {
        if (intent.action == Intent.ACTION_BOOT_COMPLETED ||
            intent.action == Intent.ACTION_MY_PACKAGE_REPLACED) {
            
            Log.d(TAG, "Boot completed or package replaced, scheduling notification reschedule work")
            
            // Schedule a worker to reschedule notifications
            // We use WorkManager here because we can't inject dependencies directly in BroadcastReceiver
            val rescheduleWork = OneTimeWorkRequestBuilder<BootRescheduleWorker>()
                .build()
            
            WorkManager.getInstance(context).enqueue(rescheduleWork)
        }
    }
}

/**
 * Worker that handles rescheduling notifications after boot.
 * This is needed because we can't inject dependencies directly into BroadcastReceiver.
 */
@androidx.hilt.work.HiltWorker
class BootRescheduleWorker @dagger.assisted.AssistedInject constructor(
    @dagger.assisted.Assisted private val context: Context,
    @dagger.assisted.Assisted workerParams: WorkerParameters,
    private val notificationScheduler: NotificationScheduler,
    private val userSettingsRepository: UserSettingsRepository,
    private val cycleEntryRepository: CycleEntryRepository
) : CoroutineWorker(context, workerParams) {
    
    companion object {
        private const val TAG = "BootRescheduleWorker"
    }
    
    override suspend fun doWork(): Result {
        return try {
            Log.d(TAG, "Starting notification reschedule after boot")
            
            // Check if user has any period data before scheduling
            val lastPeriodEntry = cycleEntryRepository.getLastPeriodEntry()
            if (lastPeriodEntry == null) {
                Log.d(TAG, "No period data available, skipping notification reschedule")
                return Result.success()
            }
            
            // Check if any notifications are enabled
            val userSettings = userSettingsRepository.getUserSettingsOnce()
            val hasEnabledNotifications = userSettings.notifBeforePeriod > 0 || 
                                        userSettings.notifFertileWindow || 
                                        userSettings.notifOvulation
            
            if (!hasEnabledNotifications) {
                Log.d(TAG, "No notifications enabled, skipping reschedule")
                return Result.success()
            }
            
            // Reschedule all notifications
            notificationScheduler.scheduleAllNotifications()
            
            Log.d(TAG, "Successfully rescheduled notifications after boot")
            Result.success()
        } catch (e: Exception) {
            Log.e(TAG, "Error rescheduling notifications after boot", e)
            Result.failure()
        }
    }
}

/**
 * Alternative approach using coroutines in the receiver itself
 * This is a simpler approach but requires careful handling of the receiver lifecycle
 */
class SimpleBootReceiver : BroadcastReceiver() {
    
    companion object {
        private const val TAG = "SimpleBootReceiver"
    }
    
    override fun onReceive(context: Context, intent: Intent) {
        if (intent.action == Intent.ACTION_BOOT_COMPLETED ||
            intent.action == Intent.ACTION_MY_PACKAGE_REPLACED) {
            
            Log.d(TAG, "Boot completed, rescheduling notifications")
            
            // Use goAsync to allow for asynchronous work
            val pendingResult = goAsync()
            
            CoroutineScope(Dispatchers.IO).launch {
                try {
                    // Here we would need to manually create the scheduler
                    // This is less ideal because we lose dependency injection
                    Log.d(TAG, "Would reschedule notifications here")
                    // notificationScheduler.scheduleAllNotifications()
                } catch (e: Exception) {
                    Log.e(TAG, "Error in boot receiver", e)
                } finally {
                    pendingResult.finish()
                }
            }
        }
    }
}