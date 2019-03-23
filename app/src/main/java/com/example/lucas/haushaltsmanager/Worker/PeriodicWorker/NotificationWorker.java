package com.example.lucas.haushaltsmanager.Worker.PeriodicWorker;

import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.NotificationManagerCompat;
import android.util.Log;

import com.example.lucas.haushaltsmanager.Activities.MainTab.ParentActivity;
import com.example.lucas.haushaltsmanager.App.app;
import com.example.lucas.haushaltsmanager.Entities.NotificationVO;
import com.example.lucas.haushaltsmanager.R;

import java.util.Random;

import androidx.work.WorkManager;
import androidx.work.WorkerParameters;

public class NotificationWorker extends AbstractRecurringWorker {
    public static final String WORKER_TAG = "notificationWorker";

    public static final String TITLE = "title";
    public static final String CONTENT = "content";
    public static final String ICON = "icon";

    private NotificationVO notification;

    /**
     * Source: https://developer.android.com/training/notify-user/build-notification
     */
    public NotificationWorker(@NonNull Context context, @NonNull WorkerParameters workerParams) {
        super(context, workerParams);

        notification = new NotificationVO(
                getInputData().getString(TITLE),
                getInputData().getString(CONTENT),
                getInputData().getInt(ICON, R.mipmap.ic_launcher)
        );
    }

    public static void stopWorker() {
        Log.i("NotificationWorker", "Stopping NotificationWorker");
        WorkManager.getInstance().cancelAllWorkByTag(WORKER_TAG);
    }

    public static boolean isRunning() {
        return isWorkerScheduled(WORKER_TAG);
    }

    @Override
    String getTag() {
        return "NotificationWorker";
    }

    @NonNull
    @Override
    public Result doWork() {
        PendingIntent pi = PendingIntent.getActivity(getApplicationContext(), 0, new Intent(getApplicationContext(), ParentActivity.class), 0);

        String reminderChannelId = app.createReminderNotificationChannel();
        NotificationCompat.Builder notification = new NotificationCompat.Builder(getApplicationContext(), reminderChannelId)
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                .setContentTitle(this.notification.getTitle())
                .setContentText(this.notification.getContent())
                .setSmallIcon(this.notification.getIcon())
                .setContentIntent(pi)
                .setAutoCancel(true);

        NotificationManagerCompat
                .from(getApplicationContext())
                .notify(getNotificationId(), notification.build());

        scheduleNextWorker(this.notification);

        return Result.success();
    }

    /**
     * Eine richtige NotificationId brauche ich erst,
     * wenn ich Push-Benachrichtigungen im nachhinein bearbeiten muss.
     */
    private int getNotificationId() {
        return new Random().nextInt();
    }
}
