package com.mjandroiddev.periodcalendar.notifications

import android.content.Context
import android.util.Log
import androidx.work.*
import com.mjandroiddev.periodcalendar.data.repository.CycleEntryRepository
import com.mjandroiddev.periodcalendar.data.repository.UserSettingsRepository
import com.mjandroiddev.periodcalendar.utils.CyclePredictionUtil
import java.time.LocalDate
import java.time.temporal.ChronoUnit
import java.util.concurrent.TimeUnit
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class NotificationScheduler @Inject constructor(
    private val context: Context,
    private val cycleEntryRepository: CycleEntryRepository,
    private val userSettingsRepository: UserSettingsRepository
) {
    
    companion object {
        private const val TAG = "NotificationScheduler"
        
        // Work tags
        private const val PERIOD_WORK_TAG = "period_notification_work"
        private const val FERTILE_WORK_TAG = "fertile_notification_work"
        private const val OVULATION_WORK_TAG = "ovulation_notification_work"
        
        // Unique work names
        private const val PERIOD_WORK_NAME = "period_reminder_work"
        private const val FERTILE_WORK_NAME = "fertile_window_work"
        private const val OVULATION_WORK_NAME = "ovulation_work"
    }
    
    private val workManager = WorkManager.getInstance(context)
    
    suspend fun scheduleAllNotifications() {
        try {
            Log.d(TAG, "Scheduling all notifications")
            
            val userSettings = userSettingsRepository.getUserSettingsOnce()
            val lastPeriodEntry = cycleEntryRepository.getLastPeriodEntry()
            
            if (lastPeriodEntry == null) {
                Log.w(TAG, "No period data available, cannot schedule notifications")
                return
            }
            
            val prediction = CyclePredictionUtil.predictCycle(
                lastPeriodEntry.date,
                userSettings.avgCycleLength
            )
            
            val today = LocalDate.now()
            
            // Schedule period reminder notification
            if (userSettings.notifBeforePeriod > 0) {
                schedulePeriodReminder(prediction.nextPeriodDate, userSettings.notifBeforePeriod, today)
            }
            
            // Schedule fertile window notification
            if (userSettings.notifFertileWindow) {
                scheduleFertileWindowNotification(prediction.fertileWindowStart, today)
            }
            
            // Schedule ovulation notification
            if (userSettings.notifOvulation) {
                scheduleOvulationNotification(prediction.ovulationDate, today)
            }
            
            Log.d(TAG, "All notifications scheduled successfully")
        } catch (e: Exception) {
            Log.e(TAG, "Error scheduling notifications", e)
        }
    }
    
    private fun schedulePeriodReminder(nextPeriodDate: LocalDate, daysBefore: Int, today: LocalDate) {
        val targetDate = nextPeriodDate.minusDays(daysBefore.toLong())
        
        if (targetDate.isBefore(today)) {
            Log.d(TAG, "Period reminder date is in the past, skipping")
            return
        }
        
        val delayInDays = ChronoUnit.DAYS.between(today, targetDate)
        
        val inputData = Data.Builder()
            .putString(PeriodNotificationWorker.NOTIFICATION_TYPE_KEY, PeriodNotificationWorker.TYPE_PERIOD_REMINDER)
            .putInt(PeriodNotificationWorker.DAYS_BEFORE_KEY, daysBefore)
            .build()
        
        val workRequest = OneTimeWorkRequestBuilder<PeriodNotificationWorker>()
            .setInputData(inputData)
            .setInitialDelay(delayInDays, TimeUnit.DAYS)
            .addTag(PERIOD_WORK_TAG)
            .setConstraints(
                Constraints.Builder()
                    .setRequiredNetworkType(NetworkType.NOT_REQUIRED)
                    .setRequiresBatteryNotLow(false)
                    .build()
            )
            .build()
        
        workManager.enqueueUniqueWork(
            PERIOD_WORK_NAME,
            ExistingWorkPolicy.REPLACE,
            workRequest
        )
        
        Log.d(TAG, "Scheduled period reminder for $targetDate (in $delayInDays days)")
    }
    
    private fun scheduleFertileWindowNotification(fertileWindowStart: LocalDate, today: LocalDate) {
        if (fertileWindowStart.isBefore(today)) {
            Log.d(TAG, "Fertile window start date is in the past, skipping")
            return
        }
        
        val delayInDays = ChronoUnit.DAYS.between(today, fertileWindowStart)
        
        val inputData = Data.Builder()
            .putString(PeriodNotificationWorker.NOTIFICATION_TYPE_KEY, PeriodNotificationWorker.TYPE_FERTILE_WINDOW)
            .build()
        
        val workRequest = OneTimeWorkRequestBuilder<PeriodNotificationWorker>()
            .setInputData(inputData)
            .setInitialDelay(delayInDays, TimeUnit.DAYS)
            .addTag(FERTILE_WORK_TAG)
            .setConstraints(
                Constraints.Builder()
                    .setRequiredNetworkType(NetworkType.NOT_REQUIRED)
                    .setRequiresBatteryNotLow(false)
                    .build()
            )
            .build()
        
        workManager.enqueueUniqueWork(
            FERTILE_WORK_NAME,
            ExistingWorkPolicy.REPLACE,
            workRequest
        )
        
        Log.d(TAG, "Scheduled fertile window notification for $fertileWindowStart (in $delayInDays days)")
    }
    
    private fun scheduleOvulationNotification(ovulationDate: LocalDate, today: LocalDate) {
        if (ovulationDate.isBefore(today)) {
            Log.d(TAG, "Ovulation date is in the past, skipping")
            return
        }
        
        val delayInDays = ChronoUnit.DAYS.between(today, ovulationDate)
        
        val inputData = Data.Builder()
            .putString(PeriodNotificationWorker.NOTIFICATION_TYPE_KEY, PeriodNotificationWorker.TYPE_OVULATION)
            .build()
        
        val workRequest = OneTimeWorkRequestBuilder<PeriodNotificationWorker>()
            .setInputData(inputData)
            .setInitialDelay(delayInDays, TimeUnit.DAYS)
            .addTag(OVULATION_WORK_TAG)
            .setConstraints(
                Constraints.Builder()
                    .setRequiredNetworkType(NetworkType.NOT_REQUIRED)
                    .setRequiresBatteryNotLow(false)
                    .build()
            )
            .build()
        
        workManager.enqueueUniqueWork(
            OVULATION_WORK_NAME,
            ExistingWorkPolicy.REPLACE,
            workRequest
        )
        
        Log.d(TAG, "Scheduled ovulation notification for $ovulationDate (in $delayInDays days)")
    }
    
    fun cancelAllNotifications() {
        Log.d(TAG, "Canceling all scheduled notifications")
        
        workManager.cancelUniqueWork(PERIOD_WORK_NAME)
        workManager.cancelUniqueWork(FERTILE_WORK_NAME)
        workManager.cancelUniqueWork(OVULATION_WORK_NAME)
        
        // Also cancel any notifications that might be currently displayed
        val notificationHelper = NotificationHelper(context)
        notificationHelper.cancelAllNotifications()
    }
    
    fun cancelPeriodNotifications() {
        Log.d(TAG, "Canceling period notifications")
        workManager.cancelUniqueWork(PERIOD_WORK_NAME)
    }
    
    fun cancelFertilityNotifications() {
        Log.d(TAG, "Canceling fertility notifications")
        workManager.cancelUniqueWork(FERTILE_WORK_NAME)
        workManager.cancelUniqueWork(OVULATION_WORK_NAME)
    }
    
    suspend fun rescheduleNotificationsAfterPeriodEntry() {
        Log.d(TAG, "Rescheduling notifications after new period entry")
        
        // Cancel existing notifications first
        cancelAllNotifications()
        
        // Wait a bit to ensure cancellation is processed
        kotlinx.coroutines.delay(1000)
        
        // Schedule new notifications
        scheduleAllNotifications()
    }
    
    suspend fun rescheduleNotificationsAfterSettingsChange() {
        Log.d(TAG, "Rescheduling notifications after settings change")
        
        // Cancel and reschedule all notifications
        cancelAllNotifications()
        
        // Wait a bit to ensure cancellation is processed
        kotlinx.coroutines.delay(1000)
        
        scheduleAllNotifications()
    }
    
    fun getScheduledWorkInfo() {
        val periodWork = workManager.getWorkInfosForUniqueWork(PERIOD_WORK_NAME)
        val fertileWork = workManager.getWorkInfosForUniqueWork(FERTILE_WORK_NAME)
        val ovulationWork = workManager.getWorkInfosForUniqueWork(OVULATION_WORK_NAME)
        
        Log.d(TAG, "Period work info: ${periodWork.get().firstOrNull()?.state}")
        Log.d(TAG, "Fertile work info: ${fertileWork.get().firstOrNull()?.state}")
        Log.d(TAG, "Ovulation work info: ${ovulationWork.get().firstOrNull()?.state}")
    }
}