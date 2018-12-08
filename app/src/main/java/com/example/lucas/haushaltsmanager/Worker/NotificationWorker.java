package com.example.lucas.haushaltsmanager.Worker;

import android.app.Notification;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v4.app.NotificationManagerCompat;

import com.example.lucas.haushaltsmanager.Activities.MainTab.ParentActivity;
import com.example.lucas.haushaltsmanager.PreferencesHelper.AppInternalPreferences;
import com.example.lucas.haushaltsmanager.R;

import androidx.work.WorkerParameters;

public class NotificationWorker extends AbstractWorker {
    public static final String WORKER_ID = "notificationWorker";

    public NotificationWorker(@NonNull Context context, @NonNull WorkerParameters workerParams) {
        super(context, workerParams);
    }

    @NonNull
    @Override
    public Result doWork() {
        PendingIntent pi = PendingIntent.getActivity(getApplicationContext(), 0, new Intent(getApplicationContext(), ParentActivity.class), 0);

        Notification notification = new Notification.Builder(getApplicationContext())
                .setContentTitle(getApplicationContext().getString(R.string.remind_notification_title))
                .setContentText(getApplicationContext().getString(R.string.remind_notification_body))
                .setAutoCancel(true)
                .setContentIntent(pi)
                .setSmallIcon(R.mipmap.ic_launcher)
                .build();

        NotificationManagerCompat.from(getApplicationContext()).notify(getNotificationId(), notification);

        scheduleNewWorker();

        return Result.SUCCESS;
    }

    @Override
    void saveWorkerId(String workerId) {
        new AppInternalPreferences(getApplicationContext()).setNotificationJobId(workerId);
    }
}
