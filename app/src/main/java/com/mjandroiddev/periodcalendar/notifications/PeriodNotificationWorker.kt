package com.mjandroiddev.periodcalendar.notifications

import android.content.Context
import android.util.Log
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.mjandroiddev.periodcalendar.data.repository.CycleEntryRepository
import com.mjandroiddev.periodcalendar.data.repository.UserSettingsRepository
import com.mjandroiddev.periodcalendar.utils.CyclePredictionUtil
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import java.time.LocalDate
import javax.inject.Inject

@HiltWorker
class PeriodNotificationWorker @AssistedInject constructor(
    @Assisted context: Context,
    @Assisted workerParams: WorkerParameters,
    private val notificationHelper: NotificationHelper,
    private val cycleEntryRepository: CycleEntryRepository,
    private val userSettingsRepository: UserSettingsRepository
) : CoroutineWorker(context, workerParams) {

    companion object {
        private const val TAG = "PeriodNotificationWorker"
        
        // Input data keys
        const val NOTIFICATION_TYPE_KEY = "notification_type"
        const val DAYS_BEFORE_KEY = "days_before"
        
        // Notification types
        const val TYPE_PERIOD_REMINDER = "period_reminder"
        const val TYPE_FERTILE_WINDOW = "fertile_window"
        const val TYPE_OVULATION = "ovulation"
    }

    override suspend fun doWork(): Result {
        return try {
            val notificationType = inputData.getString(NOTIFICATION_TYPE_KEY)
                ?: return Result.failure()
            
            Log.d(TAG, "Processing notification type: $notificationType")
            
            val userSettings = userSettingsRepository.getUserSettingsOnce()
            
            // Check if notifications are enabled for this type
            val shouldSendNotification = when (notificationType) {
                TYPE_PERIOD_REMINDER -> userSettings.notifBeforePeriod > 0
                TYPE_FERTILE_WINDOW -> userSettings.notifFertileWindow
                TYPE_OVULATION -> userSettings.notifOvulation
                else -> false
            }
            
            if (!shouldSendNotification) {
                Log.d(TAG, "Notifications disabled for type: $notificationType")
                return Result.success()
            }
            
            // Get the last period entry to calculate predictions
            val lastPeriodEntry = cycleEntryRepository.getLastPeriodEntry()
            
            if (lastPeriodEntry == null) {
                Log.w(TAG, "No period data available for predictions")
                return Result.success()
            }
            
            val today = LocalDate.now()
            val prediction = CyclePredictionUtil.predictCycle(
                lastPeriodEntry.date, 
                userSettings.avgCycleLength
            )
            
            when (notificationType) {
                TYPE_PERIOD_REMINDER -> {
                    val daysUntil = inputData.getInt(DAYS_BEFORE_KEY, 1)
                    val targetDate = prediction.nextPeriodDate.minusDays(daysUntil.toLong())
                    
                    if (today == targetDate) {
                        notificationHelper.showPeriodReminderNotification(daysUntil)
                        Log.d(TAG, "Sent period reminder notification ($daysUntil days)")
                    }
                }
                
                TYPE_FERTILE_WINDOW -> {
                    if (today == prediction.fertileWindowStart) {
                        notificationHelper.showFertileWindowNotification()
                        Log.d(TAG, "Sent fertile window notification")
                    }
                }
                
                TYPE_OVULATION -> {
                    if (today == prediction.ovulationDate) {
                        notificationHelper.showOvulationNotification()
                        Log.d(TAG, "Sent ovulation notification")
                    }
                }
            }
            
            Result.success()
        } catch (exception: Exception) {
            Log.e(TAG, "Error in PeriodNotificationWorker", exception)
            Result.failure()
        }
    }
}