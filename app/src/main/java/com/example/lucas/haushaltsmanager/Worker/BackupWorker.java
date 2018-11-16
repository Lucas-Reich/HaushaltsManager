package com.example.lucas.haushaltsmanager.Worker;

import android.content.Context;
import android.support.annotation.NonNull;
import android.util.Log;

import com.example.lucas.haushaltsmanager.BackupHandler;
import com.example.lucas.haushaltsmanager.Entities.Directory;
import com.example.lucas.haushaltsmanager.PreferencesHelper.AppInternalPreferences;
import com.example.lucas.haushaltsmanager.PreferencesHelper.UserSettingsPreferences;

import java.util.concurrent.TimeUnit;

import androidx.work.OneTimeWorkRequest;
import androidx.work.WorkManager;
import androidx.work.Worker;
import androidx.work.WorkerParameters;

public class BackupWorker extends Worker {
    public static final String WORKER_ID = "backupWorker";

    public BackupWorker(@NonNull Context context, @NonNull WorkerParameters workerParameters) {
        super(context, workerParameters);
    }

    @NonNull
    @Override
    public Result doWork() {
        if (!isEnabled())
            return Result.SUCCESS;

        BackupHandler backupHandler = new BackupHandler();
        boolean successful = backupHandler.createBackup(null, null, getApplicationContext());
        backupHandler.deleteBackupsAboveThreshold(getBackupDir(), getBackupThreshold());

        scheduleNextWorker();

        return successful ? Result.SUCCESS : Result.FAILURE;
    }

    private Directory getBackupDir() {
        return new AppInternalPreferences(getApplicationContext()).getBackupDirectory();
    }

    private int getBackupThreshold() {
        return new UserSettingsPreferences(getApplicationContext()).getMaxBackupCount();
    }

    /**
     * Methode um einen neuen NotificationWorker zu starten.
     * Da man mit dem aktuellen WorkManager noch keine Wiederkehrenden Worker erstellen kann,
     * bei denen der erste Worker zu einer bestimmten Zeit ausgelöst wird,ist das hier der Workaround.
     */
    private void scheduleNextWorker() {
        // CLEANUP: Wenn man den ersten Worker schedulen kann diese Methodik entfernen
        OneTimeWorkRequest workRequest = new OneTimeWorkRequest
                .Builder(BackupWorker.class)
                .setInitialDelay(24, TimeUnit.HOURS)
                .build();

        saveWorkerId(workRequest.getId().toString());

        WorkManager.getInstance().enqueue(workRequest);

        Log.i(BackupWorker.class.getSimpleName(), "Scheduling next BackupJob");
    }

    private void saveWorkerId(String id) {

        AppInternalPreferences preferences = new AppInternalPreferences(getApplicationContext());
        preferences.setBackupJobId(id);
    }

    private boolean isEnabled() {
        UserSettingsPreferences userSettingsPreferences = new UserSettingsPreferences(getApplicationContext());
        return userSettingsPreferences.getAutomaticBackupStatus();
    }
}
