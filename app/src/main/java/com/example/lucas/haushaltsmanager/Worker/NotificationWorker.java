package com.example.lucas.haushaltsmanager.Worker;

import android.app.Notification;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v4.app.NotificationManagerCompat;
import android.util.Log;

import com.example.lucas.haushaltsmanager.Activities.MainTab.ParentActivity;
import com.example.lucas.haushaltsmanager.PreferencesHelper.AppInternalPreferences;
import com.example.lucas.haushaltsmanager.R;

import java.util.Random;
import java.util.concurrent.TimeUnit;

import androidx.work.OneTimeWorkRequest;
import androidx.work.WorkManager;
import androidx.work.Worker;
import androidx.work.WorkerParameters;

public class NotificationWorker extends Worker {
    public static final String WORKER_ID = "notificationWorker";

    public NotificationWorker(@NonNull Context context, @NonNull WorkerParameters workerParams) {
        super(context, workerParams);
    }

    @NonNull
    @Override
    public Result doWork() {
        PendingIntent pi = PendingIntent.getActivity(getApplicationContext(), 0, new Intent(getApplicationContext(), ParentActivity.class), 0);

        Notification notification = new Notification.Builder(getApplicationContext())
                .setContentTitle(getApplicationContext().getString(R.string.remind_notification_title))//TODO Notification titel anpassen
                .setContentText(getApplicationContext().getString(R.string.remind_notification_body))
                .setAutoCancel(true)
                .setContentIntent(pi)
                .setSmallIcon(R.mipmap.ic_launcher)
                .build();

        NotificationManagerCompat.from(getApplicationContext()).notify(getNotificationId(), notification);

        setNewWorker();

        return Result.SUCCESS;
    }

    private int getNotificationId() {
        return new Random().nextInt();
    }

    /**
     * Methode um einen neuen NotificationWorker zu starten.
     * Da man mit dem aktuellen WorkManager noch keine Wiederkehrenden Worker erstellen kann,
     * bei denen der erste Worker zu einer bestimmten Zeit ausgelöst wird,ist das hier der Workaround.
     */
    private void setNewWorker() {
        //CLEANUP Wenn man den ersten Worker schedulen kann diese Methodik entfernen
        OneTimeWorkRequest workRequest = new OneTimeWorkRequest
                .Builder(NotificationWorker.class)
                .setInitialDelay(1, TimeUnit.MINUTES)
                .build();

        saveWorkerId(workRequest.getId().toString());

        WorkManager.getInstance().enqueue(workRequest);

        Log.i(NotificationWorker.class.getSimpleName(), "Scheduling next NotificationJob");
    }

    private void saveWorkerId(String id) {

        AppInternalPreferences preferences = new AppInternalPreferences(getApplicationContext());
        preferences.setNotificationJobId(id);
    }
}
