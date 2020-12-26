package com.example.lucas.haushaltsmanager.Worker.PeriodicWorker;

import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.work.WorkManager;
import androidx.work.WorkerParameters;

import com.example.lucas.haushaltsmanager.Activities.MainTab.ParentActivity;
import com.example.lucas.haushaltsmanager.App.app;
import com.example.lucas.haushaltsmanager.Entities.NotificationVO;
import com.example.lucas.haushaltsmanager.R;

import java.util.Random;

public class NotificationWorker extends AbstractRecurringWorker {
    public static final String WORKER_TAG = "notificationWorker";

    public static final String TITLE = "title";
    public static final String CONTENT = "content";
    public static final String ICON = "icon";

    private final NotificationVO notification;

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

    public static void stopWorker(Context context) {
        Log.i("NotificationWorker", "Stopping NotificationWorker");
        WorkManager.getInstance(context).cancelAllWorkByTag(WORKER_TAG);
    }

    public static boolean isRunning() {
        return isWorkerScheduled(WORKER_TAG);
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

    @Override
    String getTag() {
        return "NotificationWorker";
    }

    /**
     * Eine richtige NotificationId brauche ich erst,
     * wenn ich Push-Benachrichtigungen im nachhinein bearbeiten muss.
     */
    private int getNotificationId() {
        return new Random().nextInt();
    }
}
