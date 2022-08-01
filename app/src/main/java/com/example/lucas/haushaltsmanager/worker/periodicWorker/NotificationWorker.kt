package com.example.lucas.haushaltsmanager.worker.periodicWorker

import android.app.Notification
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.util.Log
import androidx.core.app.NotificationManagerCompat
import androidx.work.*
import com.example.lucas.haushaltsmanager.Activities.MainTab.ParentActivity
import com.example.lucas.haushaltsmanager.App.app
import java.util.*
import com.example.lucas.haushaltsmanager.entities.Notification as CustomNotification

class NotificationWorker(context: Context, workerParams: WorkerParameters) : Worker(context, workerParams) {
    companion object {
        const val WORKER_TAG = "notificationWorker"

        private const val INPUT_DATA_NOTIFICATION_TITLE = "title"
        private const val INPUT_DATA_NOTIFICATION_CONTENT = "content"
        private const val INPUT_DATA_NOTIFICATION_ICON = "icon"

        @JvmStatic
        fun createWorkRequest(notification: CustomNotification): PeriodicWorkRequest {
            val inputData = workDataOf(
                INPUT_DATA_NOTIFICATION_TITLE to notification.title,
                INPUT_DATA_NOTIFICATION_CONTENT to notification.content,
                INPUT_DATA_NOTIFICATION_ICON to notification.icon
            )

            return PeriodicWorkRequestBuilder<NotificationWorker>(notification.getDelay().duration, notification.getDelay().timeUnit)
                .setInputData(inputData)
                .addTag(WORKER_TAG)
                .build()
        }

        @JvmStatic
        fun stopWorker(context: Context) {
            Log.i(WORKER_TAG, "Stopping NotificationWorker")

            WorkManager.getInstance(context).cancelAllWorkByTag(WORKER_TAG)
        }
    }

    override fun doWork(): Result {
        val notification = buildNotification()

        NotificationManagerCompat
            .from(applicationContext)
            .notify(getNotificationId(), notification)

        return Result.success()
    }

    /**
     * I only need a real NotificationId if I want to edit/change the notification
     * after is has been dispatched
     */
    private fun getNotificationId(): Int {
        return Random().nextInt()
    }

    private fun buildNotification(): Notification {
        val pi = PendingIntent.getActivity(
            applicationContext,
            0,
            Intent(applicationContext, ParentActivity::class.java),
            PendingIntent.FLAG_IMMUTABLE
        )

        val reminderChannelId = app.createReminderNotificationChannel()

        return Notification.Builder(applicationContext, reminderChannelId)
            .setContentTitle(inputData.getString(INPUT_DATA_NOTIFICATION_TITLE))
            .setContentText(inputData.getString(INPUT_DATA_NOTIFICATION_CONTENT))
            .setSmallIcon(inputData.getInt(INPUT_DATA_NOTIFICATION_ICON, applicationContext.applicationInfo.icon))
            .setContentIntent(pi)
            .setAutoCancel(true)
            .build()
    }
}