package com.example.lucas.haushaltsmanager.Worker;

import android.content.Context;
import android.support.annotation.NonNull;
import android.util.Log;

import java.util.Random;
import java.util.concurrent.TimeUnit;

import androidx.work.OneTimeWorkRequest;
import androidx.work.WorkManager;
import androidx.work.Worker;
import androidx.work.WorkerParameters;

abstract class AbstractWorker extends Worker {

    AbstractWorker(@NonNull Context context, @NonNull WorkerParameters workerParams) {
        super(context, workerParams);
    }

    protected int getNotificationId() {
        return new Random().nextInt();
    }

    /**
     * Methode um einen neuen NotificationWorker zu starten.
     * Da man mit dem aktuellen WorkManager noch keine Wiederkehrenden Worker erstellen kann,
     * bei denen der erste Worker zu einer bestimmten Zeit ausgelöst wird,ist das hier der Workaround.
     */
    protected void scheduleNewWorker() {
        // REFACTOR: Wenn man den ersten Worker schedulen kann diese Methodik entfernen
        OneTimeWorkRequest workRequest = new OneTimeWorkRequest
                .Builder(this.getClass())
                .setInitialDelay(1, TimeUnit.MINUTES)
                .build();

        saveWorkerId(workRequest.getId().toString());

        WorkManager.getInstance().enqueue(workRequest);

        Log.i(this.getClass().getSimpleName(), "Scheduling next Job");
    }

    abstract void saveWorkerId(String workerId);
}
