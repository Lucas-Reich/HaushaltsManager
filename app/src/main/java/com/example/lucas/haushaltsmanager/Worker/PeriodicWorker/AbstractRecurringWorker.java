package com.example.lucas.haushaltsmanager.Worker.PeriodicWorker;

import android.content.Context;
import androidx.annotation.NonNull;
import android.util.Log;

import com.example.lucas.haushaltsmanager.Worker.WorkRequestBuilder;
import com.google.common.util.concurrent.ListenableFuture;

import java.util.List;

import androidx.work.WorkInfo;
import androidx.work.WorkManager;
import androidx.work.WorkRequest;
import androidx.work.Worker;
import androidx.work.WorkerParameters;

abstract class AbstractRecurringWorker extends Worker {
    private static final String TAG = AbstractRecurringWorker.class.getSimpleName();

    AbstractRecurringWorker(@NonNull Context context, @NonNull WorkerParameters workerParams) {
        super(context, workerParams);
    }

    static boolean isWorkerScheduled(String tag) {
        ListenableFuture<List<WorkInfo>> statuses = WorkManager
                .getInstance()
                .getWorkInfosByTag(tag);

        try {
            boolean running = false;
            List<WorkInfo> workInfoList = statuses.get();
            for (WorkInfo workInfo : workInfoList) {
                WorkInfo.State state = workInfo.getState();
                running = state == WorkInfo.State.RUNNING | state == WorkInfo.State.ENQUEUED;
            }

            return running;
        } catch (Exception e) {
            e.printStackTrace();
        }

        return false;
    }

    final void scheduleNextWorker(Object object) {
        WorkRequest request = WorkRequestBuilder.from(object);
        WorkManager.getInstance(getApplicationContext()).enqueue(request);

        Log.i(TAG, String.format("Scheduled new '%s' Worker", getTag()));
    }

    abstract String getTag();
}
