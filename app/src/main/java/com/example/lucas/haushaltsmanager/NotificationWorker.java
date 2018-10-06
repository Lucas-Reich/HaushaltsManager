package com.example.lucas.haushaltsmanager;

import android.app.Notification;
import android.app.PendingIntent;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v4.app.NotificationManagerCompat;
import android.util.Log;

import com.example.lucas.haushaltsmanager.Activities.MainTab.ParentActivity;
import com.example.lucas.haushaltsmanager.PreferencesHelper.AppInternalPreferences;

import java.util.Random;
import java.util.concurrent.TimeUnit;

import androidx.work.OneTimeWorkRequest;
import androidx.work.WorkManager;
import androidx.work.Worker;

public class NotificationWorker extends Worker {

    @NonNull
    @Override
    public Result doWork() {
        PendingIntent pi = PendingIntent.getActivity(getApplicationContext(), 0, new Intent(getApplicationContext(), ParentActivity.class), 0);

        Notification notification = new Notification.Builder(getApplicationContext())
                .setContentTitle("Hier ist eine Notification")
                .setContentText("Hier ist die Nachricht der Notification die ich gerade gesendet habe")
                .setAutoCancel(true)
                .setContentIntent(pi)
                .setSmallIcon(R.mipmap.ic_launcher)
                .build();

        NotificationManagerCompat.from(getApplicationContext()).notify(new Random().nextInt(), notification);

        setNewWorker();

        return Result.SUCCESS;
    }

    /**
     * Methode um einen neuen NotificationWorker zu starten.
     * Da man mit dem aktuellen WorkManager noch keine Wiederkehrenden Worker erstellen kann,
     * bei denen der erste Worker zu einer bestimmten Zeit ausgelöst wird,ist das hier der Workaround.
     * <p>
     * //todo Wenn man den ersten Worker schedulen kann diese Methodik entfernen
     */
    private void setNewWorker() {
        OneTimeWorkRequest workRequest = new OneTimeWorkRequest
                .Builder(NotificationWorker.class)
                .setInitialDelay(24, TimeUnit.HOURS)
                .build();

        saveWorkerId(workRequest.getId().toString());

        WorkManager.getInstance().enqueue(workRequest);

        Log.i(NotificationWorker.class.getSimpleName(), "Scheduling next worker");
    }

    private void saveWorkerId(String id) {

        AppInternalPreferences preferences = new AppInternalPreferences(getApplicationContext());
        preferences.setNotificationJobId(id);
    }
}
