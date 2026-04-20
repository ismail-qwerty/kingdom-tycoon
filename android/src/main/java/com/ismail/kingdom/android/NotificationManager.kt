// PATH: android/src/main/java/com/ismail/kingdom/android/NotificationManager.kt
package com.ismail.kingdom.android

import android.app.NotificationChannel
import android.app.NotificationManager as AndroidNotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import androidx.work.Worker
import androidx.work.WorkerParameters
import java.util.concurrent.TimeUnit

// Manages offline earnings notifications
class NotificationManager(private val context: Context) {
    
    private val CHANNEL_ID = "kingdom_tycoon_notifications"
    private val NOTIFICATION_ID = 1001
    private val WORK_TAG = "offline_earnings_notification"
    
    init {
        createNotificationChannel()
    }
    
    // Creates notification channel (Android 8.0+)
    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val name = "Kingdom Tycoon"
            val descriptionText = "Notifications about your kingdom's progress"
            val importance = AndroidNotificationManager.IMPORTANCE_DEFAULT
            val channel = NotificationChannel(CHANNEL_ID, name, importance).apply {
                description = descriptionText
            }
            
            val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as AndroidNotificationManager
            notificationManager.createNotificationChannel(channel)
        }
    }
    
    // Schedules offline earnings notification (8 hours after last play)
    fun scheduleOfflineEarningsNotification() {
        // Cancel any existing scheduled notifications
        cancelNotifications()
        
        // Schedule new notification for 8 hours from now
        val workRequest = OneTimeWorkRequestBuilder<OfflineEarningsWorker>()
            .setInitialDelay(8, TimeUnit.HOURS)
            .addTag(WORK_TAG)
            .build()
        
        WorkManager.getInstance(context).enqueue(workRequest)
    }
    
    // Cancels all pending notifications
    fun cancelNotifications() {
        // Cancel WorkManager tasks
        WorkManager.getInstance(context).cancelAllWorkByTag(WORK_TAG)
        
        // Cancel displayed notifications
        val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as AndroidNotificationManager
        notificationManager.cancel(NOTIFICATION_ID)
    }
    
    // Shows offline earnings notification
    private fun showOfflineEarningsNotification() {
        val intent = Intent(context, AndroidLauncher::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        }
        
        val pendingIntent = PendingIntent.getActivity(
            context,
            0,
            intent,
            PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
        )
        
        val notification = NotificationCompat.Builder(context, CHANNEL_ID)
            .setSmallIcon(android.R.drawable.ic_dialog_info) // TODO: Replace with app icon
            .setContentTitle("Your kingdom is overflowing with gold!")
            .setContentText("Come collect your offline earnings!")
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setContentIntent(pendingIntent)
            .setAutoCancel(true)
            .build()
        
        val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as AndroidNotificationManager
        notificationManager.notify(NOTIFICATION_ID, notification)
    }
    
    // Worker that shows notification after 8 hours
    class OfflineEarningsWorker(
        context: Context,
        params: WorkerParameters
    ) : Worker(context, params) {
        
        override fun doWork(): Result {
            val notificationManager = NotificationManager(applicationContext)
            notificationManager.showOfflineEarningsNotification()
            return Result.success()
        }
    }
}
